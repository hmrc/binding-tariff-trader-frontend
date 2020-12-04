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

import models.{Confirmation, oCase}
import play.twirl.api.Html
import viewmodels.PdfViewModel
import views.behaviours.ViewBehaviours
import views.html.confirmation

class ConfirmationViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "confirmation"

  private val confirm = Confirmation("reference", "eori", "marisa@example.test")
  private val pdfViewModel = oCase.pdf
  private val pdfViewNoSamplesModel = oCase.pdfNoSamples
  private val hazardousSamplePdf = pdfViewModel.copy(hazardousSample = true)
  private val noHazardousNoReturnSamplesPdf = pdfViewModel.copy(hazardousSample = false, returnSample = false)
  private val noHazardousReturnSamplesPdf = pdfViewModel.copy(hazardousSample = false, returnSample = true)

  private def createView: () => Html = () => confirmation(frontendAppConfig, confirm, "token", pdfViewModel, s => Some("example country name"))(fakeRequest, messages)
  private def createView(pdf: PdfViewModel) = confirmation(frontendAppConfig, confirm, "token", pdf, s => Some("example country name"))(fakeRequest, messages)

  "Confirmation view" must {
    behave like normalPage(createView, messageKeyPrefix)()
    behave like pageWithoutBackLink(createView)


    "with reference" in {
      val text = asDocument(createView(pdfViewModel)).text()

      text should include("reference")
      text should include("We have sent your confirmation email to marisa@example.test")
      text should include("Your application will not be processed until we receive your samples")
      text should include("21 Victoria Avenue")
      text should include(messages("confirmation.paragraph.sample.return"))
      text should include(messages("confirmation.heading2.whatNext"))
    }

    "Main HTML view" must {
      "not display sample related text when no samples are sent" in {
        val text = asDocument(createView(pdfViewNoSamplesModel)).text()

        text should not include(messages("confirmation.paragraph.sample.return"))
        text should not include(messages("confirmation.sendingSamples.important"))
      }

      "display correct messages when samples are hazardous" in {
        val text = asDocument(createView(hazardousSamplePdf)).getElementById("sampleInformation").text()

        text should include(messages("view.application.paragraph.do.not.send.sample"))
        text should not include (messages("confirmation.sendingSamples.address"))
        text should not include (messages("confirmation.paragraph.sample.return"))
      }

      "display correct messages when samples are not hazardous and samples not returned" in {
        val text = asDocument(createView(noHazardousNoReturnSamplesPdf)).getElementById("sampleInformation").text()
        val address = asDocument(createView(noHazardousReturnSamplesPdf)).getElementById("sampleInformation").getElementsByTag("address").first().text()

        address should include(messages("confirmation.sendingSamples.address").split("<")(0))
        text should not include (messages("confirmation.paragraph.sample.return"))
        text should include(messages("confirmation.paragraph1.sendingSamples"))
        text should not include (messages("view.application.paragraph.do.not.send.sample"))
      }

      "display correct messages when samples are not hazardous and samples returned" in {
        val text = asDocument(createView(noHazardousReturnSamplesPdf)).getElementById("sampleInformation").text()
        val address = asDocument(createView(noHazardousReturnSamplesPdf)).getElementById("sampleInformation").getElementsByTag("address").first().text()

        text should include(messages("confirmation.paragraph.sample.return"))
        address should include(messages("confirmation.sendingSamples.address").split("<")(0))
        text should include(messages("confirmation.paragraph1.sendingSamples"))
        text should not include (messages("view.application.paragraph.do.not.send.sample"))
      }
    }

    "Main PDF view" must {
      "not display sample related text when no samples are sent" in {
        val text = asDocument(createView(pdfViewNoSamplesModel)).text()

        text should include(messages("confirmation.paragraph.confirmationEmail", "marisa@example.test"))
        text should not include(messages("confirmation.sendingSamples.important"))
      }

      "display correct messages when samples are hazardous" in {
        val text = asDocument(createView(hazardousSamplePdf)).getElementById("pdfView").text()

        text should include(messages("view.application.paragraph.do.not.send.sample"))
        text should not include (messages("confirmation.sendingSamples.address"))
        text should not include (messages("confirmation.paragraph.sample.return"))
      }

      "display correct messages when samples are not hazardous and samples not returned" in {
        val text = asDocument(createView(noHazardousNoReturnSamplesPdf)).getElementById("pdfView").text()
        val address = asDocument(createView(noHazardousReturnSamplesPdf)).getElementById("pdfView").getElementsByTag("address").first().text()

        address should include(messages("confirmation.sendingSamples.address").split("<")(0))
        text should not include (messages("confirmation.paragraph.sample.return"))
        text should not include (messages("view.application.paragraph.do.not.send.sample"))
        text should include(messages("view.application.sending.sample.paragraph1"))
      }

      "display correct messages when samples are not hazardous and samples returned" in {
        val text = asDocument(createView(noHazardousReturnSamplesPdf)).getElementById("pdfView").text()
        val address = asDocument(createView(noHazardousReturnSamplesPdf)).getElementById("pdfView").getElementsByTag("address").first().text()

        text should include(messages("confirmation.paragraph.sample.return"))
        address should include(messages("confirmation.sendingSamples.address").split("<")(0))
        text should include(messages("view.application.sending.sample.paragraph1"))
        text should not include (messages("view.application.paragraph.do.not.send.sample"))
      }
    }
  }
}
