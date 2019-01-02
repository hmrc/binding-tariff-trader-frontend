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
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.FormError

class RegisteredAddressForEoriFormProviderSpec extends StringFieldBehaviours {

  val form = new RegisteredAddressForEoriFormProvider()()

  ".field1" must {

    val fieldName = "field1"
    val requiredKey = "registeredAddressForEori.error.field1.required"

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

  ".field2" must {

    val fieldName = "field2"
    val requiredKey = "registeredAddressForEori.error.field2.required"
    val lengthKey = "registeredAddressForEori.error.field2.length"
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

  ".field3" must {

    val fieldName = "field3"
    val requiredKey = "registeredAddressForEori.error.field3.required"
    val lengthKey = "registeredAddressForEori.error.field3.length"
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

  ".field4" must {

    val fieldName = "field4"
    val requiredKey = "registeredAddressForEori.error.field4.required"
    val lengthKey = "registeredAddressForEori.error.field4.length"
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

  ".field5" must {

    val fieldName = "field5"
    val requiredKey = "registeredAddressForEori.error.field5.required"

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
