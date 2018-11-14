package views

import views.behaviours.ViewBehaviours
import views.html.confirmation

class ConfirmationViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "confirmation"

  def createView = () => confirmation(frontendAppConfig)(fakeRequest, messages)

  "Confirmation view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }
}
