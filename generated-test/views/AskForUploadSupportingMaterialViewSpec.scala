package views

import play.api.data.Form
import controllers.routes
import forms.AskForUploadSupportingMaterialFormProvider
import views.behaviours.YesNoViewBehaviours
import models.NormalMode
import views.html.askForUploadSupportingMaterial

class AskForUploadSupportingMaterialViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "askForUploadSupportingMaterial"

  val form = new AskForUploadSupportingMaterialFormProvider()()

  def createView = () => askForUploadSupportingMaterial(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => askForUploadSupportingMaterial(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "AskForUploadSupportingMaterial view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.AskForUploadSupportingMaterialController.onSubmit(NormalMode).url)
  }
}
