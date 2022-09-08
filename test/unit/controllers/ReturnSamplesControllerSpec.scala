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
import controllers.behaviours.YesNoCachingControllerBehaviours
import forms.ReturnSamplesFormProvider
import models.NormalMode
import navigation.FakeNavigator
import play.api.data.Form
import play.api.mvc.{Call, Request}
import views.html.returnSamples

import scala.concurrent.ExecutionContext.Implicits.global

class ReturnSamplesControllerSpec extends ControllerSpecBase with YesNoCachingControllerBehaviours {

  private def onwardRoute = Call("GET", "/foo")

  private val formProvider = new ReturnSamplesFormProvider()

  val returnSamplesView: returnSamples = app.injector.instanceOf(classOf[returnSamples])

  private def controller(dataRetrievalAction: DataRetrievalAction) =
    new ReturnSamplesController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc,
      returnSamplesView
    )

  def viewAsString(form: Form[_], request: Request[_]): String =
    returnSamplesView(frontendAppConfig, form, NormalMode)(request, messages).toString

  "ReturnSamples Controller" must {
    behave like yesNoCachingController(
      controller,
      onwardRoute,
      viewAsString
    )
  }
}
