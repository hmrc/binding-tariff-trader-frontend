/*
 * Copyright 2018 HM Revenue & Customs
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

  def apply(): Form[RegisterBusinessRepresenting] = Form(
    mapping(
      "eoriNumber" -> text("registerBusinessRepresenting.error.eoriNumber.required")
        .verifying(maxLength(100, "registerBusinessRepresenting.error.eoriNumber.length")),
      "businessName" -> text("registerBusinessRepresenting.error.businessName.required")
        .verifying(maxLength(100, "registerBusinessRepresenting.error.businessName.length")),
      "addressLine1" -> text("registerBusinessRepresenting.error.addressLine1.required")
        .verifying(maxLength(100, "registerBusinessRepresenting.error.addressLine1.length")),
      "town" -> text("registerBusinessRepresenting.error.town.required")
        .verifying(maxLength(100, "registerBusinessRepresenting.error.town.length")),
      "postCode" -> text("registerBusinessRepresenting.error.postCode.required")
        .verifying(maxLength(100, "registerBusinessRepresenting.error.postCode.length")),
      "country" -> text("registerBusinessRepresenting.error.country.required")
        .verifying(maxLength(100, "registerBusinessRepresenting.error.country.length"))
    )(RegisterBusinessRepresenting.apply)(RegisterBusinessRepresenting.unapply)
  )

}
