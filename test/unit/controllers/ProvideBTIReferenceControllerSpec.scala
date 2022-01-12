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
import forms.ProvideBTIReferenceFormProvider
import models.{NormalMode, BTIReference}
import navigation.FakeNavigator
import play.api.data.Form
import play.api.mvc.{ Call, Request }
import play.api.libs.json.JsValue
import views.html.provideBTIReference

import scala.concurrent.ExecutionContext.Implicits.global

class ProvideBTIReferenceControllerSpec extends ControllerSpecBase with AnswerCachingControllerBehaviours {

  private val formProvider = new ProvideBTIReferenceFormProvider()

  val provideBTIReferenceView: provideBTIReference = app.injector.instanceOf(classOf[provideBTIReference])

  def viewAsString(form: Form[_], request: Request[_]): String =
    provideBTIReferenceView(frontendAppConfig, form, NormalMode)(request, messages).toString

  private def controller(dataRetrievalAction: DataRetrievalAction) =
    new ProvideBTIReferenceController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc,
      provideBTIReferenceView
    )

  private def onwardRoute = Call("GET", "/foo")

  val validFormData = Map("btiReference" -> "value 1")
  val invalidFormData = Map("value" -> "invalid value")
  val backgroundData = Map.empty[String, JsValue]

  "ProvideBTIReferenceController" must {
    behave like answerCachingController(
      controller,
      onwardRoute,
      viewAsString,
      validFormData,
      invalidFormData,
      backgroundData,
      BTIReference("value 1")
    )
  }
}
