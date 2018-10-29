package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class RegisterBusinessRepresentingFormProviderSpec extends StringFieldBehaviours {

  val form = new RegisterBusinessRepresentingFormProvider()()

  ".field1" must {

    val fieldName = "field1"
    val requiredKey = "registerBusinessRepresenting.error.field1.required"
    val lengthKey = "registerBusinessRepresenting.error.field1.length"
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
    val requiredKey = "registerBusinessRepresenting.error.field2.required"
    val lengthKey = "registerBusinessRepresenting.error.field2.length"
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
}
