package views

import play.api.data.Form
import forms.SupportingInformationFormProvider
import models.NormalMode
import models.SupportingInformation
import views.behaviours.ViewBehaviours
import views.html.supportingInformation

class SupportingInformationViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "supportingInformation"

  val form = new SupportingInformationFormProvider()()

  def createView = () => supportingInformation(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => supportingInformation(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "SupportingInformation view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  "SupportingInformation view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- SupportingInformation.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- SupportingInformation.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- SupportingInformation.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
