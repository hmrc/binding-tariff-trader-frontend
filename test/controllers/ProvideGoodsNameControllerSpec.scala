/*
 * Copyright 2024 HM Revenue & Customs
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
import forms.ProvideGoodsNameFormProvider
import models.NormalMode
import navigation.FakeNavigator
import play.api.data.Form
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContentAsEmpty, Call, Request}
import play.api.test.FakeRequest
import views.html.provideGoodsName

import scala.concurrent.ExecutionContext.Implicits.global

class ProvideGoodsNameControllerSpec extends ControllerSpecBase with AnswerCachingControllerBehaviours {

  private def onwardRoute = Call("GET", "/foo")

  val formProvider       = new ProvideGoodsNameFormProvider()
  val form: Form[String] = formProvider()

  val provideGoodsNameView: provideGoodsName = injector.instanceOf[views.html.provideGoodsName]

  val fakeGETRequest: FakeRequest[AnyContentAsEmpty.type] = fakeGETRequestWithCSRF

  def viewAsString(form: Form[String], request: Request[_]): String =
    provideGoodsNameView(frontendAppConfig, form, NormalMode)(request, messages).toString

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ProvideGoodsNameController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      provideGoodsNameView,
      cc
    )

  val testAnswer                           = "answer"
  val validFormData: Map[String, String]   = Map("goodsName" -> testAnswer)
  val invalidFormData: Map[String, String] = Map("goodsName" -> "")
  val backgroundData                       = Map.empty[String, JsValue]

  "ProvideGoodsNameController" must {
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
