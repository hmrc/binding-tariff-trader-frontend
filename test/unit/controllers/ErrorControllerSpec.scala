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

package unit.controllers

import controllers.{ControllerSpecBase, ErrorController}
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout}
import views.html.error_template

class ErrorControllerSpec extends ControllerSpecBase {

  lazy val errorView: error_template   = app.injector.instanceOf[error_template]
  lazy val controller: ErrorController = app.injector.instanceOf[ErrorController]

  "onPageLoad" should {
    "display the standard error template page" in {
      val expectedTitle   = messages("global.error.InternalServerError500.title")
      val expectedHeading = messages("global.error.InternalServerError500.heading")
      val expectedMessage = messages("global.error.InternalServerError500.message")

      val result = controller.onPageLoad(fakeRequest)
      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) shouldBe errorView(expectedTitle, expectedHeading, expectedMessage, appConfig)(
        fakeRequest,
        messages
      ).toString()
    }
  }
}
