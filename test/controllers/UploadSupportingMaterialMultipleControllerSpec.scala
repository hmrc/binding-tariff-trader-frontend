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

import connectors.DataCacheConnector
import controllers.actions._
import forms.UploadSupportingMaterialMultipleFormProvider
import models.FileAttachment.format
import models.{FileAttachment, NormalMode}
import navigation.FakeNavigator
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import pages.UploadSupportingMaterialMultiplePage
import play.api.data.Form
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Json
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.{Call, MultipartFormData}
import play.api.test.Helpers._
import service.FileService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.uploadSupportingMaterialMultiple

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UploadSupportingMaterialMultipleControllerSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  def onwardRoute = Call("GET", "/foo")

  private val fileService = mock[FileService]
  private val cacheConnector = mock[DataCacheConnector]
  private val formProvider = new UploadSupportingMaterialMultipleFormProvider()
  private val form = formProvider()
  private implicit val headers: HeaderCarrier = HeaderCarrier()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new UploadSupportingMaterialMultipleController(
      frontendAppConfig,
      messagesApi,
      cacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      fileService
    )

  private def viewAsString(form: Form[_] = form, existingFiles: Seq[FileAttachment] = Seq.empty): String = uploadSupportingMaterialMultiple(
    frontendAppConfig,
    form,
    existingFiles,
    NormalMode
  )(fakeRequest, messages).toString

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(fileService)
  }

  "UploadSupportingMaterialMultiple Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val files = Seq(FileAttachment("id", "file-name", "type", 1L))

      val validData = Map(UploadSupportingMaterialMultiplePage.toString -> Json.toJson(files))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(form, files)
    }

    "redirect to the next page when valid data is submitted" in {
      // Given A Form
      val file = TemporaryFile("example-file.txt")
      val filePart = FilePart[TemporaryFile](key = "file", "file.txt", contentType = Some("text/plain"), ref = file)
      val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq(filePart), badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)

      given(fileService.upload(refEq(filePart))(any[HeaderCarrier])).willReturn(concurrent.Future(FileAttachment("id", "file-name", "type", 0)))

      val savedCacheMap = mock[CacheMap]
      given(cacheConnector.save(any[CacheMap])).willReturn(Future.successful(savedCacheMap))

      // When
      val result = controller().onSubmit(NormalMode)(postRequest)

      // Then
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)

      val cache = theCacheSaved
      cache.getEntry[Seq[FileAttachment]](UploadSupportingMaterialMultiplePage) mustBe Some(Seq(FileAttachment("id", "file-name", "type", file.file.length())))
    }

    "redirect to the next page when no data is submitted" in {
      // Given A Form
      val filePart = FilePart[TemporaryFile](key = "file", "", contentType = Some("application/octet-stream"), ref = TemporaryFile())
      val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq(filePart), badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)

      // When
      val result = controller().onSubmit(NormalMode)(postRequest)

      // Then
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verifyZeroInteractions(fileService)
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

  private def theCacheSaved: CacheMap = {
    val captor = ArgumentCaptor.forClass(classOf[CacheMap])
    verify(cacheConnector).save(captor.capture())
    captor.getValue
  }
}
