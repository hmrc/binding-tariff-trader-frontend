/*
 * Copyright 2019 HM Revenue & Customs
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

import connectors.{DataCacheConnector, FakeDataCacheConnector}
import controllers.actions._
import forms.UploadWrittenAuthorisationFormProvider
import models.{FileAttachment, NormalMode}
import navigation.FakeNavigator
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import pages.UploadWrittenAuthorisationPage
import play.api.data.Form
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Json
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.{Call, MultipartFormData}
import play.api.test.Helpers._
import service.FileService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.uploadWrittenAuthorisation
import org.scalatest.Matchers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UploadWrittenAuthorisationControllerSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  private def onwardRoute = Call("GET", "/foo")

  private val fileService = mock[FileService]
  private val cacheConnector = mock[DataCacheConnector]

  override protected def afterEach(): Unit = {
    super.beforeEach()
    reset(fileService)
  }

  private val formProvider = new UploadWrittenAuthorisationFormProvider()
  private val form = formProvider()

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new UploadWrittenAuthorisationController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(onwardRoute), FakeIdentifierAction,
      dataRetrievalAction, new DataRequiredActionImpl, formProvider, fileService)

  private def viewAsString(form: Form[_] = form, file: Option[FileAttachment] = None): String =
    uploadWrittenAuthorisation(frontendAppConfig, form, file, NormalMode)(fakeRequest, messages).toString

  private val testAnswer = "answer"

  "UploadWrittenAuthorisation Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      val result1 = contentAsString(result)
      result1 mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val file = FileAttachment("id", "file-name", "type", 1L)

      val validData = Map(UploadWrittenAuthorisationPage.toString -> Json.toJson(file))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(form, Some(file))
    }

    "redirect to the next page when valid data is submitted" in {

      //Given
      val file = TemporaryFile("example-file.txt")
      val filePart = FilePart[TemporaryFile](key = "letter-of-authority", "file.txt", contentType = Some("text/plain"), ref = file)
      val form: MultipartFormData[TemporaryFile] = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq(filePart), badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)

      given(fileService.upload(refEq(filePart))(any[HeaderCarrier])).willReturn(Future.successful(FileAttachment("id", "file-name", "type", 0)))
      given(fileService.validate(any[MultipartFormData.FilePart[TemporaryFile]])).willReturn(Right(form.file("letter-of-authority").get))

      val savedCacheMap = mock[CacheMap]
      given(cacheConnector.save(any[CacheMap])).willReturn(Future.successful(savedCacheMap))

     // When
      val result = controller().onSubmit(NormalMode)(postRequest)

      // Then
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect to the next page when no data is submitted but existing file in data cache" in {

      // Empty form
      val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq.empty, badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)

      // file stored in cache
      val file = FileAttachment("id", "file-name", "type", 1L)
      val validData = Map(UploadWrittenAuthorisationPage.toString -> Json.toJson(file))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request when invalid data is submitted" in {

      // Empty form
      val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq.empty, badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
    }

    "return a Bad Request when invalid file type is submitted" in {

      val file = TemporaryFile("example-file.mp3")
      val filePart = FilePart[TemporaryFile](key = "letter-of-authority", "example-file.mp3", contentType = Some("audio/mpeg"), ref = file)
      val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq(filePart), badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)

      given(fileService.validate(any[MultipartFormData.FilePart[TemporaryFile]])).willReturn(Left("some error message about bad file"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST

      contentAsString(result) should include ("some error message about bad file")
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
