package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class RegisteredAddressForEoriFormProviderSpec extends StringFieldBehaviours {

  val form = new RegisteredAddressForEoriFormProvider()()

  ".field1" must {

    val fieldName = "field1"
    val requiredKey = "registeredAddressForEori.error.field1.required"
    val lengthKey = "registeredAddressForEori.error.field1.length"
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

  ".field2" must {

    val fieldName = "field2"
    val requiredKey = "registeredAddressForEori.error.field2.required"
    val lengthKey = "registeredAddressForEori.error.field2.length"
    val maxLength = 75

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
