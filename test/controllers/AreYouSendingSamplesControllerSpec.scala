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
import forms.AreYouSendingSamplesFormProvider
import models.NormalMode
import navigation.FakeNavigator
import pages.ProvideGoodsNamePage
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.{Call, Request}
import service.FakeDataCacheService
import views.html.areYouSendingSamples

import scala.concurrent.ExecutionContext.Implicits.global

class AreYouSendingSamplesControllerSpec extends ControllerSpecBase with YesNoCachingControllerBehaviours {

  private val formProvider: AreYouSendingSamplesFormProvider = new AreYouSendingSamplesFormProvider()
  private val goodsName: String                              = "some-goods-name"

  val areYouSendingSamplesView: areYouSendingSamples = app.injector.instanceOf(classOf[areYouSendingSamples])

  private def controller(dataRetrievalAction: DataRetrievalAction) =
    new AreYouSendingSamplesController(
      frontendAppConfig,
      FakeDataCacheService,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc,
      areYouSendingSamplesView
    )

  private def onwardRoute: Call = Call("GET", "/foo")

  private def viewAsString(form: Form[Boolean], request: Request[?]): String =
    areYouSendingSamplesView(frontendAppConfig, form, NormalMode, goodsName)(request, messages).toString

  "AreYouSendingSamplesController" must {
    behave like yesNoCachingController(
      controller,
      onwardRoute,
      viewAsString,
      backgroundData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
    )
  }
}
