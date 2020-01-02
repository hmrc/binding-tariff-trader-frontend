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

package views

import models.{Case, oCase}
import play.twirl.api.{Html, HtmlFormat}
import utils.Dates
import views.behaviours.ViewBehaviours
import views.html.ruling_information

class RulingInformationViewSpec extends ViewBehaviours {

  private def createView(c: Case): () => HtmlFormat.Appendable = () => ruling_information(frontendAppConfig, c)(fakeRequest, messages)

  private val rulingCase = oCase.btiCaseWithDecision
  private val rulingCaseNoExplanation = oCase.btiCaseWithDecisionNoExplanation
  private val holder = rulingCase.application.holder
  private val ruling = rulingCase.decision.getOrElse(throw new Exception("Bad test data"))


  "Ruling Information View" must {

    "show the expected element values" in {

      val doc = asDocument(createView(rulingCase).apply())
      assertContainsText(doc,rulingCase.reference)
      assertContainsText(doc,rulingCase.application.holder.businessName)
      assertContainsText(doc,rulingCase.application.holder.businessName)
      assertContainsText(doc,Dates.format(ruling.effectiveStartDate))
      assertContainsText(doc,Dates.format(ruling.effectiveEndDate))

      assertContainsText(doc,messages("rulingInformation.commodityIntro"))
      assertContainsText(doc, ruling.bindingCommodityCode)

      assertContainsText(doc,ruling.explanation.getOrElse("NeverMatch"))

      assertContainsText(doc, messages("rulingInformation.certificateText"))
      assertContainsText(doc, messages("rulingInformation.samplesText"))
      assertContainsText(doc, messages("rulingInformation.appealsText"))
    }

    "show the surrounding text but nothing between when no explanation present" in {
      val doc = asDocument(createView(rulingCaseNoExplanation).apply())

      assertRenderedById(doc, "rulingInformation.commodityCode")
      assertNotRenderedById(doc, "rulingInformation.explanation")
      assertRenderedById(doc, "rulingInformation.commoditySuffix")
    }
  }
}
