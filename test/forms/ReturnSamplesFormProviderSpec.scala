package forms

import forms.behaviours.OptionFieldBehaviours
import models.ReturnSamples
import play.api.data.FormError

class ReturnSamplesFormProviderSpec extends OptionFieldBehaviours {

  val form = new ReturnSamplesFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "returnSamples.error.required"

    behave like optionsField[ReturnSamples](
      form,
      fieldName,
      validValues  = ReturnSamples.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
