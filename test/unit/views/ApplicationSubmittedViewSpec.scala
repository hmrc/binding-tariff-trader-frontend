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

package unit.views

import java.time.Instant

import models.oCase
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import utils.Dates
import viewmodels.PdfViewModel
import views.ViewMatchers._
import views.behaviours.ViewBehaviours
import views.html.components.view_application

class ApplicationSubmittedViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "view.application"
  private val pdfView = oCase.pdf

  private def createView(pdfViewModel: PdfViewModel = pdfView): Html = view_application(frontendAppConfig, pdfViewModel)(fakeRequest, messages)

  protected def view(html: Html): Document = {
    Jsoup.parse(html.toString())
  }

  "Confirmation view" must {

    "contain a page heading" in {
      val doc = view(createView())

      doc should containElementWithID("pdf-id")
      doc.getElementById("pdf-id") should containText(messages("view.application.header"))
    }

    "contain a page description" in {
      val doc = view(createView())

      doc should containElementWithID("print-pages")
      doc.getElementById("print-pages") should containText(messages("view.application.logo.text"))
      doc.getElementById("print-pages") should containText(messages("view.application.title.text"))
      doc.getElementById("print-pages") should containText(messages("view.application.your.record.text"))
    }

    "contain application eori, name, address " in {
      val doc = view(createView()).getElementById("print-pages")

      doc should containText(messages("view.application.eori"))
      doc should containText(messages("view.application.account.name"))
      doc should containText(messages("view.application.account.address"))
    }

    "contain application contact name, contact email, contact phone" in {
      val doc = view(createView()).getElementById("print-pages")

      doc should containText(messages("view.application.contact.name"))
      doc should containText(messages("view.application.contact.email"))
      doc should containText(messages("view.application.contact.phone"))
    }

    "contain application date submitted" in {
      val doc = view(createView()).getElementById("print-pages")

      doc should containText(messages("view.application.contact.date"))
    }

    "contain goods name and goods details" in {
      val doc = view(createView()).getElementById("print-pages")

      doc should containText(messages("provideGoodsName.checkYourAnswersLabel"))
      doc should containText(messages("view.application.goods.details"))
    }

    "contain confidential information question when user selects NO" in {
      val doc = view(createView(pdfView.copy(confidentialInformation = None))).getElementById("print-pages")

      doc should containText(messages("provideConfidentialInformation.checkYourAnswersLabel"))
    }

    "contain confidential information question details when user selects YES" in {
      val doc =
        view(createView(pdfView.copy(confidentialInformation = Some("confidential information"))))
          .getElementById("print-pages")

      doc should containText(messages("provideConfidentialInformation.checkYourAnswersLabel"))
    }

    "contain sending samples question when user selects NO" in {
      val doc = view(createView(pdfView.copy(sendingSample = false))).getElementById("print-pages")

      doc should containText(messages("areYouSendingSamples.checkYourAnswersLabel"))
    }

    "contain hazardous samples and samples to be returned question when user selects YES to sending samples" in {
      val doc =
        view(createView(pdfView.copy(sendingSample = true, hazardousSample = Some(true), returnSample = true)))
          .getElementById("print-pages")

      doc should containText(messages("isSampleHazardous.checkYourAnswersLabel"))
      doc should containText(messages("returnSamples.checkYourAnswersLabel"))
    }

    "contain commodity code question when user selects NO" in {
      val doc = view(createView(pdfView.copy(foundCommodityCode = None))).getElementById("print-pages")

      doc should containText(messages("view.applicationPdf.foundComodityCode"))
    }

    "contain commodity code when user selects YES" in {
      val doc =
        view(createView(pdfView.copy(foundCommodityCode = Some("commodity code"))))
          .getElementById("print-pages")

      doc should containText(messages("application.section.aboutItem.envisagedCommodityCode"))
    }

    "contain legal problems question when user selects NO" in {
      val doc = view(createView(pdfView.copy(legalProblems = None))).getElementById("print-pages")

      doc should containText(messages("view.applicationPdf.legalProblemQuestion"))
    }

    "contain legal problem details when user selects YES" in {
      val doc =
        view(createView(pdfView.copy(legalProblems = Some("legal"))))
          .getElementById("print-pages")

      doc should containText(messages("view.applicationPdf.legalProblem"))
    }

    "contain footer date" in {
      val dateSubmitted = Instant.now
      val doc =
        view(createView(pdfView.copy(dateSubmitted = dateSubmitted)))
          .getElementById("print-pages")

      doc.text() should include(Dates.formatForPdf(dateSubmitted))
    }
  }
}
