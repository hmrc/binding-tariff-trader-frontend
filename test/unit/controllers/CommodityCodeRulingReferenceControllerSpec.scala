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

import play.api.data.Form
import play.api.libs.json.JsString
import uk.gov.hmrc.http.cache.client.CacheMap
import navigation.FakeNavigator
import connectors.FakeDataCacheConnector
import controllers.actions._
import play.api.test.Helpers._
import forms.CommodityCodeRulingReferenceFormProvider
import models.NormalMode
import pages.CommodityCodeRulingReferencePage
import play.api.mvc.Call
import views.html.commodityCodeRulingReference

class CommodityCodeRulingReferenceControllerSpec extends ControllerSpecBase {

  private def onwardRoute = Call("GET", "/foo")

  private val formProvider = new CommodityCodeRulingReferenceFormProvider()
  private val form = formProvider()

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new CommodityCodeRulingReferenceController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc
    )

  def viewAsString(form: Form[_] = form): String = commodityCodeRulingReference(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  val testAnswer = "answer"

  "CommodityCodeRulingReference Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(CommodityCodeRulingReferencePage.toString -> JsString(testAnswer))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) shouldBe viewAsString(form.fill(testAnswer))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testAnswer))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) shouldBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testAnswer))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
