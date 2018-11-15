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

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.RegisteredAddressForEori

class RegisteredAddressForEoriFormProvider @Inject() extends Mappings {

   def apply(): Form[RegisteredAddressForEori] = Form(
     mapping(
      "field1" -> text("registeredAddressForEori.error.field1.required"),
      "field2" -> text("registeredAddressForEori.error.field2.required")
        .verifying(maxLength(70, "registeredAddressForEori.error.field2.length")),
      "field3" -> text("registeredAddressForEori.error.field3.required")
        .verifying(maxLength(35, "registeredAddressForEori.error.field3.length")),
      "field4" -> text("registeredAddressForEori.error.field4.required")
        .verifying(maxLength(9, "registeredAddressForEori.error.field4.length")),
      "field5" -> text("registeredAddressForEori.error.field5.required")
     )(RegisteredAddressForEori.apply)(RegisteredAddressForEori.unapply)
   )

}
