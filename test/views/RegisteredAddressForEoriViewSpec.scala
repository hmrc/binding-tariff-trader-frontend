package views

import play.api.data.Form
import controllers.routes
import forms.RegisteredAddressForEoriFormProvider
import models.{NormalMode, RegisteredAddressForEori}
import views.behaviours.QuestionViewBehaviours
import views.html.registeredAddressForEori

class RegisteredAddressForEoriViewSpec extends QuestionViewBehaviours[RegisteredAddressForEori] {

  val messageKeyPrefix = "registeredAddressForEori"

  override val form = new RegisteredAddressForEoriFormProvider()()

  def createView = () => registeredAddressForEori(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => registeredAddressForEori(frontendAppConfig, form, NormalMode)(fakeRequest, messages)


  "RegisteredAddressForEori view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      routes.RegisteredAddressForEoriController.onSubmit(NormalMode).url,
      "field1", "field2"
    )
  }
}
