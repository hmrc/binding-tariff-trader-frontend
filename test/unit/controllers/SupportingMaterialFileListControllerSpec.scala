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
import forms.SupportingMaterialFileListFormProvider
import models.NormalMode
import navigation.Navigator
import play.api.data.Form
import play.api.test.Helpers._
import views.html.supportingMaterialFileList

import scala.concurrent.ExecutionContext.Implicits.global

class SupportingMaterialFileListControllerSpec extends ControllerSpecBase {

  private val formProvider = new SupportingMaterialFileListFormProvider()
  private val form: Form[Boolean] = formProvider()
  private val goodsName = "some-goods-name"

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new SupportingMaterialFileListController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new Navigator,
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc
    )

  private def viewAsString(form: Form[_] = form): String =
    supportingMaterialFileList(frontendAppConfig, form, goodsName, Seq.empty, NormalMode)(fakeRequest, messages).toString

  "SupportingMaterialFileList Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

    "redirect to the next page (Have you found commodity code) when no is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("add-file-choice", "false"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.CommodityCodeBestMatchController.onPageLoad(NormalMode).url)
    }

    "redirect to the same page when delete element" in {
      val deleteRequest = fakeRequest.withFormUrlEncodedBody(("id", "file-id"))

      val result = controller().onRemove("file-id", NormalMode)(deleteRequest)

      status(result) shouldBe SEE_OTHER
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
      val postRequest = fakeRequest.withFormUrlEncodedBody(("add-file-choice", "true"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
