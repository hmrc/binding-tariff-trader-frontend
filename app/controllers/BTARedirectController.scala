/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers

import config.FrontendAppConfig
import controllers.actions.IdentifierAction
import play.api.Logging
import play.api.mvc._
import service.BTAUserService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class BTARedirectController @Inject() (
  identify: IdentifierAction,
  cc: MessagesControllerComponents,
  btaUserService: BTAUserService,
  appConfig: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with Logging {

  def applicationsAndRulings: Action[AnyContent] = identify.async { implicit request =>
    performInternalRedirect(
      request.identifier,
      controllers.routes.IndexController.getApplicationsAndRulings(sortBy = None, order = None)
    )
  }

  def informationYouNeed: Action[AnyContent] = identify.async { implicit request =>
    performInternalRedirect(request.identifier, controllers.routes.BeforeYouStartController.onPageLoad())
  }

  def redirectToBTA: Action[AnyContent] = identify.async { implicit request =>
    btaUserService.remove(request.identifier) transformWith {
      case Success(_) => Future.successful(Redirect(appConfig.businessTaxAccountUrl))
      case Failure(error) =>
        logger.error("[BTARedirectController][redirectToBTA] An error occurred whilst removing the BTA User", error)
        Future.successful(Redirect(appConfig.businessTaxAccountUrl))
    }
  }

  private def performInternalRedirect(requestIdentifier: String, call: Call): Future[Result] =
    btaUserService.save(requestIdentifier) transformWith {
      case Success(_) => Future.successful(Redirect(call))
      case Failure(error) =>
        logger.error("[BTARedirectController][performInternalRedirect] An error occurred whilst saving BTA User", error)
        Future.successful(Redirect(routes.ErrorController.onPageLoad))
    }
}
