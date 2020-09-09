package unit.forms

import forms.AddConfidentialInformationFormProvider
import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class AddConfidentialInformationFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "addConfidentialInformation.error.required"
  val invalidKey = "error.boolean"

  val form = new AddConfidentialInformationFormProvider()()

  ".value" must {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
