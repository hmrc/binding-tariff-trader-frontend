/*
 * Copyright 2022 HM Revenue & Customs
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
import controllers.behaviours.AnswerCachingControllerBehaviours
import navigation.FakeNavigator
import forms.CommodityCodeDigitsFormProvider
import models.NormalMode
import pages.ProvideGoodsNamePage
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.{Call, Request}
import views.html.commodityCodeDigits

import scala.concurrent.ExecutionContext.Implicits.global

class CommodityCodeDigitsControllerSpec extends ControllerSpecBase with AnswerCachingControllerBehaviours {

  private def onwardRoute = Call("GET", "/foo")

  val formProvider = new CommodityCodeDigitsFormProvider()
  val goodsName    = "some goods"

  val commodityCodeDigitsView: commodityCodeDigits = app.injector.instanceOf(classOf[commodityCodeDigits])

  def controller(dataRetrievalAction: DataRetrievalAction) =
    new CommodityCodeDigitsController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc,
      commodityCodeDigitsView
    )

  def viewAsString(form: Form[_], request: Request[_]): String =
    commodityCodeDigitsView(frontendAppConfig, form, NormalMode, goodsName)(request, messages).toString

  val testAnswer      = "1234"
  val validFormData   = Map("value" -> testAnswer)
  val invalidFormData = Map("value" -> "")
  val backgroundData  = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))

  "CommodityCodeDigitsController" must {
    behave like answerCachingController(
      controller,
      onwardRoute,
      viewAsString,
      validFormData,
      invalidFormData,
      backgroundData,
      testAnswer
    )
  }
}
