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

package unit.controllers

import connectors.FakeDataCacheConnector
import controllers.actions._
import controllers.{ControllerSpecBase, ProvideConfidentialInformationController, routes}
import forms.ProvideConfidentialInformationFormProvider
import models.NormalMode
import navigation.FakeNavigator
import pages.{ProvideConfidentialInformationPage, ProvideGoodsNamePage}
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.provideConfidentialInformation

import scala.concurrent.ExecutionContext.Implicits.global

class ProvideConfidentialInformationControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  val provideConfidentialInformation = injector.instanceOf[provideConfidentialInformation]

  val fakeGETRequest = fakeGETRequestWithCSRF
  val fakePOSTRequest = fakePOSTRequestWithCSRF

  val formProvider = new ProvideConfidentialInformationFormProvider()
  val form = formProvider()
  val goodsName = "shoos"

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ProvideConfidentialInformationController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      provideConfidentialInformation,
      cc)

  def viewAsString(form: Form[_] = form) = provideConfidentialInformation(
    frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages).toString

  val testAnswer = "answer"

  "ProvideConfidentialInformation Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeGETRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(
        ProvideGoodsNamePage.toString -> JsString(goodsName),
        ProvideConfidentialInformationPage.toString -> JsString(testAnswer)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeGETRequest)

      contentAsString(result) shouldBe viewAsString(form.fill(testAnswer))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakePOSTRequest.withFormUrlEncodedBody(("confidentialInformation", testAnswer))
      val validData = Map(ProvideConfidentialInformationPage.toString -> JsString(testAnswer))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeGETRequest.withFormUrlEncodedBody(("confidentialInformation", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val validData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) shouldBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeGETRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakePOSTRequest.withFormUrlEncodedBody(("confidentialInformation", testAnswer))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
