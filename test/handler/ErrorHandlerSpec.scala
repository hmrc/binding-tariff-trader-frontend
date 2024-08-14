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

package handler

import base.SpecBase
import handlers.ErrorHandler
import play.api.i18n.MessagesApi
import views.html.error_template

import scala.concurrent.ExecutionContext.Implicits.global

class ErrorHandlerSpec extends SpecBase {

  private val messageApi: MessagesApi       = app.injector.instanceOf[MessagesApi]
  private val errorTemplate: error_template = app.injector.instanceOf[error_template]
  private val errorHandler: ErrorHandler    = new ErrorHandler(frontendAppConfig, messageApi, errorTemplate)

  "ErrorHandler" must {

    "return an error page" in {
      val result = errorHandler.standardErrorTemplate(
        pageTitle = "pageTitle",
        heading = "heading",
        message = "message"
      )(fakeRequest)

      result.body should include("pageTitle")
      result.body should include("heading")
      result.body should include("message")
    }
  }

}
