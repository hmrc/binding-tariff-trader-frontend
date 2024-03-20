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

import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import views.ViewMatchers.{containElementWithClass, containElementWithID}

trait PaginationViewBehaviours {
  this: ViewBehaviours =>

  protected def pageWithPaginationAndResults(paginationId: String, view: () => HtmlFormat.Appendable): Unit = {

    "display a paginated navigation section" when {
      "there is more than one application cases for the current page size" in {
        val docTwoAppsWithPagination: Document =
          asDocument(view())
        docTwoAppsWithPagination should containElementWithID("pagination-label")
      }

    }

    "display a page results section" when {
      "there is more than one application cases for the current page size" in {
        val docTwoAppsWithPagination: Document =
          asDocument(view())
        docTwoAppsWithPagination should containElementWithID(paginationId)
      }
    }
  }

  protected def pageWithNoPaginationAndResults(view: () => HtmlFormat.Appendable): Unit =
    "not display a page results section and navigation section" when {
      "there are no applications" in {
        val docWithNoApps: Document = asDocument(view())
        docWithNoApps shouldNot containElementWithID("pagination-results")
        docWithNoApps shouldNot containElementWithClass("hmrc-pagination")
      }
    }

  protected def pageWithResultsAndNoPagination(paginationId: String, view: () => HtmlFormat.Appendable): Unit =
    "display a page result section without navigation" when {
      "there is only one application" in {
        val docOneAppNoPagination: Document = asDocument(view())
        docOneAppNoPagination should containElementWithID(paginationId)

        docOneAppNoPagination shouldNot containElementWithClass("hmrc-pagination")
      }
    }
}
