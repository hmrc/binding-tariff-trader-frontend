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

import models.oCase
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.pdftemplates.applicationPdf

class ApplicationPdfViewSpec extends ViewSpecBase {

  private val traderCase = oCase.btiCaseExample.copy(application = oCase.btiApplicationExample.copy(agent = None))
  private val traderAttachments = Seq.empty

  private val agentCase = oCase.btiCaseExample
  private val agentAttachments = Seq.empty

  private def createTraderPdfView: () => Html = () => applicationPdf(frontendAppConfig, traderCase, traderAttachments)(fakeRequest, messages)
  private def createAgentPdfView: () => Html = () => applicationPdf(frontendAppConfig, agentCase, agentAttachments)(fakeRequest, messages)

  "Check Your Answers view" must {

    "contain the details for a trader" in {
      val doc = asDocument(createTraderPdfView())

      containsCommonSections(doc)
      assertNotRenderedById(doc, "pdf.application.section.applyingFor.heading")
    }

    "contain the details for an agent" in {
      val doc = asDocument(createAgentPdfView())

      containsCommonSections(doc)
      assertRenderedById(doc, "pdf.application.section.applyingFor.heading")
    }
  }

  private def containsCommonSections(doc: Document) = {
    assertRenderedById(doc, "pdf.application.section.applicant.heading")
    assertRenderedById(doc, "pdf.application.section.applicationType.heading")
    assertRenderedById(doc, "pdf.application.section.aboutItem.heading")
    assertRenderedById(doc, "pdf.application.section.other.heading")
  }

}
