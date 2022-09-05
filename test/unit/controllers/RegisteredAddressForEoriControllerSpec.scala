/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.RegisteredAddressForEoriFormProvider
import models.{NormalMode, RegisteredAddressForEori}
import navigation.FakeNavigator
import pages.RegisteredAddressForEoriPage
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.Helpers._
import service.CountriesService
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.registeredAddressForEori

import scala.concurrent.ExecutionContext.Implicits.global

class RegisteredAddressForEoriControllerSpec extends ControllerSpecBase {

  private lazy val formProvider: RegisteredAddressForEoriFormProvider = new RegisteredAddressForEoriFormProvider()
  private lazy val form: Form[RegisteredAddressForEori]               = formProvider()
  private val countriesService                                        = new CountriesService()

  val registeredAddressForEoriView: registeredAddressForEori =
    app.injector.instanceOf(classOf[registeredAddressForEori])

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new RegisteredAddressForEoriController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      countriesService,
      cc,
      registeredAddressForEoriView
    )

  private def onwardRoute = Call("GET", "/foo")

  private def viewAsString(
    form: Form[RegisteredAddressForEori] = form.fill(RegisteredAddressForEori(fakeRequestWithEori.userEoriNumber.get))
  ): String =
    registeredAddressForEoriView(frontendAppConfig, form, NormalMode, countriesService.getAllCountries)(
      fakeRequestWithEori,
      messages
    ).toString

  "RegisteredAddressForEori Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(
        RegisteredAddressForEoriPage.toString -> Json.toJson(
          RegisteredAddressForEori(
            fakeRequestWithEori.userEoriNumber.get,
            "businessName",
            "addressLine1",
            "townOrCity",
            Some("postcode"),
            "country"
          )
        )
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) shouldBe viewAsString(
        form.fill(
          RegisteredAddressForEori(
            fakeRequestWithEori.userEoriNumber.get,
            "businessName",
            "addressLine1",
            "townOrCity",
            Some("postcode"),
            "country"
          )
        )
      )
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(
        ("eori", "GB123"),
        ("businessName", "value 1"),
        ("addressLine1", "value 3"),
        ("townOrCity", "value 4"),
        ("postcode", "value 5"),
        ("country", "value 6")
      )

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm   = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result)          shouldBe BAD_REQUEST
      contentAsString(result) shouldBe viewAsString(boundForm)
    }

    val gbCountryCombinations = Seq("GB", "gb", "Gb", "gB", " gb", "gb ", " gb ")

    gbCountryCombinations.foreach { country =>
      s"return a Bad Request and errors when invalid gb postcode with country passed like '$country' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(
          ("eori", "GB123"),
          ("businessName", "value 1"),
          ("addressLine1", "value 3"),
          ("townOrCity", "value 4"),
          ("postcode", "value 5"),
          ("country", country)
        )

        val boundForm = form.bind(
          Map(
            "eori"         -> "GB123",
            "businessName" -> "value 1",
            "addressLine1" -> "value 3",
            "townOrCity"   -> "value 4",
            "postcode"     -> "value 5",
            "country"      -> country
          )
        )

        val result = controller().onSubmit(NormalMode)(postRequest)

        status(result)          shouldBe BAD_REQUEST
        contentAsString(result) shouldBe viewAsString(boundForm)
      }
    }
  }

}
