/*
 * Copyright 2020 HM Revenue & Customs
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

import controllers.actions._
import models.requests.IdentifierRequest
import navigation.FakeNavigator
import play.api.mvc.Call
import play.api.test.Helpers._
import views.html.beforeYouStart

class BeforeYouStartControllerSpec extends ControllerSpecBase {

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap, identifier: IdentifierAction = FakeIdentifierAction) =
    new BeforeYouStartController(
      frontendAppConfig,
      messagesApi,
      new FakeNavigator(onwardRoute),
      identifier,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      cc
    )

  private def onwardRoute = Call("GET", "/foo")

  private def viewAsString(eori: Option[String]) = beforeYouStart(frontendAppConfig)(IdentifierRequest(fakeRequest, "id", eori), messages).toString

  "BeforeYouStart Controller" must {

    "return OK and the correct view for a GET - with EORI" in {
      val result = controller(identifier = FakeIdentifierAction(Some("eori"))).onPageLoad(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString(Some("eori"))
    }

    "return OK and the correct view for a GET - without EORI" in {
      val result = controller(identifier = FakeIdentifierAction(None)).onPageLoad(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString(None)
    }

  }
}




