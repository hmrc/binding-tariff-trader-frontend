/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.LegalChallengeFormProvider
import models.NormalMode
import navigation.FakeNavigator
import pages.ProvideGoodsNamePage
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.{Call, Request}
import views.html.legalChallenge

import scala.concurrent.ExecutionContext.Implicits.global

class LegalChallengeControllerSpec extends ControllerSpecBase with YesNoCachingControllerBehaviours {

  private val formProvider = new LegalChallengeFormProvider()
  private val goodsName    = "wine"

  val legalChallengeView: legalChallenge = app.injector.instanceOf(classOf[legalChallenge])

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new LegalChallengeController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc,
      legalChallengeView
    )

  private def onwardRoute = Call("GET", "/foo")

  def viewAsString(form: Form[_], request: Request[_]): String =
    legalChallengeView(frontendAppConfig, form, goodsName, NormalMode)(request, messages).toString

  val backgroundData: Map[String, JsString] = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))

  "LegalChallengeController" must {
    behave like yesNoCachingController(
      controller,
      onwardRoute,
      viewAsString,
      backgroundData = backgroundData
    )
  }
}
