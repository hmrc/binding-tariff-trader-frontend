package views

import play.api.data.Form
import forms.WhenToSendSampleFormProvider
import models.NormalMode
import models.WhenToSendSample
import views.behaviours.ViewBehaviours
import views.html.whenToSendSample

class WhenToSendSampleViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whenToSendSample"

  val form = new WhenToSendSampleFormProvider()()

  def createView = () => whenToSendSample(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => whenToSendSample(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "WhenToSendSample view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  "WhenToSendSample view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- WhenToSendSample.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- WhenToSendSample.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- WhenToSendSample.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
