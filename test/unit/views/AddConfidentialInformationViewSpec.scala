package unit.views

import controllers.routes
import forms.AddConfidentialInformationFormProvider
import models.NormalMode
import play.api.data.Form
import views.behaviours.YesNoViewBehaviours
import views.html.addConfidentialInformation

class AddConfidentialInformationViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "addConfidentialInformation"

  val form = new AddConfidentialInformationFormProvider()()

  def createView = () => addConfidentialInformation(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => addConfidentialInformation(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "AddConfidentialInformation view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.AddConfidentialInformationController.onSubmit(NormalMode).url)
  }
}
