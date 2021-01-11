/*
 * Copyright 2021 HM Revenue & Customs
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
import controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction, FakeIdentifierAction}
import controllers.behaviours.YesNoCachingControllerBehaviours
import controllers.{AddAnotherRulingController, ControllerSpecBase, routes}
import forms.AddAnotherRulingFormProvider
import models.{FileAttachment, NormalMode}
import navigation.FakeNavigator
import pages.{AddSupportingDocumentsPage, CommodityCodeRulingReferencePage, ProvideGoodsNamePage, SimilarItemCommodityCodePage, UploadSupportingMaterialMultiplePage}
import play.api.data.Form
import play.api.libs.json.{JsArray, JsBoolean, JsString, Json}
import play.api.mvc.{Call, Request}
import play.api.test.Helpers.redirectLocation
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.addAnotherRuling
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global

class AddAnotherRulingControllerSpec extends ControllerSpecBase with YesNoCachingControllerBehaviours {

  private val formProvider = new AddAnotherRulingFormProvider()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new AddAnotherRulingController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc
    )

  private def onwardRoute = Call("GET", "/foo")

  private val rulings = List.empty[String]

  def viewAsString(form: Form[_], request: Request[_]): String =
    addAnotherRuling(frontendAppConfig, formProvider().copy(errors = form.errors), NormalMode, rulings)(request, messages).toString

  "AddAnotherRulingController" must {
    behave like yesNoCachingController(
      controller,
      onwardRoute,
      viewAsString,
      backgroundData = Map.empty,
      formField = "add-another-ruling-choice"
    )

    "redirect to the same page when deleting a file" in {
      val deleteRequest = fakeRequest

      val backgroundData = Map(
        SimilarItemCommodityCodePage.toString -> JsBoolean(true),
        CommodityCodeRulingReferencePage.toString -> JsArray(Seq(
          JsString("12345678"),
          JsString("87654321")
        ))
      )

      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))
      val result = controller(getData).onRemove(0, NormalMode)(deleteRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.AddAnotherRulingController.onPageLoad(NormalMode).url)
    }

    "redirect to the add documents choice when deleting the last remaining file" in {
      val deleteRequest = fakeRequest

      val backgroundData = Map(
        SimilarItemCommodityCodePage.toString -> JsBoolean(true),
        CommodityCodeRulingReferencePage.toString -> JsArray(Seq(
          JsString("12345678")
        ))
      )

      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))
      val result = controller(getData).onRemove(0, NormalMode)(deleteRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SimilarItemCommodityCodeController.onPageLoad(NormalMode).url)
    }
  }
}
