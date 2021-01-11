/*
 * Copyright 2021 HM Revenue & Customs
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

import forms.AddAnotherRulingFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.addAnotherRuling

class AddAnotherRulingViewSpec extends YesNoViewBehaviours {

  private lazy val messageKeyPrefix = "addAnotherRuling"

  override protected val form = new AddAnotherRulingFormProvider()()

  private def createView: () => HtmlFormat.Appendable = { () => createViewWithForm(form) }

  private def createViewWithForm(f: Form[Boolean], rulings: List[String] = List.empty): HtmlFormat.Appendable =
    addAnotherRuling(frontendAppConfig, form, NormalMode, rulings)(fakeRequest, messages)

  "AddAnotherRuling view" must {

    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    "show the expected heading when no rulings have been added" in {
      assertHeading(0)
    }

    "show the expected heading when 1 ruling has been added" in {
      assertHeading(1)
    }

    "show the expected heading when multiple rulings have been added" in {
      assertHeading(2)
    }

  }

  private def assertHeading: Int => Unit = { n: Int =>
    val rulingForm = new AddAnotherRulingFormProvider
    val htmlView = asDocument(createViewWithForm(form, generateRulings(n)))

    val headings = htmlView.getElementsByTag("h1")
    assert(headings.size() == 1)
    val heading = headings.first().ownText()

    if (n == 1) {
      assert(heading == messages(s"${messageKeyPrefix}.addRulingCounter.singular", n))
    } else if (n > 1) {
      assert(heading == messages(s"${messageKeyPrefix}.addRulingCounter.plural", n))
    } else {
      assert(heading == messages(s"${messageKeyPrefix}.heading"))
    }
  }

  private def generateRulings(number: Int): List[String] = {
    (1 to number).map { idx =>
      s"ruling$idx"
    }
      .toList
  }
}
