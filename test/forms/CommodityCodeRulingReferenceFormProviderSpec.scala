package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class CommodityCodeRulingReferenceFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "commodityCodeRulingReference.error.required"
  val lengthKey = "commodityCodeRulingReference.error.length"
  val maxLength = 100

  val form = new CommodityCodeRulingReferenceFormProvider()()

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
