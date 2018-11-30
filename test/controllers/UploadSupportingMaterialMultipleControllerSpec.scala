/*
 * Copyright 2018 HM Revenue & Customs
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

package controllers

import connectors.FakeDataCacheConnector
import controllers.actions._
import forms.UploadSupportingMaterialMultipleFormProvider
import models.NormalMode
import navigation.FakeNavigator
import org.scalatest.mockito.MockitoSugar
import pages.UploadSupportingMaterialMultiplePage
import play.api.data.Form
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.JsString
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.{Call, MultipartFormData}
import play.api.test.Helpers._
import service.FileService
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.uploadSupportingMaterialMultiple

class UploadSupportingMaterialMultipleControllerSpec extends ControllerSpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val fileService = mock[FileService]
  val formProvider = new UploadSupportingMaterialMultipleFormProvider()
  val form = formProvider()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new UploadSupportingMaterialMultipleController(
      frontendAppConfig,
      messagesApi,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      fileService
    )

  def viewAsString(form: Form[_] = form) = uploadSupportingMaterialMultiple(frontendAppConfig, form, Seq.empty, NormalMode)(fakeRequest, messages).toString

  val testAnswer = "answer"

  "UploadSupportingMaterialMultiple Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(UploadSupportingMaterialMultiplePage.toString -> JsString(testAnswer))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(form.fill(testAnswer))
    }

    "redirect to the next page when valid data is submitted" in {
      val filePart = FilePart[TemporaryFile](key = "file", "file.txt", contentType = Some("text/plain"), ref = TemporaryFile("example-file.txt"))
      val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq(filePart), badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val filePart = FilePart[TemporaryFile](key = "file", "file.txt", contentType = Some("text/plain"), ref = TemporaryFile("example-file.txt"))
      val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq(filePart), badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
