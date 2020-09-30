/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package unit.controllers

import connectors.FakeDataCacheConnector
import controllers.actions._
import controllers.{ControllerSpecBase, MakeFileConfidentialController, routes}
import forms.MakeFileConfidentialFormProvider
import models.{FileConfidentiality, NormalMode}
import navigation.FakeNavigator
import pages.{MakeFileConfidentialPage, ProvideGoodsNamePage}
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.makeFileConfidential

import scala.concurrent.ExecutionContext.Implicits.global

class MakeFileConfidentialControllerSpec extends ControllerSpecBase {

  private def onwardRoute = Call("GET", "/foo")

  private val formProvider = new MakeFileConfidentialFormProvider()
  private val form = formProvider()
  private val fileId = "some-file-id"

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new MakeFileConfidentialController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc
    )

  def viewAsString(form: Form[_] = form): String = makeFileConfidential(frontendAppConfig, form, NormalMode, fileId)(fakeRequest, messages).toString

  "MakeFileConfidentialController" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fileId, NormalMode)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("fileId" -> fileId, "confidential" -> "true")

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("fileId" -> fileId, "confidential" -> "")
      val boundForm = form.bind(Map("fileId" -> fileId, "confidential" -> ""))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) shouldBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(fileId, NormalMode)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("fileId" -> fileId, "confidential" -> "true")
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
