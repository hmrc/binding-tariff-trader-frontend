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

import controllers.actions._
import play.api.test.Helpers._
import views.html.acceptItemInformationList

import scala.concurrent.ExecutionContext.Implicits.global

class AcceptItemInformationListControllerSpec extends ControllerSpecBase {

  val acceptItemInformationListView: acceptItemInformationList =
    app.injector.instanceOf(classOf[acceptItemInformationList])

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new AcceptItemInformationListController(
      frontendAppConfig,
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      cc,
      acceptItemInformationListView
    )
  def viewAsString(): String = acceptItemInformationListView(frontendAppConfig)(fakeRequest, messages).toString

  "AcceptItemInformationList Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }
  }
}
