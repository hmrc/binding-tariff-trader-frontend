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

import play.twirl.api.HtmlFormat
import views.ViewSpecBase

trait ViewBehaviours extends ViewSpecBase {

  val expectTimeoutDialog: Boolean = true

  protected def normalPage(view: () => HtmlFormat.Appendable, messageKeyPrefix: String, messageHeadingArgs: Any*)(
    expectedGuidanceKeys: String*
  ): Unit =
    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc         = asDocument(view())
          val bannerTitle = doc.select(".govuk-service-navigation__text")
          bannerTitle.text shouldBe messages("service.name")
        }

        "display the correct browser title" in {
          val doc = asDocument(view())
          assertEqualsMessage(doc, "title", s"$messageKeyPrefix.title")
        }

        "display the correct page title" in {
          val doc = asDocument(view())
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", messageHeadingArgs*)
        }

        "display the correct guidance" in {
          val doc = asDocument(view())
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }
        "contain a timeout dialog" in {
          val timeoutElm = asDocument(view()).select("meta[name=\"hmrc-timeout-dialog\"]")
          if (expectTimeoutDialog) {
            assert(timeoutElm.size == 1)
          } else {
            assert(timeoutElm.size == 0)
          }
        }
      }
    }

  protected def pageWithBackLink(view: () => HtmlFormat.Appendable): Unit =
    "behave like a page with a back link" must {
      "have a back link" in {
        val doc = asDocument(view())
        assertRenderedById(doc, "back-link")
      }
    }

  protected def pageWithoutBackLink(view: () => HtmlFormat.Appendable): Unit =
    "behave like a page without a back link" must {
      "not have a back link" in {
        val doc = asDocument(view())
        assertNotRenderedById(doc, "back-link")
      }
    }

  protected def pageWithCancelApplicationLink(view: () => HtmlFormat.Appendable): Unit =
    "behave like a page with a cancel application link" must {
      "have a cancel application link" in {
        val doc = asDocument(view())
        assertRenderedById(doc, "cancel-application-link")
      }
    }

  protected def pageWithoutCancelApplicationLink(view: () => HtmlFormat.Appendable): Unit =
    "behave like a page without a cancel application link" must {
      "not have a cancel application link" in {
        val doc = asDocument(view())
        assertNotRenderedById(doc, "cancel-application-link")
      }
    }
}
