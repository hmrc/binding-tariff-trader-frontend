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

package controllers

import connectors.FakeDataCacheConnector
import controllers.actions._
import controllers.behaviours.YesNoCachingControllerBehaviours
import forms.AddConfidentialInformationFormProvider
import models.NormalMode
import navigation.FakeNavigator
import pages.ProvideGoodsNamePage
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.Call

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.Request

class AddConfidentialInformationControllerSpec extends ControllerSpecBase with YesNoCachingControllerBehaviours {

  def onwardRoute = Call("GET", "/foo")
  val formProvider = new AddConfidentialInformationFormProvider()
  val addConfidentialInformationView = injector.instanceOf[views.html.addConfidentialInformation]
  val goodsName = "Mushrooms"

  def controller(dataRetrievalAction: DataRetrievalAction) =
    new AddConfidentialInformationController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      addConfidentialInformationView,
      cc
    )

  def viewAsString(form: Form[_], request: Request[_]) = addConfidentialInformationView(
    frontendAppConfig, form, goodsName, NormalMode)(request, messages).toString

  "AddConfidentialInformationController" must {
    behave like yesNoCachingController(
      controller,
      onwardRoute,
      viewAsString,
      backgroundData = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))
    )
  }
}
