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

import javax.inject.Inject
import forms.mappings.{Constraints, Mappings}
import play.api.data.Form
import play.api.data.Forms._
import models.EnterContactDetails

class EnterContactDetailsFormProvider @Inject() extends Mappings with Constraints {

   def apply(): Form[EnterContactDetails] = Form(
     mapping(
       "name" -> text("enterContactDetails.error.name.required")
         .verifying(maxLength(100, "enterContactDetails.error.name.length")),
       "email" -> text("enterContactDetails.error.email.required")
         .verifying(validEmailAddress("enterContactDetails.error.email.invalid"))
         .verifying(maxLength(100, "enterContactDetails.error.email.length")),
       "phoneNumber" -> optional(text("enterContactDetails.error.phoneNumber.required"))
         .verifying(optionalMaxLength(20, "enterContactDetails.error.phoneNumber.length"))
     )(EnterContactDetails.apply)(EnterContactDetails.unapply)
   )

}
