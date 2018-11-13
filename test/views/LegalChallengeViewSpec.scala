/*
 * Copyright 2018 HM Revenue & Customs
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

import play.api.data.Form
import forms.LegalChallengeFormProvider
import models.NormalMode
import models.LegalChallenge
import views.behaviours.ViewBehaviours
import views.html.legalChallenge

class LegalChallengeViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "legalChallenge"

  val form = new LegalChallengeFormProvider()()

  def createView = () => legalChallenge(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => legalChallenge(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "LegalChallenge view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  "LegalChallenge view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- LegalChallenge.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- LegalChallenge.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- LegalChallenge.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
