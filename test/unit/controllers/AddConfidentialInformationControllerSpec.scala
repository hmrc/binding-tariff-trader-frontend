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
import controllers.{AddConfidentialInformationController, ControllerSpecBase, routes}
import forms.AddConfidentialInformationFormProvider
import models.NormalMode
import navigation.FakeNavigator
import pages.{AddConfidentialInformationPage, ProvideGoodsNamePage}
import play.api.data.Form
import play.api.libs.json.{JsBoolean, JsString}
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global

class AddConfidentialInformationControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AddConfidentialInformationFormProvider()
  val form = formProvider()

  val addConfidentialInformationView = injector.instanceOf[views.html.addConfidentialInformation]

  val fakeGETRequest = fakeGETRequestWithCSRF
  val fakePOSTRequest = fakePOSTRequestWithCSRF

  val goodsName = "Mushrooms"

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new AddConfidentialInformationController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      addConfidentialInformationView,
      cc)

  def viewAsString(form: Form[_] = form) = addConfidentialInformationView(
    frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages).toString

  "AddConfidentialInformation Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
      val getRequiredData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRequiredData).onPageLoad(NormalMode)(fakeGETRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(AddConfidentialInformationPage.toString -> JsBoolean(true),
        ProvideGoodsNamePage.toString -> JsString(goodsName))

      val getRequiredData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRequiredData).onPageLoad(NormalMode)(fakeGETRequest)

      contentAsString(result) shouldBe viewAsString(form.fill(true))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakePOSTRequest.withFormUrlEncodedBody(("value", "true"))

      val validData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
      val getRequiredData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRequiredData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeGETRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val validData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
      val getRequiredData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRequiredData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) shouldBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeGETRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakePOSTRequest.withFormUrlEncodedBody(("value", "true"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
