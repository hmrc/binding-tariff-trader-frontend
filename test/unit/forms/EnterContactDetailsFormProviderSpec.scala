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
  ".name" must {

    val fieldName = "name"
    val requiredKey = "enterContactDetails.error.name.required"
    val lengthKey = "enterContactDetails.error.name.length"
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
  ".email" must {

    val fieldName = "email"
    val requiredKey = "enterContactDetails.error.email.required"
    val validKey = "enterContactDetails.error.email.invalid"
    val lengthKey = "enterContactDetails.error.email.length"
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
  ".phoneNumber" must {

    val fieldName = "phoneNumber"
    val lengthKey = "enterContactDetails.error.phoneNumber.length"
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
