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
import forms.RegisterBusinessRepresentingFormProvider
import models.{NormalMode, RegisterBusinessRepresenting}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import service.CountriesService
import views.behaviours.QuestionViewBehaviours
import views.html.registerBusinessRepresenting

class RegisterBusinessRepresentingViewSpec extends QuestionViewBehaviours[RegisterBusinessRepresenting] {

  private val messageKeyPrefix = "registerBusinessRepresenting"

  override protected val form: Form[RegisterBusinessRepresenting] = new RegisterBusinessRepresentingFormProvider()()

  val countriesService = new CountriesService

  private def createView: () => HtmlFormat.Appendable = () => registerBusinessRepresenting(frontendAppConfig, form, NormalMode, countriesService.getAllCountries)(fakeRequest, messages)

  private def createViewUsingForm: Form[RegisterBusinessRepresenting] => HtmlFormat.Appendable = (form: Form[RegisterBusinessRepresenting]) => registerBusinessRepresenting(frontendAppConfig, form, NormalMode, countriesService.getAllCountries)(fakeRequest, messages)


  "RegisterBusinessRepresenting view" must {

    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      routes.RegisterBusinessRepresentingController.onSubmit(NormalMode).url,
      "eoriNumber", "businessName", "addressLine1", "town", "postCode", "country"
    )
  }
}