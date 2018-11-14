package views

import play.api.data.Form
import controllers.routes
import forms.DeclarationFormProvider
import models.NormalMode
import views.behaviours.StringViewBehaviours
import views.html.declaration

class DeclarationViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "declaration"

  val form = new DeclarationFormProvider()()

  def createView = () => declaration(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => declaration(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "Declaration view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.DeclarationController.onSubmit(NormalMode).url)
  }
}
