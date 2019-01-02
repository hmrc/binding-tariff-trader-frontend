package views

import views.behaviours.ViewBehaviours
import views.html.beforeYouStart

class BeforeYouStartViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "beforeYouStart"

  def createView = () => beforeYouStart(frontendAppConfig)(fakeRequest, messages)

  "BeforeYouStart view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }
}
