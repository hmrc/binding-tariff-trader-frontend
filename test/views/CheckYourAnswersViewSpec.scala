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

package views

import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}
import views.behaviours.ViewBehaviours
import views.html.check_your_answers

class CheckYourAnswersViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "checkYourAnswers"
  private val traderAnswer     = "Answer by the trader"

  private val traderAnswers = Seq(
    AnswerSection(
      Some("checkYourAnswers.aboutTheApplicantSection"),
      Seq(AnswerRow("label", traderAnswer, answerIsMessageKey = false, "url"))
    ),
    AnswerSection(Some("checkYourAnswers.applicantOtherBusiness"), Seq.empty)
  )

  val checkYourAnswersView: check_your_answers = app.injector.instanceOf[check_your_answers]

  private def createTraderView: () => Html =
    () => checkYourAnswersView(frontendAppConfig, traderAnswers, sendingSamples = true)(fakeRequest, messages)

  private def createTraderViewNoSamples: () => Html =
    () => checkYourAnswersView.render(frontendAppConfig, traderAnswers, sendingSamples = false, fakeRequest, messages)

  "Check Your Answers view" must {
    behave like normalPage(createTraderView, messageKeyPrefix)()

    "contain the answers for a trader" in {
      val text = asDocument(createTraderView()).text()

      text should include("About the applicant")
      text should include(traderAnswer)
    }

    "samples may be damaged statement is visible when sending samples is true" in {
      val text = asDocument(createTraderView()).text()

      text should include(messages("checkYourAnswers.declaration.paragraph2"))
    }

    "samples may be damaged statement is not visible when sending samples is false" in {
      val text = asDocument(createTraderViewNoSamples()).text()

      text should not include messages("checkYourAnswers.declaration.paragraph2")
    }
  }
}
