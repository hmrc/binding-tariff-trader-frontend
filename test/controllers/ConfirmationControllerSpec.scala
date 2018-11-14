package controllers

import controllers.actions._
import play.api.test.Helpers._
import views.html.confirmation

class ConfirmationControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ConfirmationController(frontendAppConfig, messagesApi, FakeIdentifierAction,
      dataRetrievalAction, new DataRequiredActionImpl)

  def viewAsString() = confirmation(frontendAppConfig)(fakeRequest, messages).toString

  "Confirmation Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}




