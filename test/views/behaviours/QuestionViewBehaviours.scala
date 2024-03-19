/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat

trait QuestionViewBehaviours[A] extends ViewBehaviours {

  private val errorKey                           = "value"
  protected val errorMessage                     = "error.number"
  protected val errorPrefix                      = "error.browser.title.prefix"
  protected def error: FormError                 = FormError(errorKey, errorMessage)
  protected def error(errKey: String): FormError = FormError(errKey, errorMessage)

  protected val form: Form[A]

  protected def pageWithTextFields(
    createView: Form[A] => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    fields: String*
  ): Unit =
    "behave like a question page" when {
      "rendered" must {
        for (field <- fields) {
          s"contain an input for $field" in {
            val doc = asDocument(createView(form))
            assertRenderedById(doc, field)
          }
        }

        "not render an error summary" in {
          val doc = asDocument(createView(form))
          assertNotRenderedById(doc, "error-summary-title")
        }
      }

      "rendered with any error" must {
        "show an error prefix in the browser title" in {
          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(doc, "title", s"""${messages("error.browser.title.prefix")} ${messages(
            s"$messageKeyPrefix.title"
          )}""")
        }
      }

      for (field <- fields) {
        s"rendered with an error with field '$field'" must {
          "show an error summary" in {
            val doc = asDocument(createView(form.withError(FormError(field, "error"))))
            assertRenderedById(doc, "error-summary-title")
          }

          s"show an error in the label for field '$field'" in {
            val doc       = asDocument(createView(form.withError(FormError(field, "error"))))
            val errorSpan = doc.getElementsByClass("govuk-error-message").first
            errorSpan.parent.getElementsByTag("label").attr("for") shouldBe field
          }
        }
      }

    }

}
