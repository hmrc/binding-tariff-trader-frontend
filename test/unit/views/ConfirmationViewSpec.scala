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

import models.Confirmation
import play.twirl.api.Html
import views.behaviours.ViewBehaviours
import views.html.confirmation

class ConfirmationViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "confirmation"

  private val confirm = Confirmation("reference", "marisa@example.test", sendingSamples = true)
  private val confirmNoSample = Confirmation("referenceNoSample", "marisa.nosample@example.test", sendingSamples = false)

  private def createView: () => Html = () => confirmation(frontendAppConfig, confirm)(fakeRequest, messages)
  private def createViewNoSamples: () => Html = () => confirmation(frontendAppConfig, confirmNoSample)(fakeRequest, messages)

  "Confirmation view" must {
    behave like normalPage(createView, messageKeyPrefix)()

    "with reference" in {
      val text = asDocument(createView()).text()

      text must include("reference")
      text must include("We have sent your confirmation email to marisa@example.test")
      text must include("Your application will not be processed until we get your samples")
      text must include("21 Victoria Avenue")
      text must include("We will give you your ruling within 30 to 60 days of receiving your samples")
    }

    "not display sample related text when no samples are sent" in {
      val text = asDocument(createViewNoSamples()).text()

      text must include("referenceNoSample")
      text must include("We have sent your confirmation email to marisa.nosample@example.test")
      text must not include "Your application will not be processed until we get your samples"
      text must not include "21 Victoria Avenue"
      text must include("We will give you your ruling within 30 to 60 days.")
    }
  }
}
