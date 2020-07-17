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

package views

import controllers.routes
import forms.RegisteredAddressForEoriFormProvider
import models.{NormalMode, RegisteredAddressForEori}
import play.api.data.Form
import service.CountriesService
import views.behaviours.QuestionViewBehaviours
import views.html.registeredAddressForEori

class RegisteredAddressForEoriViewSpec extends QuestionViewBehaviours[RegisteredAddressForEori] {

  private val messageKeyPrefix = "registeredAddressForEori"

  private val countriesService = new CountriesService()

  override protected val form = new RegisteredAddressForEoriFormProvider()()



  private def createView = { () =>
    registeredAddressForEori(frontendAppConfig, form, NormalMode, countriesService.getAllCountries)(fakeRequestWithNotOptionalEoriAndCache, messages)
  }

  private def createViewUsingForm = { form: Form[_] =>
    registeredAddressForEori(frontendAppConfig, form, NormalMode, countriesService.getAllCountries)(fakeRequestWithNotOptionalEoriAndCache, messages)
  }

  "RegisteredAddressForEori view" must {

    behave like normalPage(createView, messageKeyPrefix, "eori-789012")()

    behave like pageWithoutBackLink(createView)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      routes.RegisteredAddressForEoriController.onSubmit(NormalMode).url,
      "businessName", "addressLine1", "townOrCity", "postcode", "country"
    )

    "show the expected text" in {
      val text = asDocument(createViewUsingForm(form)).text()
      text should include("What is the registered name and address for this EORI number? - Apply for a Binding Tariff Information ruling - GOV.UK")
      text should include("What is the registered name and address for EORI number eori-789012?")
      text should include("The details you enter must match the exact format of the registered EORI address")
      text should include("Business, organisation or individual’s name")
      text should include("Address line 1")
      text should include("Town or city")
      text should include("Postcode")
      text should include("Country")
    }
  }

}
