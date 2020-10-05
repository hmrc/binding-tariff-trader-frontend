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
import controllers.behaviours.YesNoCachingControllerBehaviours
import forms.SupportingMaterialFileListFormProvider
import models.NormalMode
import navigation.FakeNavigator
import pages.ProvideGoodsNamePage
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.{ Call, Request }
import play.api.test.Helpers._
import views.html.supportingMaterialFileList

import scala.concurrent.ExecutionContext.Implicits.global

class SupportingMaterialFileListControllerSpec extends ControllerSpecBase with YesNoCachingControllerBehaviours {
  private def onwardRoute = Call("GET", "/foo")
  private val formProvider = new SupportingMaterialFileListFormProvider()
  private val goodsName = "some-goods-name"

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new SupportingMaterialFileListController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc
    )

  // We ignore the provided form here - the controller does not prepopulate the view
  private def viewAsString(form: Form[_], request: Request[_]): String =
    supportingMaterialFileList(frontendAppConfig, formProvider(), goodsName, Seq.empty, NormalMode)(request, messages).toString

  "SupportingMaterialFileListController" must {
    behave like yesNoCachingController(
      controller,
      onwardRoute,
      viewAsString,
      formField = "add-file-choice",
      backgroundData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
    )

    "redirect to the same page when delete element" in {
      val deleteRequest = fakeRequest.withFormUrlEncodedBody(("id", "file-id"))

      val result = controller().onRemove("file-id", NormalMode)(deleteRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SupportingMaterialFileListController.onPageLoad(NormalMode).url)
    }
  }
}
