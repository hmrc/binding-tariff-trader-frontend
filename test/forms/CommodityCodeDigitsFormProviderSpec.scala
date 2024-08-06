/*
 * Copyright 2024 HM Revenue & Customs
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

class CommodityCodeDigitsFormProviderSpec extends StringFieldBehaviours {

  val requiredKey  = "commodityCodeDigits.error.required"
  val maxLengthKey = "commodityCodeDigits.error.maxLength"
  val minLengthKey = "commodityCodeDigits.error.minLength"
  val numericType  = "commodityCodeDigits.error.type"
  val maxLength    = 25
  val minLength    = 2

  val form = new CommodityCodeDigitsFormProvider()()

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like commodityCodeField(
      form,
      fieldName,
      requiredErrorKey = FormError(fieldName, requiredKey),
      notNumericTypeErrorKey = FormError(fieldName, numericType),
      maxLengthErrorKey = FormError(fieldName, maxLengthKey, Seq(maxLength)),
      minLengthErrorKey = FormError(fieldName, minLengthKey, Seq(minLength))
    )
  }
}
