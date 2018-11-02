package forms

import forms.behaviours.OptionFieldBehaviours
import models.CommodityCodeBestMatch
import play.api.data.FormError

class CommodityCodeBestMatchFormProviderSpec extends OptionFieldBehaviours {

  val form = new CommodityCodeBestMatchFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "commodityCodeBestMatch.error.required"

    behave like optionsField[CommodityCodeBestMatch](
      form,
      fieldName,
      validValues  = CommodityCodeBestMatch.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
