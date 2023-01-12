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
import controllers.behaviours.AnswerCachingControllerBehaviours
import forms.ProvideGoodsDescriptionFormProvider
import models.NormalMode
import navigation.FakeNavigator
import pages.ProvideGoodsNamePage
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.{Call, Request}
import views.html.provideGoodsDescription

import scala.concurrent.ExecutionContext.Implicits.global

class ProvideGoodsDescriptionControllerSpec extends ControllerSpecBase with AnswerCachingControllerBehaviours {

  def onwardRoute                                          = Call("GET", "/foo")
  val formProvider                                         = new ProvideGoodsDescriptionFormProvider()
  val provideGoodsDescriptionView: provideGoodsDescription = injector.instanceOf[views.html.provideGoodsDescription]

  def controller(dataRetrievalAction: DataRetrievalAction) =
    new ProvideGoodsDescriptionController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      provideGoodsDescriptionView,
      cc
    )

  def viewAsString(form: Form[_], request: Request[_]): String =
    provideGoodsDescriptionView(frontendAppConfig, form, testName, NormalMode)(request, messages).toString

  val testAnswer = "answer"
  val testName   = "Luigi"

  val validFormData   = Map("goodsDescription"            -> testAnswer)
  val invalidFormData = Map("goodsDescription"            -> "")
  val backgroundData  = Map(ProvideGoodsNamePage.toString -> JsString(testName))

  "ProvideGoodsDescriptionController" must {
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
