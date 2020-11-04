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
import views.behaviours.ViewBehaviours
import views.html.confirmation

class ConfirmationViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "confirmation"

  private val confirm = Confirmation("reference", "eori", "marisa@example.test", sendingSamples = true)
  private val confirmNoSample = Confirmation("referenceNoSample", "eori", "marisa.nosample@example.test", sendingSamples = false)
  private val pdfViewModel = oCase.pdf

  private def createView: () => Html = () => confirmation(frontendAppConfig, confirm, "token", pdfViewModel)(fakeRequest, messages)
  private def createViewNoSamples: () => Html = () => confirmation(frontendAppConfig, confirmNoSample, "token", pdfViewModel)(fakeRequest, messages)
  private def createViewHazardous: () => Html = () => confirmation(frontendAppConfig, confirm, "token", pdfViewModel.copy(hazardousSample = true))(fakeRequest, messages)

  "Confirmation view" must {
    behave like normalPage(createView, messageKeyPrefix)()

    "with reference" in {
      val text = asDocument(createView()).text()

      text should include("reference")
      text should include("We have sent your confirmation email to marisa@example.test")
      text should include("Your application will not be processed until we receive your samples")
      text should include("21 Victoria Avenue")
      text should include(messages("view.application.paragraph.whatNext0.nosample"))
    }

    "not display sample related text when no samples are sent" in {
      val text = asDocument(createViewNoSamples()).text()

      text should include("referenceNoSample")
      text should include(messages("confirmation.paragraph.confirmationEmail", "marisa.nosample@example.test"))
      text should include (messages("confirmation.sendingSamples.important"))
      text should not include(messages("confirmation.paragraph.whatNext0.nosample"))
    }

    "display correct messages when samples are hazardous" in {
      val text = asDocument(createViewHazardous()).text()

      text should include (messages("view.application.paragraph.do.not.send.sample"))
      text should not include(messages("confirmation.paragraph.whatNext0.nosample", "confirmation.paragraph.whatNext0.important"))

    }


    "display correct messages when samples are not hazardous" in {
      val text = asDocument(createViewHazardous()).text()

      text should include (messages("confirmation.paragraph.whatNext0.sample01"))
      text should not include(messages("view.application.paragraph.do.not.send.sample"))

    }
  }
}
