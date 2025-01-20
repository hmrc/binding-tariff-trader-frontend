/*
 * Copyright 2025 HM Revenue & Customs
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

import controllers.actions.{FakeDataRetrievalAction, FakeIdentifierAction, IdentifierAction}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, _}
import views.html.informationPublic

class InformationPublicControllerSpec extends ControllerSpecBase {

  val informationPublicView: informationPublic = app.injector.instanceOf(classOf[informationPublic])

  private def controller(identifier: IdentifierAction) =
    new InformationPublicController(
      frontendAppConfig,
      identifier,
      new FakeDataRetrievalAction(None),
      cc,
      informationPublicView
    )

  val fakeGETRequest: FakeRequest[AnyContentAsEmpty.type] = fakeGETRequestWithCSRF

  private def viewAsString() = informationPublicView(frontendAppConfig)(fakeGETRequest, messages).toString

  "InformationPublic Controller" must {

    "return OK and the correct view for a GET" in {

      val result = controller(identifier = FakeIdentifierAction(None)).onPageLoad(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

  }

}
