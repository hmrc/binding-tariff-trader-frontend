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
import forms.EnterContactDetailsFormProvider
import models.{EnterContactDetails, NormalMode}
import navigation.FakeNavigator
import play.api.data.Form
import play.api.mvc.{ Call, Request }
import play.api.libs.json.JsValue
import views.html.enterContactDetails

import scala.concurrent.ExecutionContext.Implicits.global

class EnterContactDetailsControllerSpec extends ControllerSpecBase with AnswerCachingControllerBehaviours {

  private val formProvider = new EnterContactDetailsFormProvider

  val enterContactDetailsView: enterContactDetails = app.injector.instanceOf(classOf[enterContactDetails])

  private def controller(dataRetrievalAction: DataRetrievalAction): EnterContactDetailsController = {
    new EnterContactDetailsController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc,
      enterContactDetailsView
    )
  }

  private def onwardRoute = Call("GET", "/foo")

  private def viewAsString(form: Form[_], request: Request[_]): String =
    enterContactDetailsView(frontendAppConfig, form, NormalMode)(request, messages).toString

  val validFormData = Map("name" -> "value 1", "email" -> "value2@me.com", "phoneNumber" -> "+44 (0)800-443-123")
  val invalidFormData = Map("value" -> "invalid data")
  val backgroundData = Map.empty[String, JsValue]

  "EnterContactDetailsController" must {
    val validAnswers = List(
      EnterContactDetails("value 1", "value 2", "value 3"),
      EnterContactDetails("value 1", "value 2", "another tel number")
    )

    behave like answerCachingController(
      controller,
      onwardRoute,
      viewAsString,
      validFormData,
      invalidFormData,
      backgroundData,
      validAnswers: _*
    )
  }
}
