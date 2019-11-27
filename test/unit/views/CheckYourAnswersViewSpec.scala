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

package views

import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}
import views.behaviours.ViewBehaviours
import views.html.check_your_answers

class CheckYourAnswersViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "checkYourAnswers"
  private val traderAnswer = "Answer by the trader"
  private val agentAnswer = "Answer by the agent"
  private val agentAnswerForTrader = "Answer by the agent on behalf of trader"

  private val view = app.injector.instanceOf[check_your_answers]

  private val traderAnswers = Seq(
    AnswerSection(Some("checkYourAnswers.applicantRegisteredSection"), Seq(AnswerRow("label", traderAnswer, answerIsMessageKey = false, "url"))),
    AnswerSection(Some("checkYourAnswers.applicantOtherBusiness"), Seq.empty)
  )
  private val agentAnswers = Seq(
    AnswerSection(Some("checkYourAnswers.applicantRegisteredSection"), Seq(AnswerRow("label", agentAnswer, answerIsMessageKey = false, "url"))),
    AnswerSection(Some("checkYourAnswers.applicantOtherBusiness"), Seq(AnswerRow("label", agentAnswerForTrader, answerIsMessageKey = false, "url")))
  )

  private def createTraderView: () => Html = () => view(traderAnswers)(messages, fakeRequest)
  private def createAgentView: () => Html = () => view(agentAnswers)(messages, fakeRequest)

  "Check Your Answers view" must {
    behave like normalPage(createTraderView, messageKeyPrefix)()


    "contain the answers for a trader" in {
      val text = asDocument(createTraderView()).text()

      text must include("Your details")
      text must include(traderAnswer)
      text must not include "Details of the business, organisation or individual you represent"
    }

    "contain the answers for an agent" in {
      val text = asDocument(createAgentView()).text()

      text must include("Your details")
      text must include(agentAnswer)
      text must include("Details of the business, organisation or individual you represent")
      text must include(agentAnswerForTrader)
    }
  }
}
