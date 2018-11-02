package views

import play.api.data.Form
import controllers.routes
import forms.UploadSupportingMaterialMultipleFormProvider
import models.NormalMode
import views.behaviours.StringViewBehaviours
import views.html.uploadSupportingMaterialMultiple

class UploadSupportingMaterialMultipleViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "uploadSupportingMaterialMultiple"

  val form = new UploadSupportingMaterialMultipleFormProvider()()

  def createView = () => uploadSupportingMaterialMultiple(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => uploadSupportingMaterialMultiple(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "UploadSupportingMaterialMultiple view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.UploadSupportingMaterialMultipleController.onSubmit(NormalMode).url)
  }
}
