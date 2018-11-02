package forms

import forms.behaviours.OptionFieldBehaviours
import models.WhenToSendSample
import play.api.data.FormError

class WhenToSendSampleFormProviderSpec extends OptionFieldBehaviours {

  val form = new WhenToSendSampleFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "whenToSendSample.error.required"

    behave like optionsField[WhenToSendSample](
      form,
      fieldName,
      validValues  = WhenToSendSample.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
