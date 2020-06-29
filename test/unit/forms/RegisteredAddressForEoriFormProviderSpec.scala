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

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.FormError

class RegisteredAddressForEoriFormProviderSpec extends StringFieldBehaviours {

  val form = new RegisteredAddressForEoriFormProvider()()

  ".eori" must {

    val fieldName = "eori"
    val requiredKey = "registeredAddressForEori.error.eori.required"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      arbitrary[String]
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".businessName" must {

    val fieldName = "businessName"
    val requiredKey = "registeredAddressForEori.error.businessName.required"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      arbitrary[String]
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".addressLine1" must {

    val fieldName = "addressLine1"
    val requiredKey = "registeredAddressForEori.error.addressLine1.required"
    val lengthKey = "registeredAddressForEori.error.addressLine1.length"
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

  ".townOrCity" must {

    val fieldName = "townOrCity"
    val requiredKey = "registeredAddressForEori.error.townOrCity.required"
    val lengthKey = "registeredAddressForEori.error.townOrCity.length"
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

  ".postcode" must {

    val fieldName = "postcode"
    val lengthKey = "registeredAddressForEori.error.postcode.length"
    val requirePostcodeKey = "registeredAddressForEori.error.postcode.required"
    val invalidPostcodeKey = "registeredAddressForEori.error.postcode.gb"
    val maxLength = 19

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

    behave like postcodeField(
      form,
      fieldName,
      Seq(FormError(fieldName, requirePostcodeKey)),
      Seq(FormError(fieldName, invalidPostcodeKey))
    )
  }

  ".country" must {

    val fieldName = "country"
    val requiredKey = "registeredAddressForEori.error.country.required"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      arbitrary[String]
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

}
