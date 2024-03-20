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

package controllers

import config.FrontendAppConfig
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.Future

class ErrorController @Inject() (
  errorTemplateView: views.html.error_template,
  cc: MessagesControllerComponents,
  appConfig: FrontendAppConfig
) extends FrontendController(cc)
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(InternalServerError(internalServerErrorTemplate).withHeaders(CACHE_CONTROL -> "no-cache"))
  }

  private def internalServerErrorTemplate(implicit request: Request[_]): Html =
    errorTemplateView(
      Messages("global.error.InternalServerError500.title"),
      Messages("global.error.InternalServerError500.heading"),
      Messages("global.error.InternalServerError500.message"),
      appConfig
    )
}
