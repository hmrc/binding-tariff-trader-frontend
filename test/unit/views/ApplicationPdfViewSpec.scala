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

import models.response.FilestoreResponse
import models.{Case, oCase}
import org.jsoup.nodes.Document
import utils.Dates
import views.html.templates.applicationTemplate

class ApplicationPdfViewSpec extends ViewSpecBase {

  private val traderApplication = oCase.btiApplicationExample.copy(agent = None)
  private val traderCase = oCase.btiCaseExample.copy(application = traderApplication)
  private val agentCase = oCase.btiCaseExample

  private def createPdfView(c: Case, attachments: Seq[FilestoreResponse]) = applicationTemplate(frontendAppConfig, c, attachments)(fakeRequest, messages)
  private def createHtmlView(c: Case, attachments: Seq[FilestoreResponse]) = applicationTemplate(frontendAppConfig, c, attachments,viewMode = true)(fakeRequest, messages)

  "Application pdf view" must {

    "contain the details for a trader" in {
      val doc = asDocument(createPdfView(traderCase, Seq.empty))

      containsCommonSections(doc)
      assertNotRenderedById(doc, "application.section.applyingFor.heading")
    }

    "contain the details for an agent" in {
      val doc = asDocument(createPdfView(agentCase, Seq.empty))

      containsCommonSections(doc)
      assertRenderedById(doc, "application.section.applyingFor.heading")
    }

    "contain the details for re-issued BTI" in {
      val doc = asDocument(createPdfView(traderCase.copy(application = traderApplication.copy(
        reissuedBTIReference = Some("REISSUE1234"))), Seq.empty))

      doc.getElementById("application.reissuedBTIReference").text() mustBe "REISSUE1234"
    }

    "contain the details for uploaded files" in {
      asDocument(createPdfView(traderCase, Seq.empty)).getElementById("application.attachments").text() mustBe "None"

      val docFiles = asDocument(createPdfView(traderCase, Seq(
        FilestoreResponse("id", "pdfFile.pdf", "application/pdf"),
        FilestoreResponse("id", "image.jpg", "image/jpg"))))
      docFiles.getElementById("application.attachments").text() mustBe "pdfFile.pdf image.jpg"
    }

    "contain the details for related BTI case" in {
      val doc = asDocument(createPdfView(traderCase.copy(application = traderApplication.copy(
        relatedBTIReference = Some("RELATED1234"))), Seq.empty))

      doc.getElementById("application.relatedBTIReference").text() mustBe "RELATED1234"
    }

    "contain the details for legal problems" in {
      val doc = asDocument(createPdfView(traderCase.copy(application = traderApplication.copy(
        knownLegalProceedings = Some("Legal problems"))), Seq.empty))

      doc.getElementById("application.knownLegalProceedings").text() mustBe "Legal problems"
    }

    "contain the details for other information" in {
      val doc = asDocument(createPdfView(traderCase.copy(application = traderApplication.copy(
        otherInformation = Some("Other information"))), Seq.empty))

      doc.getElementById("application.otherInformation").text() mustBe "Other information"
    }

    "contain the optional hmrc logo" in {
      val doc = asDocument(createPdfView(traderCase, Seq.empty))

      assertRenderedById(doc, "application.header.logo")
    }

    "contain the pdf version of samples section" in {
      val doc = asDocument(createPdfView(traderCase, Seq.empty))

      assertRenderedById(doc, "applicationView.sendingSamples")
    }

    "contain the pdf version of what happens next section" in {
      val doc = asDocument(createPdfView(traderCase, Seq.empty))

      assertRenderedById(doc, "applicationView.whatHappensNext")
    }
  }

  "Application html view" must {

    "not contain the optional hmrc logo" in {
      val doc = asDocument(createHtmlView(traderCase, Seq.empty))

      assertNotRenderedById(doc, "application.header.logo")
    }

    "contain the html version of samples section" in {
      val doc = asDocument(createHtmlView(traderCase, Seq.empty))

      assertRenderedById(doc, "applicationView.sendingSamples")
    }

    "contain the html version of what happens next section" in {
      val doc = asDocument(createHtmlView(traderCase, Seq.empty))

      assertRenderedById(doc, "applicationView.whatHappensNext")
    }
  }

  private def containsCommonSections(doc: Document) = {
    doc.getElementById("application.submitted").text() mustBe s"${Dates.format(oCase.btiCaseExample.createdDate)}"
    doc.getElementById("application.casereference").text() mustBe s"${oCase.btiCaseExample.reference}"
    assertRenderedById(doc, "application.section.applicant.heading")
    assertRenderedById(doc, "application.section.aboutItem.heading")
    assertRenderedById(doc, "application.section.other.heading")
  }

}
