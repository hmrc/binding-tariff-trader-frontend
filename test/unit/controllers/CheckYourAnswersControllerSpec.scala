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

import controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeIdentifierAction}
import play.api.test.Helpers._
import service.CountriesService
import viewmodels.AnswerSection
import views.html.check_your_answers

class CheckYourAnswersControllerSpec extends ControllerSpecBase {

  private val countriesService = new CountriesService

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new CheckYourAnswersController(
      frontendAppConfig,
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      countriesService,
      cc
    )

  "Check Your Answers Controller" must {

    "return 200 and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      status(result) shouldBe OK

      val expectedSections = Seq(
        AnswerSection(Some("checkYourAnswers.applicantRegisteredSection"), Seq.empty),
        AnswerSection(Some("checkYourAnswers.applicantOtherBusiness"), Seq.empty),
        AnswerSection(Some("checkYourAnswers.informationAboutYourItemSection"), Seq.empty),
        AnswerSection(Some("checkYourAnswers.otherInformation"), Seq.empty)
      )
      contentAsString(result) shouldBe check_your_answers(frontendAppConfig, expectedSections)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

  }

}
