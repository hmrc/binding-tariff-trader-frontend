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
import views.html.{acceptItemInformationList, beforeYouStart}

class AcceptItemInformationListControllerSpec extends ControllerSpecBase {

  private val view: acceptItemInformationList = app.injector.instanceOf[acceptItemInformationList]

  private def onwardRoute = Call("GET", "/foo")

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new AcceptItemInformationListController(frontendAppConfig,  messagesApi,  new FakeNavigator(onwardRoute), FakeIdentifierAction,
      dataRetrievalAction, new DataRequiredActionImpl, messagesControllerComponents,view)

  def viewAsString() = view()(messages, fakeRequest).toString

  "AcceptItemInformationList Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}
