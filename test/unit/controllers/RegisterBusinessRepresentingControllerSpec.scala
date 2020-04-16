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

import connectors.FakeDataCacheConnector
import controllers.actions._
import forms.RegisterBusinessRepresentingFormProvider
import models.{NormalMode, RegisterBusinessRepresenting}
import navigation.FakeNavigator
import pages.RegisterBusinessRepresentingPage
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.Helpers._
import service.CountriesService
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.registerBusinessRepresenting

class RegisterBusinessRepresentingControllerSpec extends ControllerSpecBase {

  val countriesService = new CountriesService
  private val formProvider = new RegisterBusinessRepresentingFormProvider()
  private val form: Form[RegisterBusinessRepresenting] = formProvider()

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new RegisterBusinessRepresentingController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      countriesService,
      cc
    )

  private def onwardRoute = Call("GET", "/foo")

  private def viewAsString(form: Form[RegisterBusinessRepresenting] = form) = registerBusinessRepresenting(frontendAppConfig, form, NormalMode, countriesService.getAllCountries)(fakeRequest, messages).toString

  "RegisterBusinessRepresenting Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) shouldBe OK
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(RegisterBusinessRepresentingPage.toString ->
        Json.toJson(RegisterBusinessRepresenting(
          "validEori123",
          "valid-business-name",
          "valid-address-line",
          "valid-town",
          "valid-post-code",
          "valid-country")
        )
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) shouldBe viewAsString(form.fill(RegisterBusinessRepresenting("validEori123", "valid-business-name", "valid-address-line", "valid-town", "valid-post-code", "valid-country")))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(
        ("eoriNumber", "validEori123"),
        ("businessName", "valid-business-name"),
        ("addressLine1", "valid-address-line"),
        ("town", "valid-town"),
        ("postCode", "valid-pc"),
        ("country", "valid-country")
      )

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

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
      val postRequest = fakeRequest.withFormUrlEncodedBody(
        ("eoriNumber", "validEori123"),
        ("businessName", "valid-business-name"),
        ("addressLine1", "valid-address-line"),
        ("town", "valid-town"),
        ("postCode", "valid-post-code"),
        ("country", "valid-country")
      )

      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
