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
import controllers.behaviours.AnswerCachingControllerBehaviours
import forms.ProvideConfidentialInformationFormProvider
import models.NormalMode
import navigation.FakeNavigator
import pages.ProvideGoodsNamePage
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.{Call, Request}
import service.FakeDataCacheService
import views.html.provideConfidentialInformation

import scala.concurrent.ExecutionContext.Implicits.global

class ProvideConfidentialInformationControllerSpec extends ControllerSpecBase with AnswerCachingControllerBehaviours {

  def onwardRoute: Call = Call("GET", "/foo")

  val provideConfidentialInformationView: provideConfidentialInformation =
    injector.instanceOf[provideConfidentialInformation]

  val formProvider = new ProvideConfidentialInformationFormProvider()
  val goodsName    = "shoos"

  def controller(dataRetrievalAction: DataRetrievalAction) =
    new ProvideConfidentialInformationController(
      frontendAppConfig,
      FakeDataCacheService,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      provideConfidentialInformationView,
      cc
    )

  def viewAsString(form: Form[String], request: Request[?]): String =
    provideConfidentialInformationView(frontendAppConfig, form, goodsName, NormalMode)(request, messages).toString

  val testAnswer                            = "answer"
  val validFormData: Map[String, String]    = Map("confidentialInformation" -> testAnswer)
  val invalidFormData: Map[String, String]  = Map("confidentialInformation" -> "")
  val backgroundData: Map[String, JsString] = Map(ProvideGoodsNamePage.toString -> JsString(goodsName))

  "ProvideConfidentialInformationController" must {
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
