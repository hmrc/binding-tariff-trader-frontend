package forms

import forms.behaviours.OptionFieldBehaviours
import models.SupportingInformation
import play.api.data.FormError

class SupportingInformationFormProviderSpec extends OptionFieldBehaviours {

  val form = new SupportingInformationFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "supportingInformation.error.required"

    behave like optionsField[SupportingInformation](
      form,
      fieldName,
      validValues  = SupportingInformation.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
