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

package views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat

trait YesNoViewBehaviours extends BooleanViewBehaviours[Boolean] {
  def yesNoPage(
    createView: Form[Boolean] => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    expectedFormAction: String,
    idPrefix: String = "value"
  ): Unit =
    booleanPage(
      createView,
      identity,
      messageKeyPrefix,
      idPrefix
    )(true, false)

}

trait BooleanViewBehaviours[T] extends QuestionViewBehaviours[T] {

  protected def expectedLegend(messageKeyPrefix: String): String = {
    def has(value: String): Boolean = {
      val key = s"$messageKeyPrefix.$value"
      messages(key) != key
    }
    if (has("legend")) {
      messages(s"$messageKeyPrefix.legend")
    } else {
      messages(s"$messageKeyPrefix.heading")
    }
  }

  protected def booleanPage(
    createView: Form[T] => HtmlFormat.Appendable,
    choiceFrom: T => Boolean,
    messageKeyPrefix: String,
    idPrefix: String = "value"
  )(choices: T*): Unit =
    "behave like a page with a Yes/No question" when {

      renderWithoutError(createView, messageKeyPrefix, idPrefix)

      renderWithError(createView, messageKeyPrefix, idPrefix)

      choices.foreach { choice =>
        s"rendered with a value of ${choiceFrom(choice)}" must {
          behave like answeredYesNoPage(createView, choiceFrom, answer = choice, idPrefix = idPrefix)
        }
      }
    }

  private def renderWithoutError(
    createView: Form[T] => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    idPrefix: String
  ): Unit =
    "rendered" must {
      "contain a legend for the question" in {
        val doc     = asDocument(createView(form))
        val legends = doc.getElementsByTag("legend")

        legends.size     shouldBe 1
        legends.first.text should include(expectedLegend(messageKeyPrefix))
      }

      "contain an input for the value" in {
        val doc = asDocument(createView(form))
        assertRenderedById(doc, s"$idPrefix-yes")
        assertRenderedById(doc, s"$idPrefix-no")
      }

      "have no values checked when rendered with no form" in {
        val doc = asDocument(createView(form))
        assert(!doc.getElementById(s"$idPrefix-yes").hasAttr("checked"))
        assert(!doc.getElementById(s"$idPrefix-no").hasAttr("checked"))
      }

      "not render an error summary" in {
        val doc = asDocument(createView(form))
        assertNotRenderedById(doc, "error-summary_header")
      }
    }

  private def renderWithError(
    createView: Form[T] => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    idPrefix: String
  ): Unit =
    "rendered with an error" must {
      "show an error summary" in {
        val doc = asDocument(createView(form.withError(error)))
        assertRenderedById(doc, "error-summary-title")
      }

      "show an error in the value field's label" in {
        val doc       = asDocument(createView(form.withError(error(idPrefix))))
        val errorSpan = doc.getElementsByClass("govuk-error-message").first
        errorSpan.text shouldBe messages(errorPrefix) + messages(errorMessage)
      }

      "show an error prefix in the browser title" in {
        val doc = asDocument(createView(form.withError(error)))
        assertEqualsValue(
          doc,
          "title",
          s"""${messages("error.browser.title.prefix")} ${messages(
              s"$messageKeyPrefix.title"
            )}"""
        )
      }
    }

  private def answeredYesNoPage(
    createView: Form[T] => HtmlFormat.Appendable,
    choiceFrom: T => Boolean,
    answer: T,
    idPrefix: String
  ): Unit = {

    "have only the correct value checked" in {
      val doc = asDocument(createView(form.fill(answer)))
      assert(doc.getElementById(s"$idPrefix-yes").hasAttr("checked") == choiceFrom(answer))
      assert(doc.getElementById(s"$idPrefix-no").hasAttr("checked") != choiceFrom(answer))
    }

    "not render an error summary" in {
      val doc = asDocument(createView(form.fill(answer)))
      assertNotRenderedById(doc, "error-summary_header")
    }
  }

}
