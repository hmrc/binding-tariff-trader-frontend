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

import play.api.data.Form
import forms.WhichBestDescribesYouFormProvider
import models.NormalMode
import models.WhichBestDescribesYou
import views.behaviours.ViewBehaviours
import views.html.whichBestDescribesYou

class WhichBestDescribesYouViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whichBestDescribesYou"

  val form = new WhichBestDescribesYouFormProvider()()

  def createView = () => whichBestDescribesYou(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => whichBestDescribesYou(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "WhichBestDescribesYou view" must {
    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)
  }

  "WhichBestDescribesYou view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- WhichBestDescribesYou.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- WhichBestDescribesYou.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- WhichBestDescribesYou.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
