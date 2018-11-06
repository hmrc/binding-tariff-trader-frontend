package views

import play.api.data.Form
import controllers.routes
import forms.LegalChallengeDetailsFormProvider
import models.NormalMode
import views.behaviours.StringViewBehaviours
import views.html.legalChallengeDetails

class LegalChallengeDetailsViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "legalChallengeDetails"

  val form = new LegalChallengeDetailsFormProvider()()

  def createView = () => legalChallengeDetails(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => legalChallengeDetails(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "LegalChallengeDetails view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.LegalChallengeDetailsController.onSubmit(NormalMode).url)
  }
}
