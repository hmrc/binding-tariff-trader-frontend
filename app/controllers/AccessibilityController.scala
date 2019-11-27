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

package controllers

import java.net.URI

import config.FrontendAppConfig
import javax.inject.Inject
import views.html.accessibilityStatementView
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

class AccessibilityController  @Inject()(
                                        val frontendAppConfig: FrontendAppConfig,
                                        cc: MessagesControllerComponents,
                                        view: accessibilityStatementView) extends FrontendController(cc) {


  def onPageLoad: Action[AnyContent] = Action { implicit request =>
    val refererPage = new URI(request.headers.get("referer").getOrElse(frontendAppConfig.reportAccessibilityProblemDefaultUrl)).getPath
    Ok(view(refererPage))
  }


}