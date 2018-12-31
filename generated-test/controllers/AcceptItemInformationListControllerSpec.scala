package controllers

import controllers.actions._
import play.api.test.Helpers._
import views.html.acceptItemInformationList

class AcceptItemInformationListControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new AcceptItemInformationListController(frontendAppConfig, messagesApi, FakeIdentifierAction,
      dataRetrievalAction, new DataRequiredActionImpl)

  def viewAsString() = acceptItemInformationList(frontendAppConfig)(fakeRequest, messages).toString

  "AcceptItemInformationList Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}
