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

import play.api.data.Form
import play.api.libs.json.JsString
import uk.gov.hmrc.http.cache.client.CacheMap
import navigation.FakeNavigator
import connectors.FakeDataCacheConnector
import controllers.actions._
import play.api.test.Helpers._
import forms.WhichBestDescribesYouFormProvider
import models.NormalMode
import models.WhichBestDescribesYou
import models.WhichBestDescribesYou.{BusinessOwner, BusinessRepresentative}
import pages.WhichBestDescribesYouPage
import play.api.mvc.Call
import views.html.whichBestDescribesYou
import scala.concurrent.ExecutionContext.Implicits.global

class WhichBestDescribesYouControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new WhichBestDescribesYouFormProvider()
  private val view = app.injector.instanceOf[whichBestDescribesYou]
  val form = formProvider()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new WhichBestDescribesYouController(FakeDataCacheConnector, new FakeNavigator(onwardRoute), FakeIdentifierAction(messagesControllerComponents),
      dataRetrievalAction, app.injector.instanceOf[DataRequiredActionImpl](messagesControllerComponents), formProvider, messagesControllerComponents, view)

  def viewAsString(form: Form[_] = form) = view(form, NormalMode)(messages, fakeRequest).toString

  "WhichBestDescribesYou Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(WhichBestDescribesYouPage.toString -> JsString(WhichBestDescribesYou.values.head.toString))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(form.fill(WhichBestDescribesYou.values.head))
    }

    "redirect to the next page when BusinessOwner is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", BusinessOwner))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect to the next page when BusinessRepresentative is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", BusinessRepresentative))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }


    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", WhichBestDescribesYou.options.head.value))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
