package views

import play.api.data.Form
import controllers.routes
import forms.RegisterBusinessRepresentingFormProvider
import models.{NormalMode, RegisterBusinessRepresenting}
import views.behaviours.QuestionViewBehaviours
import views.html.registerBusinessRepresenting

class RegisterBusinessRepresentingViewSpec extends QuestionViewBehaviours[RegisterBusinessRepresenting] {

  val messageKeyPrefix = "registerBusinessRepresenting"

  override val form = new RegisterBusinessRepresentingFormProvider()()

  def createView = () => registerBusinessRepresenting(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => registerBusinessRepresenting(frontendAppConfig, form, NormalMode)(fakeRequest, messages)


  "RegisterBusinessRepresenting view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      routes.RegisterBusinessRepresentingController.onSubmit(NormalMode).url,
      "field1", "field2"
    )
  }
}
