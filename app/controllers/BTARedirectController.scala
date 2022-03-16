/*
 * Copyright 2022 HM Revenue & Customs
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

import controllers.actions.IdentifierAction
import play.api.Logging
import play.api.mvc._
import service.BTAUserService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

class BTARedirectController @Inject()(
                                       identify: IdentifierAction,
                                       cc: MessagesControllerComponents,
                                       btaUserService: BTAUserService)(implicit ec: ExecutionContext) extends FrontendController(cc) with Logging {

  def bta: Action[AnyContent] = identify.async { implicit request =>
    btaUserService.save(request.identifier) transformWith {
        case Success(_) => Future.successful(Redirect(controllers.routes.IndexController.getApplicationsAndRulings(sortBy = None, order = None)))
        case Failure(NonFatal(error)) =>
          logger.error("An error occurred whilst saving BTA User", error)
          Future.successful(Redirect(routes.ErrorController.onPageLoad()))
    }
  }
}