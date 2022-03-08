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
import models.requests.BTARequest
import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess, Reads}
import play.api.mvc._
import service.URLCacheService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

class BTARedirectController @Inject()(
  identify: IdentifierAction,
  cc: MessagesControllerComponents,
  urlCacheService: URLCacheService)(implicit ec: ExecutionContext) extends FrontendController(cc) with Logging {

  def bta: Action[AnyContent] = identify.async { implicit request =>
    withBTAJsonBody[BTARequest] { btaRequest: BTARequest =>
      urlCacheService.saveBTACallbackURL(request.identifier, btaRequest.url) transformWith {
        case Success(_) => Future.successful(Redirect(controllers.routes.IndexController.getApplicationsAndRulings(sortBy = None, order = None)))
        case Failure(NonFatal(_)) => {
          logger.error("Error occurred whilst saving BTA callback URL to the cache")
          Future.successful(Results.BadGateway)
        }
      }
    }
  }

  private def withBTAJsonBody[BTARequest](f: BTARequest => Future[Result])(implicit request: Request[AnyContent],
     m: Manifest[BTARequest], reads: Reads[BTARequest]): Future[Result] = {
      request.body.asJson.fold(Future.successful(BadRequest("Expecting application/json request body"))) { json =>
      json.validate[BTARequest] match {
        case JsSuccess(payload, _) => f(payload)
        case JsError(errs) => Future.successful(BadRequest(s"Invalid ${m.runtimeClass.getSimpleName} payload: $errs"))
      }
    }
  }
}
