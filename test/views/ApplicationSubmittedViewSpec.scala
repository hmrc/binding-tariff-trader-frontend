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

package views

import models.oCase
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import viewmodels.{FileView, PdfViewModel}
import views.ViewMatchers._
import views.behaviours.ViewBehaviours
import views.html.components.view_application

class ApplicationSubmittedViewSpec extends ViewBehaviours {

  private val pdfView = oCase.pdf

  private def createView(pdfViewModel: PdfViewModel = pdfView): Html =
    view_application(frontendAppConfig, pdfViewModel, _ => Some("example country name"))(messages)

  private def createViewWithToggle(pdfViewModel: PdfViewModel = pdfView): Html =
    view_application(frontendAppConfigWithToggle, pdfViewModel, _ => Some("example country name"))(messages)

  protected def view(html: Html): Document =
    Jsoup.parse(html.toString())

  "Confirmation view" must {

    "contain a page heading" in {
      val doc = view(createView())

      doc                               should containElementWithID("print-pages")
      doc.getElementById("print-pages") should containText(messages("view.application.header"))
    }

    "contain a page description" in {
      val doc = view(createView())

      doc                                should containElementWithID("your-records")
      doc.getElementById("your-records") should containText(messages("view.application.title.text"))
      doc.getElementById("your-records") should containText(messages("view.application.your.record.text"))
    }

    "contain application eori, name, address " in {
      val doc = view(createView()).getElementById("your-records")

      doc should containText(messages("view.application.eori"))
      doc should containText(messages("view.application.account.name"))
      doc should containText(messages("view.application.account.address"))
    }

    "contain application contact name, contact email, contact phone" in {
      val doc = view(createView()).getElementById("your-records")

      doc should containText(messages("view.application.contact.name"))
      doc should containText(messages("view.application.contact.email"))
      doc should containText(messages("view.application.contact.phone"))
    }

    "contain application date submitted" in {
      val doc = view(createView()).getElementById("your-records")

      doc should containText(messages("view.application.contact.date"))
    }

    "contain goods name and goods details" in {
      val doc = view(createView()).getElementById("print-document")

      doc should containText(messages("provideGoodsName.checkYourAnswersLabel"))
      doc should containText(messages("view.application.goods.details"))
    }

    "contain confidential information question when user selects NO" in {
      val doc = view(createView(pdfView.copy(confidentialInformation = None))).getElementById("print-document")

      doc should containText(messages("provideConfidentialInformation.checkYourAnswersLabel"))
    }

    "contain confidential information question details when user selects YES" in {
      val doc =
        view(createView(pdfView.copy(confidentialInformation = Some("confidential information"))))
          .getElementById("print-document")

      doc should containText(messages("provideConfidentialInformation.checkYourAnswersLabel"))
    }

    "contain supporting material file list details" in {
      val doc =
        view(createView(pdfView)).getElementById("print-document")

      doc should containText(messages("supportingMaterialFileList.checkYourAnswersLabel"))
    }

    "contain supporting material file list details with Keep confidential flag on" in {
      val doc =
        view(
          createView(pdfView.copy(attachments = Seq(FileView("file id", "confidential file.pdf", confidential = true))))
        ).getElementById("print-document")

      doc should containText(messages("supportingMaterialFileList.checkYourAnswersLabel"))
      doc should containText("- Keep confidential")
    }

    "contain supporting material file list when user selects NO" in {
      val doc = view(createView(pdfView.copy(attachments = Seq()))).getElementById("print-document")

      doc should containText(messages("supportingMaterialFileList.checkYourAnswersLabel"))
    }

    "contain sending samples question when user selects NO" in {
      val doc = view(createView(pdfView.copy(sendingSample = false))).getElementById("print-document")

      doc should containText(messages("areYouSendingSamples.checkYourAnswersLabel"))
    }

    "contain hazardous samples and samples to be returned question when user selects YES to sending samples" in {
      val doc =
        view(createView(pdfView.copy(sendingSample = true, hazardousSample = true, returnSample = true)))
          .getElementById("print-document")

      doc should containText(messages("isSampleHazardous.checkYourAnswersLabel"))
      doc should containText(messages("returnSamples.checkYourAnswersLabel"))
    }

    "contain commodity code question when user selects NO" in {
      val doc = view(createView(pdfView.copy(foundCommodityCode = None))).getElementById("print-document")

      doc should containText(messages("view.applicationPdf.foundCommodityCode"))
    }

    "contain commodity code when user selects YES" in {
      val doc =
        view(createView(pdfView.copy(foundCommodityCode = Some("commodity code"))))
          .getElementById("print-document")

      doc should containText(messages("application.section.aboutItem.envisagedCommodityCode"))
    }

    "contain legal problems question when user selects NO" in {
      val doc = view(createView(pdfView.copy(legalProblems = None))).getElementById("print-document")

      doc should containText(messages("view.applicationPdf.legalProblemQuestion"))
    }

    "contain legal problem details when user selects YES" in {
      val doc =
        view(createView(pdfView.copy(legalProblems = Some("legal"))))
          .getElementById("print-document")

      doc should containText(messages("view.applicationPdf.legalProblem"))
    }

    "contain similar good codes when user selects YES and adds similar good codes " in {
      val doc =
        view(createView(pdfView.copy(similarAtarReferences = List("12345", "23456")))).getElementById("print-document")

      doc should containText(messages("commodityCodeRulingReference.checkYourAnswersLabel"))
      doc should containText(pdfView.similarAtarReferences.mkString)
    }

    "not contain similar good codes when user selects NO" in {
      val doc = view(createView(pdfView)).getElementById("print-document")

      doc should containText(messages("commodityCodeRulingReference.checkYourAnswersLabel"))
    }

    "contain reissuedBTIReference when user provides a reference" in {
      val doc = view(createView(pdfView.copy(reissuedBTIReference = Some("reissuedBTIReference"))))
        .getElementById("print-document")

      doc should containText(messages("provideBTIReference.checkYourAnswersLabel"))
      doc should containText("reissuedBTIReference")
    }

    "not contain reissuedBTIReference when user does not provide a reference" in {
      val doc = view(createView(pdfView)).getElementById("print-document")

      doc should containText(messages("provideBTIReference.checkYourAnswersLabel"))
    }

    "contain a message to not send a sample when samplesNotAccepted toggle is set to true" in {
      val doc =
        view(createViewWithToggle(pdfView))
          .getElementById("print-pages")

      doc should containText(messages("view.application.paragraph.do.not.send.sample"))
    }
  }
}
