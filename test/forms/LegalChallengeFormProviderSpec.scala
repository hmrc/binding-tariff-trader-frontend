package forms

import forms.behaviours.OptionFieldBehaviours
import models.LegalChallenge
import play.api.data.FormError

class LegalChallengeFormProviderSpec extends OptionFieldBehaviours {

  val form = new LegalChallengeFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "legalChallenge.error.required"

    behave like optionsField[LegalChallenge](
      form,
      fieldName,
      validValues  = LegalChallenge.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
