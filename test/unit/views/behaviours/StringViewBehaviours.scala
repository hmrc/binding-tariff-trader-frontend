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

package views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat

trait StringViewBehaviours extends QuestionViewBehaviours[String] {

  val answer = "answer"

  private def getMessage(key: String): Option[String] = {
    messages(key) match {
      case m if m == key => None
      case s => Some(s)
    }
  }

  private def expectedLabel(messageKeyPrefix: String): String = {
    (getMessage(s"$messageKeyPrefix.caption"),
      getMessage(s"$messageKeyPrefix.heading"),
      getMessage(s"$messageKeyPrefix.label")
    ) match {
      case (_,             _, Some(l)) => l
      case (Some(c), Some(h), _) => s"$c $h"
      case (_,       Some(h), _) => h
      case _ => ""
    }
  }

  protected def stringPage(createView: Form[String] => HtmlFormat.Appendable,
                           messageKeyPrefix: String,
                           expectedFormAction: String,
                           expectedHintKey: Option[String] = None): Unit = {

    "behave like a page with a string value field" when {

      "rendered" must {

        "contain a label for the value" in {
          val doc = asDocument(createView(form))
          val expectedHintText = expectedHintKey map (k => messages(k))

          assertContainsLabel(doc, "value",  expectedLabel(messageKeyPrefix), expectedHintText)
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(form))
          assertRenderedById(doc, "value")
        }
      }

      "rendered with a valid form" must {
        "include the form's value in the value input" in {
          val doc = asDocument(createView(form.fill(answer)))
          val input = doc.getElementById("value")
          if(input.tagName() == "textarea")
            input.html() shouldBe answer
          else
            input.attr("value") shouldBe answer
        }
      }

      "rendered with an error" must {

        "show an error summary" in {
          val doc = asDocument(createView(form.withError(error)))
          assertRenderedById(doc, "error-summary-heading")
        }

        "show an error in the value field's label" in {
          val doc = asDocument(createView(form.withError(error)))
          val errorSpan = doc.getElementsByClass("error-message").first
          errorSpan.text shouldBe messages(errorPrefix) + messages(errorMessage)
        }

        "show an error prefix in the browser title" in {
          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(doc, "title", s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title")}""")
        }
      }
    }
  }

  protected def textareaPage(createView: Form[String] => HtmlFormat.Appendable,
                             messageKeyPrefix: String,
                             expectedFormAction: String,
                             expectedHintKey: Option[String] = None): Unit = {

    "behave like a page with a string value field" when {

      "rendered" must {

        "contain a label for the value" in {
          val doc = asDocument(createView(form))
          val expectedHintText = expectedHintKey map (k => messages(k))

          assertContainsLabel(doc, "value",  expectedLabel(messageKeyPrefix), expectedHintText)
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(form))
          assertRenderedById(doc, "value")
        }
      }

      "rendered with a valid form" must {
        "include the form's value in the value input" in {
          val doc = asDocument(createView(form.fill(answer)))
          doc.getElementById("value").text() shouldBe answer
        }
      }

      "rendered with an error" must {

        "show an error summary" in {
          val doc = asDocument(createView(form.withError(error)))
          assertRenderedById(doc, "error-summary-heading")
        }

        "show an error in the value field's label" in {
          val doc = asDocument(createView(form.withError(error)))
          val errorSpan = doc.getElementsByClass("error-message").first
          errorSpan.text shouldBe messages(errorPrefix) + messages(errorMessage)
        }

        "show an error prefix in the browser title" in {
          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(doc, "title", s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title")}""")
        }
      }
    }
  }

}
