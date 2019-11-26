/*
 * Copyright 2019 HM Revenue & Customs
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

import controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeIdentifierAction}
import navigation.FakeNavigator
import play.api.mvc.Call
import play.api.test.Helpers._
import viewmodels.AnswerSection
import views.html.check_your_answers

class CheckYourAnswersControllerSpec extends ControllerSpecBase {

  private def onwardRoute = Call("GET", "/foo")
  private def view = app.injector.instanceOf[check_your_answers]

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new CheckYourAnswersController(
      frontendAppConfig,
      messagesApi,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      messagesControllerComponents,
      view
    )

  "Check Your Answers Controller" must {

    "return 200 and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      status(result) mustBe OK

      val expectedSections = Seq(
        AnswerSection(Some("checkYourAnswers.applicantRegisteredSection"), Seq.empty),
        AnswerSection(Some("checkYourAnswers.applicantOtherBusiness"), Seq.empty),
        AnswerSection(Some("checkYourAnswers.informationAboutYourItemSection"), Seq.empty),
        AnswerSection(Some("checkYourAnswers.otherInformation"), Seq.empty)
      )
      contentAsString(result) mustBe view(expectedSections)(messages, fakeRequest).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

  }

}
