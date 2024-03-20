/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.actions

import com.google.inject.Inject
import config.FrontendAppConfig
import models.requests.IdentifierRequest
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core.AuthProvider.GovernmentGateway
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction
    extends ActionBuilder[IdentifierRequest, AnyContent]
    with ActionFunction[Request, IdentifierRequest] {
  protected lazy val atarEnrolment = "HMRC-ATAR-ORG"
  protected lazy val EORINumber    = "EORINumber"
}

class AuthenticatedIdentifierAction @Inject() (
  override val authConnector: AuthConnector,
  cc: ControllerComponents,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends IdentifierAction
    with AuthorisedFunctions {

  override val parser: BodyParser[AnyContent]               = cc.parsers.defaultBodyParser
  override protected val executionContext: ExecutionContext = cc.executionContext

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authorise()
      .retrieve(Retrievals.internalId and Retrievals.allEnrolments) {
        case ~(Some(internalId: String), allEnrolments: Enrolments) =>
          eori(allEnrolments)
            .map(eori => block(IdentifierRequest(request, internalId, Some(eori))))
            .getOrElse(throw InsufficientEnrolments())

        case _ =>
          throw new UnauthorizedException("Unable to retrieve internal Id")

      }
      .recover {
        case _: NoActiveSession =>
          redirectToLogin
        case _: InsufficientEnrolments =>
          redirectToEoriComponent
      }
  }

  private def redirectToLogin: Result =
    Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))

  private def redirectToEoriComponent: Result =
    Redirect(config.eoriCommonComponentSubscribeUrl)

  def eori(enrolments: Enrolments): Option[String] =
    for {
      enrolment  <- enrolments.getEnrolment(atarEnrolment)
      identifier <- enrolment.getIdentifier(EORINumber)
    } yield identifier.value

  private def authorise(): AuthorisedFunction =
    authorised(Enrolment(atarEnrolment) and AuthProviders(GovernmentGateway))
}
