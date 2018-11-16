/*
 * Copyright 2018 HM Revenue & Customs
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

import navigation.FakeNavigator
import connectors.FakeDataCacheConnector
import controllers.actions._
import play.api.test.Helpers._
import models.NormalMode
import pages.DeclarationPage
import play.api.libs.json.JsString
import play.api.mvc.Call
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.declaration

class DeclarationControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new DeclarationController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(onwardRoute), FakeIdentifierAction,
      dataRetrievalAction)

  def viewAsString = declaration(frontendAppConfig, NormalMode)(fakeRequest, messages).toString

  val testAnswer = "answer"

  "Declaration Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(DeclarationPage.toString -> JsString(testAnswer))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testAnswer))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }
  }

}
