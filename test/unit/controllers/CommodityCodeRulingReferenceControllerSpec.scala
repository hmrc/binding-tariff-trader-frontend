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
import controllers.behaviours.AnswerCachingControllerBehaviours
import forms.CommodityCodeRulingReferenceFormProvider
import models.NormalMode
import navigation.FakeNavigator
import play.api.data.Form
import play.api.mvc.{ Call, Request }
import views.html.commodityCodeRulingReference

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.JsValue

class CommodityCodeRulingReferenceControllerSpec extends ControllerSpecBase with AnswerCachingControllerBehaviours {

  private def onwardRoute = Call("GET", "/foo")

  private val formProvider = new CommodityCodeRulingReferenceFormProvider()

  private def controller(dataRetrievalAction: DataRetrievalAction) =
    new CommodityCodeRulingReferenceController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc
    )

  def viewAsString(form: Form[_], request: Request[_]): String =
    commodityCodeRulingReference(frontendAppConfig, form, onwardRoute, NormalMode)(request, messages).toString

  val testAnswer = "answer"
  val validFormData = Map("value" -> testAnswer)
  val invalidFormData = Map("value" -> "")
  val backgroundData = Map.empty[String, JsValue]

  "CommodityCodeRulingReferenceController" must {
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
