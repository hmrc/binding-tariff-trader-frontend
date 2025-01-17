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

import models.{Case, oCase}
import play.twirl.api.HtmlFormat
import views.ViewMatchers.containElementWithID
import views.html.components.rulingCoverLetterTemplate

class CoverLetterViewSpec extends ViewSpecBase {

  private def createPdfView(c: Case): HtmlFormat.Appendable =
    rulingCoverLetterTemplate(frontendAppConfig, c, c.decision.get, _ => Some("dummy country name"))(messages)

  private val rulingCase            = oCase.btiCaseWithDecision
  private val applicationSamples    = oCase.btiCaseWithDecision.application.copy(sampleToBeProvided = true)
  private val rulingCaseWithSamples = oCase.btiCaseWithDecision.copy(application = applicationSamples)
  private val doc                   = asDocument(createPdfView(rulingCase))
  private val docWithSamples        = asDocument(createPdfView(rulingCaseWithSamples))

  "Ruling pdf samples" must {

    val section = "samples-section"

    "contain the samples information box if samples are being sent" in {
      docWithSamples should containElementWithID(section)
    }

    " not contain the samples information box if samples are not being sent" in {
      doc shouldNot containElementWithID(section)
    }
  }

}
