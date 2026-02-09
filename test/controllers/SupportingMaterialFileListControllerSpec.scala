/*
 * Copyright 2026 HM Revenue & Customs
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

import controllers.actions.*
import controllers.behaviours.YesNoCachingControllerBehaviours
import forms.SupportingMaterialFileListFormProvider
import models.cache.CacheMap
import models.{FileAttachment, NormalMode}
import navigation.FakeNavigator
import pages.{AddSupportingDocumentsPage, ProvideGoodsNamePage, UploadSupportingMaterialMultiplePage}
import play.api.data.Form
import play.api.libs.json.{JsArray, JsBoolean, JsString, JsValue, Json}
import play.api.mvc.{Call, Request}
import play.api.test.Helpers.*
import service.FakeDataCacheService
import views.html.supportingMaterialFileList

import scala.concurrent.ExecutionContext.Implicits.global

class SupportingMaterialFileListControllerSpec extends ControllerSpecBase with YesNoCachingControllerBehaviours {
  private def onwardRoute  = Call("GET", "/foo")
  private val formProvider = new SupportingMaterialFileListFormProvider()
  private val goodsName    = "some-goods-name"

  val supportingMaterialFileListView: supportingMaterialFileList =
    app.injector.instanceOf(classOf[supportingMaterialFileList])

  private def controller(dataRetrievalAction: DataRetrievalAction) =
    new SupportingMaterialFileListController(
      frontendAppConfig,
      FakeDataCacheService,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc,
      supportingMaterialFileListView
    )

  // We will not use the provided form here - errors aside, the controller does not prepopulate the view
  private def viewAsString(form: Form[?], request: Request[?]): String =
    supportingMaterialFileListView(
      frontendAppConfig,
      formProvider().copy(errors = form.errors),
      goodsName,
      Seq.empty,
      NormalMode
    )(request, messages).toString

  "SupportingMaterialFileListController" must {
    behave like yesNoCachingController(
      controller,
      onwardRoute,
      viewAsString,
      formField = "add-file-choice",
      backgroundData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
    )

    "redirect to the same page when deleting a file" in {
      val deleteRequest = fakeRequest.withFormUrlEncodedBody(("id", "file-id"))

      val backgroundData = Map(
        ProvideGoodsNamePage.toString       -> JsString(goodsName),
        AddSupportingDocumentsPage.toString -> JsBoolean(true),
        UploadSupportingMaterialMultiplePage.toString -> JsArray(
          Seq(
            Json.toJson(FileAttachment("file-id-1", "foo.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-2", "bar.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-3", "baz.jpg", "image/jpeg", 1L))
          )
        )
      )

      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))
      val result  = controller(getData).onRemove("file-id-2", NormalMode)(deleteRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SupportingMaterialFileListController.onPageLoad(NormalMode).url)
    }

    "redirect to the add documents choice when deleting the last remaining file" in {
      val deleteRequest = fakeRequest.withFormUrlEncodedBody(("id", "file-id"))

      val backgroundData = Map(
        ProvideGoodsNamePage.toString       -> JsString(goodsName),
        AddSupportingDocumentsPage.toString -> JsBoolean(true),
        UploadSupportingMaterialMultiplePage.toString -> JsArray(
          Seq(
            Json.toJson(FileAttachment("file-id-1", "foo.jpg", "image/jpeg", 1L))
          )
        )
      )

      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))
      val result  = controller(getData).onRemove("file-id-1", NormalMode)(deleteRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.AddSupportingDocumentsController.onPageLoad(NormalMode).url)
    }

    "displays error when trying to exceed max file upload" in {
      val submitRequest = fakeRequest.withFormUrlEncodedBody(("add-file-choice", "true"))

      val backgroundData = Map(
        ProvideGoodsNamePage.toString       -> JsString(goodsName),
        AddSupportingDocumentsPage.toString -> JsBoolean(true),
        UploadSupportingMaterialMultiplePage.toString -> JsArray(
          Seq(
            Json.toJson(FileAttachment("file-id-1", "foo.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-2", "bar.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-3", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-4", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-5", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-6", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-7", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-8", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-9", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-10", "baz.jpg", "image/jpeg", 1L))
          )
        )
      )

      val getData      = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))
      val result       = controller(getData).onSubmit(NormalMode)(submitRequest)
      val resultString = contentAsString(result)

      status(result) shouldBe BAD_REQUEST
      resultString     should include("error-message-add-file-choice-input")
      resultString should include(
        messages("supportingMaterialFileList.error.numberFiles", appConfig.fileUploadMaxFiles)
      )
    }

    "allows user to proceed after they've added the maximum number of uploads" in {
      val submitRequest = fakeRequest.withFormUrlEncodedBody(("add-file-choice", "false"))

      val backgroundData = Map(
        ProvideGoodsNamePage.toString       -> JsString(goodsName),
        AddSupportingDocumentsPage.toString -> JsBoolean(true),
        UploadSupportingMaterialMultiplePage.toString -> JsArray(
          Seq(
            Json.toJson(FileAttachment("file-id-1", "foo.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-2", "bar.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-3", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-4", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-5", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-6", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-7", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-8", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-9", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-10", "baz.jpg", "image/jpeg", 1L))
          )
        )
      )

      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))
      val result  = controller(getData).onSubmit(NormalMode)(submitRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "not allow user to proceed after they've exceeded the maximum number of uploads" in {
      val submitRequest = fakeRequest.withFormUrlEncodedBody(("add-file-choice", "false"))

      val backgroundData = Map(
        ProvideGoodsNamePage.toString       -> JsString(goodsName),
        AddSupportingDocumentsPage.toString -> JsBoolean(true),
        UploadSupportingMaterialMultiplePage.toString -> JsArray(
          Seq(
            Json.toJson(FileAttachment("file-id-1", "foo.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-2", "bar.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-3", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-4", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-5", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-6", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-7", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-8", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-9", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-10", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-11", "baz.jpg", "image/jpeg", 1L))
          )
        )
      )

      val getData      = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))
      val result       = controller(getData).onSubmit(NormalMode)(submitRequest)
      val resultString = contentAsString(result)

      status(result) shouldBe BAD_REQUEST
      resultString     should include("error-message-add-file-choice-input")
      resultString should include(
        messages("supportingMaterialFileList.error.numberFiles", appConfig.fileUploadMaxFiles)
      )
    }

    "show both error messages when the user exceeds max files and doesn't answer the question" in {
      val submitRequest = fakeRequest.withFormUrlEncodedBody(("add-file-choice", ""))

      val backgroundData = Map(
        ProvideGoodsNamePage.toString       -> JsString(goodsName),
        AddSupportingDocumentsPage.toString -> JsBoolean(true),
        UploadSupportingMaterialMultiplePage.toString -> JsArray(
          Seq(
            Json.toJson(FileAttachment("file-id-1", "foo.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-2", "bar.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-3", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-4", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-5", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-6", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-7", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-8", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-9", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-10", "baz.jpg", "image/jpeg", 1L)),
            Json.toJson(FileAttachment("file-id-11", "baz.jpg", "image/jpeg", 1L))
          )
        )
      )

      val getData      = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))
      val result       = controller(getData).onSubmit(NormalMode)(submitRequest)
      val resultString = contentAsString(result)

      status(result) shouldBe BAD_REQUEST
      resultString     should include("error-message-add-file-choice-input")
      resultString     should include(messages("supportingMaterialFileList.error.required"))
      resultString should include(
        messages("supportingMaterialFileList.error.numberFiles", appConfig.fileUploadMaxFiles)
      )
    }
  }

}
