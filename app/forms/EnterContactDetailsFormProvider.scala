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
import forms.mappings.{Constraints, Mappings}
import play.api.data.Form
import play.api.data.Forms._
import models.EnterContactDetails

class EnterContactDetailsFormProvider @Inject() extends Mappings with Constraints {

   def apply(): Form[EnterContactDetails] = Form(
     mapping(
       "field1" -> text("enterContactDetails.error.field1.required")
         .verifying(maxLength(100, "enterContactDetails.error.field1.length")),
       "field2" -> text("enterContactDetails.error.field2.required")
         .verifying(validEmailAddress("enterContactDetails.error.field2.invalid"))
         .verifying(maxLength(100, "enterContactDetails.error.field2.length")),
       "field3" -> optional(text("enterContactDetails.error.field3.required"))
         .verifying(optionalMaxLength(20, "enterContactDetails.error.field3.length"))
     )(EnterContactDetails.apply)(EnterContactDetails.unapply)
   )

}
