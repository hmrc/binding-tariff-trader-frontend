package controllers

import controllers.actions._
import play.api.test.Helpers._
import views.html.beforeYouStart

class BeforeYouStartControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new BeforeYouStartController(frontendAppConfig, messagesApi, FakeIdentifierAction,
      dataRetrievalAction, new DataRequiredActionImpl)

  def viewAsString() = beforeYouStart(frontendAppConfig)(fakeRequest, messages).toString

  "BeforeYouStart Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}




