package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class AskForUploadSupportingMaterialFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "askForUploadSupportingMaterial.error.required"
  val invalidKey = "error.boolean"

  val form = new AskForUploadSupportingMaterialFormProvider()()

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
