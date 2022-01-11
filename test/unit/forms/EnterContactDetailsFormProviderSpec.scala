/*
 * Copyright 2022 HM Revenue & Customs
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

import scala.collection.mutable

class EnterContactDetailsFormProviderSpec extends StringFieldBehaviours with EmailFieldBehaviours {

  val formProvider = new EnterContactDetailsFormProvider()
  val form = formProvider()
  val formWithTelValidation = formProvider.formWithMinTelNumber

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
  ".phoneNumber in rest of the world" must {

    val fieldName = "phoneNumber"
    val lengthKey = "enterContactDetails.error.phoneNumber.length"
    val maxLength = 20
    val phoneFormatKey = "enterContactDetails.error.phoneNumber.invalid"


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

    behave like fieldWithRegex(
      form,
      fieldName,
      "432jfsdfs453fcs",
      maxLength = maxLength,
      regex = formProvider.telephoneRegex,
      error = FormError(fieldName, phoneFormatKey, mutable.WrappedArray.empty)
    )
  }

  ".phoneNumber in UK" must {

    val fieldName = "phoneNumber"
    val lengthKey = "enterContactDetails.error.phoneNumber.length"
    val minLengthKey = "enterContactDetails.error.phoneNumber.minLength"
    val minLength = 10
    val maxLength = 20
    val phoneFormatKey = "enterContactDetails.error.phoneNumber.invalid"


    behave like fieldThatBindsValidData(
      formWithTelValidation,
      fieldName,
      stringsWithMaxLength(maxLength),
    )

    behave like fieldWithMaxLength(
      formWithTelValidation,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithMinLength(
      formWithTelValidation,
      fieldName,
      minLength = minLength,
      lengthError = FormError(fieldName, minLengthKey, Seq(minLength))
    )

    behave like fieldWithRegexAndMinTelLength(
      formWithTelValidation,
      fieldName,
      "432jfsdfs453fcs",
      minLength = minLength,
      maxLength = maxLength,
      regex = """[0-9]{1,20}$""",
      error = FormError(fieldName, phoneFormatKey, mutable.WrappedArray.empty)
    )
  }
}
