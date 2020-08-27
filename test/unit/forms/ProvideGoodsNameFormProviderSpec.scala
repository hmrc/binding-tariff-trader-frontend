package unit.forms

import forms.behaviours.StringFieldBehaviours
import forms.provideGoodsNameFormProvider
import play.api.data.FormError

class ProvideGoodsNameFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "provideGoodsName.error.required"
  val lengthKey = "provideGoodsName.error.length"
  val maxLength = 100

  val form = new provideGoodsNameFormProvider()()

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
