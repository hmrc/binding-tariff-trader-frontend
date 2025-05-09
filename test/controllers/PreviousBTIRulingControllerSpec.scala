/*
 * Copyright 2025 HM Revenue & Customs
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

import controllers.actions._
import controllers.behaviours.YesNoCachingControllerBehaviours
import forms.PreviousBTIRulingFormProvider
import models.NormalMode
import navigation.FakeNavigator
import pages.ProvideGoodsNamePage
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.{Call, Request}
import service.FakeDataCacheService
import views.html.previousBTIRuling

import scala.concurrent.ExecutionContext.Implicits.global

class PreviousBTIRulingControllerSpec extends ControllerSpecBase with YesNoCachingControllerBehaviours {

  private val formProvider = new PreviousBTIRulingFormProvider()
  private val goodsName    = "some-goods-name"

  val previousBTIRulingView: previousBTIRuling = app.injector.instanceOf(classOf[previousBTIRuling])

  private def controller(dataRetrievalAction: DataRetrievalAction): PreviousBTIRulingController =
    new PreviousBTIRulingController(
      frontendAppConfig,
      FakeDataCacheService,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc,
      previousBTIRulingView
    )

  private def onwardRoute = Call("GET", "/foo")

  private def viewAsString(form: Form[Boolean], request: Request[?]): String =
    previousBTIRulingView(frontendAppConfig, form, goodsName, NormalMode)(request, messages).toString

  val backgroundData: Map[String, JsString] = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))

  "PreviousBTIRulingController" must {
    behave like yesNoCachingController(
      controller,
      onwardRoute,
      viewAsString,
      backgroundData = backgroundData
    )
  }
}
