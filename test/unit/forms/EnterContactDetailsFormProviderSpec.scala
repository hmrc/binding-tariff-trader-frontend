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

import forms.behaviours.{EmailFieldBehaviours, StringFieldBehaviours}
import play.api.data.FormError

class EnterContactDetailsFormProviderSpec extends StringFieldBehaviours with EmailFieldBehaviours {

  val form = new EnterContactDetailsFormProvider()()

  // name
  ".field1" must {

    val fieldName = "field1"
    val requiredKey = "enterContactDetails.error.field1.required"
    val lengthKey = "enterContactDetails.error.field1.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  // email address
  ".field2" must {

    val fieldName = "field2"
    val requiredKey = "enterContactDetails.error.field2.required"
    val validKey = "enterContactDetails.error.field2.invalid"
    val lengthKey = "enterContactDetails.error.field2.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like validEmailFieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength)),
      invalidEmailError = FormError(fieldName, validKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  // phone number
  ".field3" must {

    val fieldName = "field3"
    val lengthKey = "enterContactDetails.error.field3.length"
    val maxLength = 20

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )
  }

}
