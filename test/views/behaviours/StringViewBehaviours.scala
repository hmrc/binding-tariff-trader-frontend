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

trait StringViewBehaviours extends QuestionViewBehaviours[String] {

  private val answer = "answer"

  private def getMessage(key: String, args: String*): Option[String] =
    messages(key, args*) match {
      case m if m == key => None
      case s             => Some(s)
    }

  private def getExpectedLabel(messageKeyPrefix: String, args: String*): String =
    getMessage(s"$messageKeyPrefix.label") match {
      case Some(l) => l
      case _ =>
        getMessage(s"$messageKeyPrefix.heading", args*) match {
          case Some(h) => h
          case _       => ""
        }
    }

  protected def stringPage(
    createView: Form[String] => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    expectedFormAction: String,
    expectedHintKey: Option[String] = None,
    forElement: String = "value"
  ): Unit =
    "behave like a page with a string value field" when {

      "rendered" must {

        "contain a label for the value" in {
          val doc              = asDocument(createView(form))
          val expectedHintText = expectedHintKey map (k => messages(k))

          assertContainsLabel(doc, forElement, getExpectedLabel(messageKeyPrefix), expectedHintText)
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(form))
          assertRenderedById(doc, forElement)
        }
      }

      "rendered with a valid form" must {
        "include the form's value in the value input" in {
          val doc   = asDocument(createView(form.fill(answer)))
          val input = doc.getElementById(forElement)
          if (input.tagName() == "textarea") {
            input.html() shouldBe answer
          } else {
            input.attr("value") shouldBe answer
          }
        }
      }

      renderWithError(createView, messageKeyPrefix, forElement)
    }

  protected def textAreaPage(
    createView: Form[String] => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    expectedFormAction: String,
    expectedFormElementId: String = "value",
    messageArgs: Seq[String] = Nil,
    expectedHintKey: Option[String] = None
  ): Unit =
    "behave like a page with a string value field" when {

      "rendered" must {

        "contain a label for the value" in {
          val doc              = asDocument(createView(form))
          val expectedHintText = expectedHintKey map (k => messages(k))
          val expectedLabel    = getExpectedLabel(messageKeyPrefix, messageArgs*)

          assertContainsLabel(doc, expectedFormElementId, expectedLabel, expectedHintText)
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(form))
          assertRenderedById(doc, expectedFormElementId)
        }
      }

      "rendered with a valid form" must {
        "include the form's value in the value input" in {
          val doc = asDocument(createView(form.fill(answer)))
          doc.getElementById(expectedFormElementId).text() shouldBe answer
        }
      }

      renderWithError(createView, messageKeyPrefix, expectedFormElementId)
    }

  private def renderWithError(
    createView: Form[String] => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    errorKey: String
  ): Unit =
    "rendered with an error" must {
      "show an error summary" in {
        val doc = asDocument(createView(form.withError(error(errorKey))))
        assertRenderedById(doc, "error-summary-title")
      }

      "show an error in the value field's label" in {
        val doc       = asDocument(createView(form.withError(error(errorKey))))
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

}
