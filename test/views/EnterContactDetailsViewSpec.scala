package views

import play.api.data.Form
import controllers.routes
import forms.EnterContactDetailsFormProvider
import models.{NormalMode, EnterContactDetails}
import views.behaviours.QuestionViewBehaviours
import views.html.enterContactDetails

class EnterContactDetailsViewSpec extends QuestionViewBehaviours[EnterContactDetails] {

  val messageKeyPrefix = "enterContactDetails"

  override val form = new EnterContactDetailsFormProvider()()

  def createView = () => enterContactDetails(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => enterContactDetails(frontendAppConfig, form, NormalMode)(fakeRequest, messages)


  "EnterContactDetails view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      routes.EnterContactDetailsController.onSubmit(NormalMode).url,
      "field1", "field2"
    )
  }
}
