/*
 * Copyright 2019 HM Revenue & Customs
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

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class RegisterBusinessRepresentingFormProviderSpec extends StringFieldBehaviours {

  val form = new RegisterBusinessRepresentingFormProvider()()

  ".eoriNumber" must {

    val fieldName = "eoriNumber"
    val requiredKey = "registerBusinessRepresenting.error.eoriNumber.required"
    val lengthKey = "registerBusinessRepresenting.error.eoriNumber.format"

    "pass when a valid eori is inserted" in {
      val validEori = "GB123456789000000"
      val result = form.bind(Map(fieldName -> validEori)).apply(fieldName)
      result.errors shouldBe empty
      result.value.value shouldBe validEori
    }

    "fail when eori format is not correct" in {
      val invalidEori = "GB1234567890000001234"
      val result = form.bind(Map(fieldName -> invalidEori)).apply(fieldName)
      result.errors.size shouldBe 1
      result.errors.head.message shouldBe "registerBusinessRepresenting.error.eoriNumber.format"
    }

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }


  ".businessName" must {

    val fieldName = "businessName"
    val requiredKey = "registerBusinessRepresenting.error.businessName.required"
    val lengthKey = "registerBusinessRepresenting.error.businessName.length"
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

  ".addressLine1" must {

    val fieldName = "addressLine1"
    val requiredKey = "registerBusinessRepresenting.error.addressLine1.required"
    val lengthKey = "registerBusinessRepresenting.error.addressLine1.length"
    val maxLength = 70

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

  ".town" must {

    val fieldName = "town"
    val requiredKey = "registerBusinessRepresenting.error.town.required"
    val lengthKey = "registerBusinessRepresenting.error.town.length"
    val maxLength = 35

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

  ".postCode" must {

    val fieldName = "postCode"
    val requiredKey = "registerBusinessRepresenting.error.postCode.required"
    val lengthKey = "registerBusinessRepresenting.error.postCode.length"
    val maxLength = 9

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

  ".country" must {

    val fieldName = "country"
    val requiredKey = "registerBusinessRepresenting.error.country.required"
    val lengthKey = "registerBusinessRepresenting.error.country.length"
    val maxLength = 60

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

}
