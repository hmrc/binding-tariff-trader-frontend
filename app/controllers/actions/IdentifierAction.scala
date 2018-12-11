/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.mvc.{ActionBuilder, ActionFunction, Request, Result}
import play.api.mvc.Results._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import config.FrontendAppConfig
import controllers.routes
import models.requests.IdentifierRequest
import uk.gov.hmrc.http.UnauthorizedException
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedIdentifierAction @Inject()(override val authConnector: AuthConnector, config: FrontendAppConfig)
                                             (implicit ec: ExecutionContext) extends IdentifierAction with AuthorisedFunctions {


  lazy val requiredEnrolment = Enrolment(config.authEnrolment)

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authorised(requiredEnrolment).retrieve(Retrievals.internalId) {
      _.map {
        internalId => block(IdentifierRequest(request, internalId))
      }.getOrElse(throw new UnauthorizedException("Unable to retrieve internal Id"))
    } recover {
      case _: NoActiveSession =>
        Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case _: InsufficientEnrolments =>
        Redirect(routes.UnauthorisedController.onPageLoad())
      case _: InsufficientConfidenceLevel =>
        Redirect(routes.UnauthorisedController.onPageLoad())
      case _: UnsupportedAuthProvider =>
        Redirect(routes.UnauthorisedController.onPageLoad())
      case _: UnsupportedAffinityGroup =>
        Redirect(routes.UnauthorisedController.onPageLoad())
      case _: UnsupportedCredentialRole =>
        Redirect(routes.UnauthorisedController.onPageLoad())
    }
  }

}

trait IdentifierAction extends ActionBuilder[IdentifierRequest] with ActionFunction[Request, IdentifierRequest]

class SessionIdentifierAction @Inject()(config: FrontendAppConfig)
                                       (implicit ec: ExecutionContext) extends IdentifierAction {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    hc.sessionId match {
      case Some(session) => block(IdentifierRequest(request, session.value))
      case _ => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
  }

}
