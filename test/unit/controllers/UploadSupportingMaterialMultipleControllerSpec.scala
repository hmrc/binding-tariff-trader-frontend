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

import connectors.FakeDataCacheConnector
import controllers.actions._
import forms.UploadSupportingMaterialMultipleFormProvider
import models.requests.FileStoreInitiateRequest
import models.response.{FileStoreInitiateResponse, UpscanFormTemplate}
import models.{FileAttachment, FileTooLarge, FileTooSmall, NoFileSelected, NormalMode, Other}
import navigation.FakeNavigator
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.{ProvideGoodsNamePage, UploadSupportingMaterialMultiplePage}
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc.Call
import play.api.mvc.request.RequestTarget
import play.api.test.Helpers._
import service.FileService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.uploadSupportingMaterialMultiple

import scala.concurrent.ExecutionContext.Implicits.global

class UploadSupportingMaterialMultipleControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {
  private val fileService = mock[FileService]
  private val cacheConnector = FakeDataCacheConnector
  private val formProvider = new UploadSupportingMaterialMultipleFormProvider()
  private val request = fakeGETRequestWithCSRF
  private val form = formProvider()
  private val goodsName = "goose"
  private val file = FileAttachment("id", "MyFile.jpg", "image/jpeg", 1L, false)
  private val uploadedFile = file.copy(uploaded = true)

  def onwardRoute = Call("GET", "/foo")


