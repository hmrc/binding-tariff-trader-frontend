/*
 * Copyright 2018 HM Revenue & Customs
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
import mapper.CaseRequestMapper
import models.NormalMode
import navigation.FakeNavigator
import org.scalatest.mockito.MockitoSugar
import play.api.mvc.Call
import play.api.test.Helpers._
import service.CasesService
import views.html.declaration

class DeclarationControllerSpec extends ControllerSpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val casesService = mock[CasesService]
  val mapper = mock[CaseRequestMapper]
  val testAnswer = "answer"

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new DeclarationController(
      frontendAppConfig,
      messagesApi,
      FakeDataCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      casesService,
      mapper
    )

  def viewAsString = declaration(frontendAppConfig, NormalMode)(fakeRequest, messages).toString

  "Declaration Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString
    }

    "return OK and the correct view for a POST" in {
      val result = controller().onSubmit(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString
    }
  }
}
