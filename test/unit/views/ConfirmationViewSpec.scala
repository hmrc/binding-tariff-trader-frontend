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
import views.behaviours.ViewBehaviours
import views.html.confirmation

class ConfirmationViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "confirmation"

  private val confirm = Confirmation("reference", "marisa@example.test")

  private def createView = { () =>
    confirmation(frontendAppConfig, confirm)(fakeRequest, messages)
  }

  "Confirmation view" must {
    behave like normalPage(createView, messageKeyPrefix)()

    "with reference" in {
      val text = asDocument(createView()).text()

      text must include("GBreference")
      text must include("We have sent your confirmation email to marisa@example.test")
    }
  }

}
