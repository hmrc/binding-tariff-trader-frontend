package controllers

import play.api.data.Form
import play.api.libs.json.JsString
import uk.gov.hmrc.http.cache.client.CacheMap
import navigation.FakeNavigator
import connectors.FakeDataCacheConnector
import controllers.actions._
import play.api.test.Helpers._
import forms.$className$FormProvider
import models.NormalMode
import models.$className$
import pages.$className$Page
import play.api.mvc.Call
import views.html.$className;format="decap"$

class $className$ControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new $className$FormProvider()
  val form = formProvider()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new $className$Controller(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(onwardRoute), FakeIdentifierAction,
      dataRetrievalAction, new DataRequiredActionImpl, formProvider)

  def viewAsString(form: Form[_] = form) = $className;format="decap"$(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  "$className$ Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map($className$Page.toString -> JsString($className$.values.head.toString))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) shouldBe viewAsString(form.fill($className$.values.head))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", $className$.options.head.value))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) shouldBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", $className$.options.head.value))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
