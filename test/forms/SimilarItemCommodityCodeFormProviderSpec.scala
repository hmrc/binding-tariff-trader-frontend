package forms

import forms.behaviours.OptionFieldBehaviours
import models.SimilarItemCommodityCode
import play.api.data.FormError

class SimilarItemCommodityCodeFormProviderSpec extends OptionFieldBehaviours {

  val form = new SimilarItemCommodityCodeFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "similarItemCommodityCode.error.required"

    behave like optionsField[SimilarItemCommodityCode](
      form,
      fieldName,
      validValues  = SimilarItemCommodityCode.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
