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

package views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat

trait FileUploadViewBehaviours extends QuestionViewBehaviours[String] {

  private def getMessage(key: String): Option[String] = {
    messages(key) match {
      case m if m == key => None
      case s => Some(s)
    }
  }

  private def expectedLabel(messageKeyPrefix: String): String = {
    getMessage(s"$messageKeyPrefix.label") match {
      case Some(l) => l
      case _ => getMessage(s"$messageKeyPrefix.heading") match {
        case Some(h) => h
        case _ => "no label or value defined"
      }
    }
  }

  def multipleFileUploadPage(createView: Form[String] => HtmlFormat.Appendable,
                             messageKeyPrefix: String,
                             expectedFormAction: String,
                             expectedHintKey: Option[String] = None): Unit = {

    "behave like a page with a file upload field" when {

      "rendered" must {

        "contain a label for the value" in {
          val doc = asDocument(createView(form))
          val expectedHintText = expectedHintKey map (k => messages(k))

          assertContainsLabel(doc, "file", expectedLabel(messageKeyPrefix), expectedHintText)
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(form))
          assertRenderedById(doc, "file")
        }
      }

      "rendered with an error" must {

        "show an error prefix in the browser title" in {
          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(doc, "title", s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title")}""")
        }
      }
    }
  }

  def singleFileUploadPage(createView: Form[String] => HtmlFormat.Appendable,
                           messageKeyPrefix: String,
                           expectedFormAction: String,
                           expectedHintKey: Option[String] = None): Unit = {

    "behave like a page with a file upload field" when {

      "rendered" must {

        "contain a label for the value" in {
          val doc = asDocument(createView(form))
          val expectedHintText = expectedHintKey map (k => messages(k))

          assertContainsLabel(doc, "file-input", expectedLabel(messageKeyPrefix), expectedHintText)
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(form))
          assertRenderedById(doc, "file-input")
        }
      }

      "rendered with an error" must {

        "show an error summary" in {
          val doc = asDocument(createView(form.withError(error)))
          assertRenderedById(doc, "error-summary-heading")
        }

        "show an error prefix in the browser title" in {
          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(doc, "title", s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title")}""")
        }
      }
    }
  }

}
