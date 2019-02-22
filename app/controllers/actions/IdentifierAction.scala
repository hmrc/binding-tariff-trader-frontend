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
import play.api.mvc.Results._
import play.api.mvc.{ActionBuilder, ActionFunction, Request, Result}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.AuthProvider.GovernmentGateway
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest] with ActionFunction[Request, IdentifierRequest] {

  protected lazy val cdsEnrolment = "HMRC-CUS-ORG"

}

class AuthenticatedIdentifierAction @Inject()(override val authConnector: AuthConnector, config: FrontendAppConfig)
                                             (implicit ec: ExecutionContext) extends IdentifierAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    def redirectToUnauthorised = {
      Redirect(routes.UnauthorisedController.onPageLoad())
    }

    // TODO: remove fake EORI number when in public beta
    def eoriNumber(enrolments: Enrolments): String = {
      enrolments.getEnrolment(cdsEnrolment).flatMap(_.getIdentifier("EORINumber")) match {
        case Some(eori) => eori.value
        case _ if config.isCdsEnrolmentCheckEnabled => throw InsufficientEnrolments()
        case _ => "GB6723190" // fake EORI number
      }
    }

    authorise().retrieve(Retrievals.internalId and Retrievals.allEnrolments) {
      case ~(Some(internalId: String), allEnrolments: Enrolments) => block(IdentifierRequest(request, internalId, eoriNumber(allEnrolments)))
      case _ => throw new UnauthorizedException("Unable to retrieve internal Id")
    } recover {
      case _: NoActiveSession => Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case _: InsufficientEnrolments |
           _: InsufficientConfidenceLevel |
           _: UnsupportedAuthProvider |
           _: UnsupportedAffinityGroup |
           _: UnsupportedCredentialRole => redirectToUnauthorised
    }
  }

  private def authorise(): AuthorisedFunction = {
    if (config.isCdsEnrolmentCheckEnabled) {
      authorised(Enrolment(cdsEnrolment) and AuthProviders(GovernmentGateway))
    } else {
      authorised(AuthProviders(GovernmentGateway))
    }
  }

}
