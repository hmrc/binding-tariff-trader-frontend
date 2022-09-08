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

package views

import models.{Case, oCase}
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import utils.Dates
import views.behaviours.ViewBehaviours
import views.html.ruling_information

class RulingInformationViewSpec extends ViewBehaviours {

  val rulingCertificateView: ruling_information = app.injector.instanceOf[ruling_information]

  private def createView(c: Case): () => HtmlFormat.Appendable =
    () => rulingCertificateView(frontendAppConfig, c)(fakeRequest, messages)
  private def getElementText(doc: Document, id: String): String = doc.getElementById(id).text().trim

  private val rulingCaseWithDecision              = oCase.btiCaseWithDecision
  private val rulingCaseWithoutDecision           = oCase.btiCaseWithDecision.copy(decision = None)
  private val rulingCaseWithDecisionNoExplanation = oCase.btiCaseWithDecisionNoExplanation
  private val ruling                              = rulingCaseWithDecision.decision.getOrElse(throw new Exception("Bad test data"))

  "Ruling Information View" must {

    "show the expected element values" in {

      val doc = asDocument(createView(rulingCaseWithDecision).apply())
      assertContainsText(doc, rulingCaseWithDecision.reference)
      assertContainsText(doc, rulingCaseWithDecision.application.holder.businessName)
      assertContainsText(doc, rulingCaseWithDecision.application.holder.businessName)
      assertContainsText(doc, Dates.format(ruling.effectiveStartDate)(messages))
      assertContainsText(doc, Dates.format(ruling.effectiveEndDate)(messages))

      assertContainsText(doc, messages("rulingInformation.commodityIntro"))
      assertContainsText(doc, ruling.bindingCommodityCode)

      assertContainsText(doc, ruling.explanation.getOrElse("NeverMatch"))

      assertContainsText(doc, messages("rulingInformation.certificateText"))
      assertContainsText(doc, messages("rulingInformation.samplesText"))
      assertContainsText(doc, messages("rulingInformation.appealsText"))
    }

    "show the surrounding text but nothing between when no explanation present" in {
      val doc = asDocument(createView(rulingCaseWithDecisionNoExplanation).apply())

      assertRenderedById(doc, "rulingInformation.commodityCode")
      assertNotRenderedById(doc, "rulingInformation.explanation")
      assertRenderedById(doc, "rulingInformation.commoditySuffix")
    }

    "show start date when there is decision" in {
      val doc      = asDocument(createView(rulingCaseWithDecision).apply())
      val expected = Dates.format(rulingCaseWithDecision.decision.get.effectiveStartDate)(messages)

      getElementText(doc, "rulingInformation.startDate") shouldBe expected
    }

    "show start date when there is no decision" in {
      val doc      = asDocument(createView(rulingCaseWithoutDecision).apply())
      val expected = ""

      getElementText(doc, "rulingInformation.startDate") shouldBe expected
    }

    "show expiry date when there is decision" in {
      val doc      = asDocument(createView(rulingCaseWithDecision).apply())
      val expected = Dates.format(rulingCaseWithDecision.decision.get.effectiveEndDate)(messages)

      getElementText(doc, "rulingInformation.expiryDate") shouldBe expected
    }

    "show expiry date when there is no decision" in {
      val doc      = asDocument(createView(rulingCaseWithoutDecision).apply())
      val expected = ""

      getElementText(doc, "rulingInformation.expiryDate") shouldBe expected
    }

    "show bindingCommodityCode when there is decision" in {
      val doc      = asDocument(createView(rulingCaseWithDecision).apply())
      val expected = rulingCaseWithDecision.decision.get.bindingCommodityCode

      getElementText(doc, "rulingInformation.commodityCode") shouldBe expected
    }

    "show bindingCommodityCode when there is no decision" in {
      val doc      = asDocument(createView(rulingCaseWithoutDecision).apply())
      val expected = ""

      getElementText(doc, "rulingInformation.commodityCode") shouldBe expected
    }

    "show explanation when there is decision and explanation" in {
      val doc      = asDocument(createView(rulingCaseWithDecision).apply())
      val expected = rulingCaseWithDecision.decision.get.explanation.get

      getElementText(doc, "rulingInformation.explanation") shouldBe expected
    }

    "show explanation when there is decision and no explanation" in {
      val doc      = asDocument(createView(rulingCaseWithDecisionNoExplanation).apply())
      val expected = null

      doc.getElementById("rulingInformation.explanation") should be(expected)
    }

    "show explanation when there is no decision" in {
      val doc      = asDocument(createView(rulingCaseWithoutDecision).apply())
      val expected = null

      doc.getElementById("rulingInformation.explanation") should be(expected)
    }
  }
}
