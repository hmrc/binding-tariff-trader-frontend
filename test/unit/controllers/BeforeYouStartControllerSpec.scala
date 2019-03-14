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

import controllers.actions._
import navigation.FakeNavigator
import play.api.mvc.Call
import play.api.test.Helpers._
import views.html.beforeYouStart

class BeforeYouStartControllerSpec extends ControllerSpecBase {

  private def onwardRoute = Call("GET", "/foo")

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new BeforeYouStartController(frontendAppConfig, messagesApi,  new FakeNavigator(onwardRoute), FakeIdentifierAction,
      dataRetrievalAction, new DataRequiredActionImpl)

  def viewAsString() = beforeYouStart(frontendAppConfig)(fakeRequest, messages).toString

  "BeforeYouStart Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted with POST" in {
      val result = controller().onSubmit()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

  }
}




