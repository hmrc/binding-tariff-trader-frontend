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

package controllers

import connectors.DataCacheConnector
import controllers.actions._
import forms.UploadSupportingMaterialMultipleFormProvider
import models.FileAttachment.format
import models.{FileAttachment, NormalMode}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.{ProvideGoodsNamePage, SupportingMaterialFileListPage}
import play.api.data.Form
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.JsString
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import play.api.test.Helpers._
import service.FileService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.uploadSupportingMaterialMultiple

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UploadSupportingMaterialMultipleControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {
  private val fileService = mock[FileService]
  private val cacheConnector = mock[DataCacheConnector]
  private val formProvider = new UploadSupportingMaterialMultipleFormProvider()
  private val form = formProvider()
  private val goodsName = "goose"

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new UploadSupportingMaterialMultipleController(
      frontendAppConfig,
      cacheConnector,
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      fileService,
      cc
    )

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(fileService)
  }

  private def viewAsString(form: Form[_] = form): String = uploadSupportingMaterialMultiple(
    frontendAppConfig,
    form,
    goodsName,
    NormalMode
  )(fakeRequest, messages).toString

  "UploadSupportingMaterialMultiple Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

    "respond with accepted and add next page on the location header  when valid data is submitted" in {
      // Given A Form
      val file = tempFileCreator.create("example-file.txt")
      val filePart = FilePart[TemporaryFile](key = "file-input", "file.txt", contentType = Some("text/plain"), ref = file)
      val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq(filePart), badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)

      given(fileService.validate(refEq(filePart))).willReturn(Right(filePart))
      given(fileService.upload(refEq(filePart))(any[HeaderCarrier])).willReturn(concurrent.Future(FileAttachment("id", "file-name", "type", 0)))

      val savedCacheMap = mock[CacheMap]
      given(cacheConnector.save(any[CacheMap])).willReturn(Future.successful(savedCacheMap))

      // When
      val result = controller().onSubmit(NormalMode)(postRequest)

      // Then
      status(result) shouldBe SEE_OTHER

      val cache = theCacheSaved
      cache.getEntry[Seq[FileAttachment]](SupportingMaterialFileListPage) shouldBe Some(Seq(FileAttachment("id", "file-name", "type", file.toPath.toFile.length())))
    }

    "respond with bad request if a file has wrong extension" in {
      // Given A Form
      val file = tempFileCreator.create("example-file.txt")
      val filePart = FilePart[TemporaryFile](key = "file-input", "file.3gp", contentType = Some("video/3gpp"), ref = file)
      val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq(filePart), badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)

      given(fileService.validate(refEq(filePart))).willReturn(Left("message something went wrong"))

      val savedCacheMap = mock[CacheMap]
      given(cacheConnector.save(any[CacheMap])).willReturn(Future.successful(savedCacheMap))

      // When
      val result = controller().onSubmit(NormalMode)(postRequest)

      // Then
      status(result) shouldBe BAD_REQUEST
      contentAsString(result) should include("message something went wrong")
    }

    "respond with bad request if no file is selected" in {
      // Given A Form
      val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq.empty, badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)

      val savedCacheMap = mock[CacheMap]
      given(cacheConnector.save(any[CacheMap])).willReturn(Future.successful(savedCacheMap))

      // When
      val result = controller().onSubmit(NormalMode)(postRequest)

      // Then
      status(result) shouldBe BAD_REQUEST
      contentAsString(result) should include("You must select a file")
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val filePart = FilePart[TemporaryFile](key = "file-input", "file.txt", contentType = Some("text/plain"), ref = tempFileCreator.create("example-file.txt"))
      val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq(filePart), badParts = Seq.empty)
      val postRequest = fakeRequest.withBody(form)
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }

  private def theCacheSaved: CacheMap = {
    val captor = ArgumentCaptor.forClass(classOf[CacheMap])
    verify(cacheConnector).save(captor.capture())
    captor.getValue
  }
}
