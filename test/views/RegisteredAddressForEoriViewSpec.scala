/*
 * Copyright 2023 HM Revenue & Customs
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

  private val messageKeyPrefix: String = "registeredAddressForEori"

  private val countriesService: CountriesService = new CountriesService()

  override protected val form: Form[RegisteredAddressForEori] = new RegisteredAddressForEoriFormProvider()()

  val registeredAddressForEoriView: registeredAddressForEori = app.injector.instanceOf[registeredAddressForEori]

  private def createView = { () =>
    val request      = fakeRequestWithEori
    val preparedForm = form.fill(RegisteredAddressForEori(request.userEoriNumber.get))
    registeredAddressForEoriView(frontendAppConfig, preparedForm, NormalMode, countriesService.getAllCountries)(
      request,
      messages
    )
  }

  private def createViewUsingForm = { form: Form[_] =>
    registeredAddressForEoriView(frontendAppConfig, form, NormalMode, countriesService.getAllCountries)(
      fakeRequestWithEori,
      messages
    )
  }

  "RegisteredAddressForEori view" must {

    behave like normalPage(createView, messageKeyPrefix, "eori-789012")()

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      "businessName",
      "addressLine1",
      "townOrCity",
      "postcode",
      "country"
    )

    "show the expected text" in {
      val request      = fakeRequestWithEori
      val preparedForm = form.fill(RegisteredAddressForEori(request.userEoriNumber.get))
      val text         = asDocument(createViewUsingForm(preparedForm)).text()
      text should include(messages("registeredAddressForEori.title"))
      text should include(messages("registeredAddressForEori.heading", "eori-789012"))
      text should include(messages("registeredAddressForEori.note"))
      text should include(messages("registeredAddressForEori.businessName"))
      text should include(messages("registeredAddressForEori.addressLine1"))
      text should include(messages("registeredAddressForEori.townCity"))
      text should include(messages("registeredAddressForEori.postcode"))
      text should include(messages("registeredAddressForEori.country"))
    }
  }

}
