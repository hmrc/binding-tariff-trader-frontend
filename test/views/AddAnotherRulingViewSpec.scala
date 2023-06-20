/*
 * Copyright 2023 HM Revenue & Customs
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

import forms.AddAnotherRulingFormProvider
import models.NormalMode
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.addAnotherRuling

class AddAnotherRulingViewSpec extends YesNoViewBehaviours {

  private lazy val messageKeyPrefix = "addAnotherRuling"

  override protected val form = new AddAnotherRulingFormProvider()()

  val addAnotherRulingView: addAnotherRuling = app.injector.instanceOf[addAnotherRuling]

  val viewViaApply: () => HtmlFormat.Appendable = () =>
    addAnotherRulingView(frontendAppConfig, form, NormalMode, List.empty)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable = () =>
    addAnotherRulingView.render(frontendAppConfig, form, NormalMode, List.empty, fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => addAnotherRulingView.f(frontendAppConfig, form, NormalMode, List.empty)(fakeRequest, messages)

  val viewWithRulings: List[String] => HtmlFormat.Appendable = (rulings: List[String]) => {
    addAnotherRulingView(frontendAppConfig, form, NormalMode, rulings)(fakeRequest, messages)
  }

  "AddAnotherRuling view" when {
    def test(method: String, view: () => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix)()

        behave like pageWithBackLink(view)
      }

    val input: Seq[(String, () => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))

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
    new AddAnotherRulingFormProvider
    val htmlView = asDocument(viewWithRulings(generateRulings(n)))

    val headings = htmlView.getElementsByTag("h1")
    assert(headings.size() == 1)
    val heading = headings.first().ownText()

    if (n == 1) {
      assert(heading == messages(s"$messageKeyPrefix.addRulingCounter.singular", n))
    } else if (n > 1) {
      assert(heading == messages(s"$messageKeyPrefix.addRulingCounter.plural", n))
    } else {
      assert(heading == messages(s"$messageKeyPrefix.heading"))
    }
  }

  private def generateRulings(number: Int): List[String] =
    (1 to number).map(idx => s"ruling$idx").toList
}
