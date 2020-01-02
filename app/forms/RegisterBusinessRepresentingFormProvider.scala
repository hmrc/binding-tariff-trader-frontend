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

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.RegisterBusinessRepresenting
import play.api.data.Form
import play.api.data.Forms._

class RegisterBusinessRepresentingFormProvider @Inject() extends Mappings {

  private val businessMaxLength = 100
  private val addressMaxLength = 70
  private val townMaxLength = 35
  private val postCodeMaxLength = 9
  private val countryMaxLength = 60

  def apply(): Form[RegisterBusinessRepresenting] = Form(
    mapping(
      "eoriNumber" ->   text("registerBusinessRepresenting.error.eoriNumber.required")
        .verifying(FormConstraints.eoriCodeConstraint),
      "businessName" -> text("registerBusinessRepresenting.error.businessName.required")
        .verifying(maxLength(businessMaxLength, "registerBusinessRepresenting.error.businessName.length")),
      "addressLine1" -> text("registerBusinessRepresenting.error.addressLine1.required")
        .verifying(maxLength(addressMaxLength, "registerBusinessRepresenting.error.addressLine1.length")),
      "town" -> text("registerBusinessRepresenting.error.town.required")
        .verifying(maxLength(townMaxLength, "registerBusinessRepresenting.error.town.length")),
      "postCode" -> text("registerBusinessRepresenting.error.postCode.required")
        .verifying(maxLength(postCodeMaxLength, "registerBusinessRepresenting.error.postCode.length")),
      "country" -> text("registerBusinessRepresenting.error.country.required")
        .verifying(maxLength(countryMaxLength, "registerBusinessRepresenting.error.country.length"))
    )(RegisterBusinessRepresenting.apply)(RegisterBusinessRepresenting.unapply)
  )

}
