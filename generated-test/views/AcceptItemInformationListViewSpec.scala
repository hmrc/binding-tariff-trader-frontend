package views

import views.behaviours.ViewBehaviours
import views.html.acceptItemInformationList

class AcceptItemInformationListViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "acceptItemInformationList"

  def createView = () => acceptItemInformationList(frontendAppConfig)(fakeRequest, messages)

  "AcceptItemInformationList view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }
}