  private def controller(dataRetrievalAction: DataRetrievalAction) =
    new UploadSupportingMaterialMultipleController(
      frontendAppConfig,
      cacheConnector,
      FakeIdentifierAction,
      dataRetrievalAction,
      new FakeNavigator(onwardRoute),
      new DataRequiredActionImpl,
      formProvider,
      fileService,
      cc
    )

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(fileService)
  }

  private val initiateResponse = FileStoreInitiateResponse(
    id = "id",
    upscanReference = "ref",
    uploadRequest = UpscanFormTemplate(
      "http://localhost:20001/upscan/upload",
      Map("key" -> "value")
    )
  )

  private def viewAsString(form: Form[_] = form): String = uploadSupportingMaterialMultiple(
    frontendAppConfig,
    initiateResponse,
    form,
    goodsName,
    NormalMode
  )(request, messages).toString

  "UploadSupportingMaterialMultiple Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      given(fileService.initiate(any[FileStoreInitiateRequest])(any[HeaderCarrier])).willReturn(initiateResponse)

      val result = controller(getRelevantData).onPageLoad(NormalMode)(request)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

    "update user answers with file when file upload succeeds" in {
      val fileAttachmentsJson = Json.toJson(Seq(file))
      val validData = Map(
        ProvideGoodsNamePage.toString -> JsString(goodsName),
        UploadSupportingMaterialMultiplePage.toString -> fileAttachmentsJson
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onFileUploadSuccess(file.id, NormalMode)(request)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
      await(FakeDataCacheConnector.getEntry[Seq[FileAttachment]](cacheMapId, UploadSupportingMaterialMultiplePage.toString)) shouldBe(Some(Seq(uploadedFile)))
    }

    "remove metadata entry for file when file upload fails with an unknown error" in {
      val fileAttachmentsJson = Json.toJson(Seq(file))
      val validData = Map(
        ProvideGoodsNamePage.toString -> JsString(goodsName),
        UploadSupportingMaterialMultiplePage.toString -> fileAttachmentsJson
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      given(fileService.initiate(any[FileStoreInitiateRequest])(any[HeaderCarrier])).willReturn(initiateResponse)

      val result = controller(getRelevantData).onFileUploadError(file.id, NormalMode)(request)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) should include("error-message-file-input")
      contentAsString(result) should include(messages(Other("").errorMessageKey))
      await(FakeDataCacheConnector.getEntry[Seq[FileAttachment]](cacheMapId, UploadSupportingMaterialMultiplePage.toString)) shouldBe(Some(Seq()))
    }

    "remove metadata entry for file when file upload fails because the file is too small" in {
      val upscanRequest = request.withTarget(RequestTarget("", "/", Map("errorCode" -> Seq("EntityTooSmall"))))
      val fileAttachmentsJson = Json.toJson(Seq(file))
      val validData = Map(
        ProvideGoodsNamePage.toString -> JsString(goodsName),
        UploadSupportingMaterialMultiplePage.toString -> fileAttachmentsJson
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      given(fileService.initiate(any[FileStoreInitiateRequest])(any[HeaderCarrier])).willReturn(initiateResponse)

      val result = controller(getRelevantData).onFileUploadError(file.id, NormalMode)(upscanRequest)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) should include("error-message-file-input")
      contentAsString(result) should include(messages(FileTooSmall.errorMessageKey))
      await(FakeDataCacheConnector.getEntry[Seq[FileAttachment]](cacheMapId, UploadSupportingMaterialMultiplePage.toString)) shouldBe(Some(Seq()))
    }

    "remove metadata entry for file when file upload fails because the file is too large" in {
      val upscanRequest = request.withTarget(RequestTarget("", "/", Map("errorCode" -> Seq("EntityTooLarge"))))
      val fileAttachmentsJson = Json.toJson(Seq(file))
      val validData = Map(
        ProvideGoodsNamePage.toString -> JsString(goodsName),
        UploadSupportingMaterialMultiplePage.toString -> fileAttachmentsJson
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      given(fileService.initiate(any[FileStoreInitiateRequest])(any[HeaderCarrier])).willReturn(initiateResponse)

      val result = controller(getRelevantData).onFileUploadError(file.id, NormalMode)(upscanRequest)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) should include("error-message-file-input")
      contentAsString(result) should include(messages(FileTooLarge.errorMessageKey))
      await(FakeDataCacheConnector.getEntry[Seq[FileAttachment]](cacheMapId, UploadSupportingMaterialMultiplePage.toString)) shouldBe(Some(Seq()))
    }

    "remove metadata entry for file when file upload fails because a file has not been selected" in {
      val upscanRequest = request.withTarget(RequestTarget("", "/", Map("errorCode" -> Seq("NoSuchUpload"))))
      val fileAttachmentsJson = Json.toJson(Seq(file))
      val validData = Map(
        ProvideGoodsNamePage.toString -> JsString(goodsName),
        UploadSupportingMaterialMultiplePage.toString -> fileAttachmentsJson
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      given(fileService.initiate(any[FileStoreInitiateRequest])(any[HeaderCarrier])).willReturn(initiateResponse)

      val result = controller(getRelevantData).onFileUploadError(file.id, NormalMode)(upscanRequest)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) should include("error-message-file-input")
      contentAsString(result) should include(messages(NoFileSelected.errorMessageKey))
      await(FakeDataCacheConnector.getEntry[Seq[FileAttachment]](cacheMapId, UploadSupportingMaterialMultiplePage.toString)) shouldBe(Some(Seq()))
    }


    "update file metadata when a file is selected in the file input" in {
      val validData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val jsonRequest = request.withBody(file)
      val result = controller(getRelevantData).onFileSelected()(jsonRequest)

      status(result) shouldBe OK
      await(FakeDataCacheConnector.getEntry[Seq[FileAttachment]](cacheMapId, UploadSupportingMaterialMultiplePage.toString)) shouldBe(Some(Seq(file)))

    }

    "add more file metadata when user uploads an additional file" in {
      val fileAttachmentsJson = Json.toJson(Seq(uploadedFile))
      val validData = Map(
        ProvideGoodsNamePage.toString -> JsString(goodsName),
        UploadSupportingMaterialMultiplePage.toString -> fileAttachmentsJson
      )

      val differentFile = file.copy(id = "fileId", name = "MyFile2.docx")
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val jsonRequest = request.withBody(differentFile)
      val result = controller(getRelevantData).onFileSelected()(jsonRequest)

      status(result) shouldBe OK
      await(FakeDataCacheConnector.getEntry[Seq[FileAttachment]](cacheMapId, UploadSupportingMaterialMultiplePage.toString)) shouldBe(Some(Seq(uploadedFile, differentFile)))
    }

    "remove stale metadata entries that the user never uploaded" in {
      val fileAttachmentsJson = Json.toJson(Seq(file))
      val validData = Map(
        ProvideGoodsNamePage.toString -> JsString(goodsName),
        UploadSupportingMaterialMultiplePage.toString -> fileAttachmentsJson
      )

      val differentFile = file.copy(id = "fileId", name = "MyFile2.docx")
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val jsonRequest = request.withBody(differentFile)
      val result = controller(getRelevantData).onFileSelected()(jsonRequest)

      status(result) shouldBe OK
      await(FakeDataCacheConnector.getEntry[Seq[FileAttachment]](cacheMapId, UploadSupportingMaterialMultiplePage.toString)) shouldBe(Some(Seq(differentFile)))
    }

    "replace file metadata when user selects different file on same page" in {
      val fileAttachmentsJson = Json.toJson(Seq(uploadedFile))
      val validData = Map(
        ProvideGoodsNamePage.toString -> JsString(goodsName),
        UploadSupportingMaterialMultiplePage.toString -> fileAttachmentsJson
      )

      val differentFile = file.copy(name = "MyFile2.docx")
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val jsonRequest = request.withBody(differentFile)
      val result = controller(getRelevantData).onFileSelected()(jsonRequest)

      status(result) shouldBe OK
      await(FakeDataCacheConnector.getEntry[Seq[FileAttachment]](cacheMapId, UploadSupportingMaterialMultiplePage.toString)) shouldBe(Some(Seq(differentFile)))
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(request)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

  }

}
