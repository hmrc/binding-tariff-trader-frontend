/*
 * Copyright 2019 HM Revenue & Customs
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
import controllers.routes
import models.requests.IdentifierRequest
import play.api.mvc._
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionBuilder, ActionFunction, AnyContent, Request, Result}
import uk.gov.hmrc.auth.core.AuthProvider.GovernmentGateway
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.HeaderCarrierConverter
import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]{
  protected lazy val cdsEnrolment = "HMRC-CUS-ORG"
}

class AuthenticatedIdentifierAction @Inject()(override val authConnector: AuthConnector,
                                              config: FrontendAppConfig, cc: MessagesControllerComponents)
                                             (implicit val ec: ExecutionContext) extends IdentifierAction with AuthorisedFunctions {




  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authorise().retrieve(Retrievals.internalId and Retrievals.allEnrolments) {
      case ~(Some(internalId: String), allEnrolments: Enrolments) => eori(allEnrolments) match {
        case Some(eori: String) => block(IdentifierRequest(request, internalId, Some(eori)))
        case None if !config.isCdsEnrolmentCheckEnabled => block(IdentifierRequest(request, internalId, None))
        case _ => throw InsufficientEnrolments()
      }

      case _ =>
        throw new UnauthorizedException("Unable to retrieve internal Id")

    } recover {
      case _: NoActiveSession => Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case _: InsufficientEnrolments |
           _: InsufficientConfidenceLevel |
           _: UnsupportedAuthProvider |
           _: UnsupportedAffinityGroup |
           _: UnsupportedCredentialRole => redirectToUnauthorised
    }
  }

  def redirectToUnauthorised: Result = Redirect(routes.UnauthorisedController.onPageLoad())

  def eori(enrolments: Enrolments): Option[String] = enrolments.getEnrolment(cdsEnrolment).flatMap(_.getIdentifier("EORINumber")).map(_.value)

  private def authorise(): AuthorisedFunction = if (config.isCdsEnrolmentCheckEnabled) {
    authorised(Enrolment(cdsEnrolment) and AuthProviders(GovernmentGateway))
  } else {
    authorised(AuthProviders(GovernmentGateway))
  }

  override def parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser

  override protected def executionContext: ExecutionContext = cc.executionContext

}
