package views

import play.api.data.Form
import forms.SimilarItemCommodityCodeFormProvider
import models.NormalMode
import models.SimilarItemCommodityCode
import views.behaviours.ViewBehaviours
import views.html.similarItemCommodityCode

class SimilarItemCommodityCodeViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "similarItemCommodityCode"

  val form = new SimilarItemCommodityCodeFormProvider()()

  def createView = () => similarItemCommodityCode(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => similarItemCommodityCode(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "SimilarItemCommodityCode view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  "SimilarItemCommodityCode view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- SimilarItemCommodityCode.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- SimilarItemCommodityCode.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- SimilarItemCommodityCode.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
