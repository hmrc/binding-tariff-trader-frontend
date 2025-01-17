/*
 * Copyright 2025 HM Revenue & Customs
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

package forms

import forms.mappings.Mappings
import models.RegisteredAddressForEori
import play.api.data.Form
import play.api.data.Forms._

import javax.inject.Inject

class RegisteredAddressForEoriFormProvider @Inject() extends Mappings {

  private val maximumValueForAddress    = 70
  private val maximumValueForTownOrCity = 35

  def apply(): Form[RegisteredAddressForEori] = Form(
    mapping(
      "eori" -> text("registeredAddressForEori.error.eori.required")
        .verifying(FormConstraints.eoriCodeConstraint),
      "businessName" -> text("registeredAddressForEori.error.businessName.required"),
      "addressLine1" -> text("registeredAddressForEori.error.addressLine1.required")
        .verifying(maxLength(maximumValueForAddress, "registeredAddressForEori.error.addressLine1.length")),
      "townOrCity" -> text("registeredAddressForEori.error.townOrCity.required")
        .verifying(maxLength(maximumValueForTownOrCity, "registeredAddressForEori.error.townOrCity.length")),
      "postcode" -> postcodeText(
        "registeredAddressForEori.error.postcode.required",
        "registeredAddressForEori.error.postcode.gb"
      ).verifying(optionalPostCodeMaxLength("registeredAddressForEori.error.postcode.length")),
      "country" -> text("registeredAddressForEori.error.country.required")
    )(RegisteredAddressForEori.apply)(RegisteredAddressForEori.unapply)
  )

}
