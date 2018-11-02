package views

import play.api.data.Form
import forms.CommodityCodeBestMatchFormProvider
import models.NormalMode
import models.CommodityCodeBestMatch
import views.behaviours.ViewBehaviours
import views.html.commodityCodeBestMatch

class CommodityCodeBestMatchViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "commodityCodeBestMatch"

  val form = new CommodityCodeBestMatchFormProvider()()

  def createView = () => commodityCodeBestMatch(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => commodityCodeBestMatch(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "CommodityCodeBestMatch view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  "CommodityCodeBestMatch view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- CommodityCodeBestMatch.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- CommodityCodeBestMatch.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- CommodityCodeBestMatch.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
