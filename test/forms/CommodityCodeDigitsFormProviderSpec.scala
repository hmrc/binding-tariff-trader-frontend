package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class CommodityCodeDigitsFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "commodityCodeDigits.error.required"
  val lengthKey = "commodityCodeDigits.error.length"
  val maxLength = 100

  val form = new CommodityCodeDigitsFormProvider()()

  ".value" must {

    val fieldName = "value"

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
