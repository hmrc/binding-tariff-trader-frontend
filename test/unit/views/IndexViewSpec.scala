/*
 * Copyright 2022 HM Revenue & Customs
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

import controllers.routes
import models.{Paged, oCase}
import play.twirl.api.{Html, HtmlFormat}
import unit.views.behaviours.PaginationViewBehaviours
import views.behaviours.ViewBehaviours
import views.html.components.{table_applications, table_rulings}
import views.html.index

class IndexViewSpec extends ViewBehaviours with PaginationViewBehaviours {

  val indexView: index = app.injector.instanceOf[index]
  val expectedContent: Html = Html.apply("expected-content")

  def applicationView(viewTable: Html = expectedContent):
    () => HtmlFormat.Appendable = () => indexView(frontendAppConfig, CaseDetailTab.APPLICATION, viewTable)(fakeRequest, messages)

  def rulingView(viewTable: Html = expectedContent): () => HtmlFormat.Appendable =
    () => indexView(frontendAppConfig, CaseDetailTab.RULING, viewTable)(fakeRequest, messages)

  "Load application view" must {
    val paginationAppIdOneResult = "bottom-applications-pagination-one-result"
    val paginationAppIdMultipleResult = "bottom-applications-pagination-some-result"

    val appWithResultsOnly: () => HtmlFormat.Appendable = applicationView(table_applications(Paged(Seq(oCase.btiCaseExample)))(messages))
    val appWithPaginationAndResults: () => HtmlFormat.Appendable = applicationView(table_applications(Paged(Seq(oCase.btiCaseExample,
      oCase.btiCaseExample), pageIndex = 1, pageSize = 1, resultCount = 2))(messages))

    behave like normalPage(applicationView(), "index")()
    behave like pageWithNoPaginationAndResults(applicationView())
    behave like pageWithResultsAndNoPagination(paginationAppIdOneResult, appWithResultsOnly)
    behave like pageWithPaginationAndResults(paginationAppIdMultipleResult, appWithPaginationAndResults)
  }

  "Load rulings view" must {
    val paginationRulingIdOneResult = "bottom-rulings-pagination-one-result"
    val paginationRulingIdMultipleResult = "bottom-rulings-pagination-some-result"

    val rulingsWithResultsOnly: () => HtmlFormat.Appendable = rulingView(table_rulings(Paged(Seq(oCase.btiCaseExample)))(messages))
    val rulingsWithPaginationAndResults: () => HtmlFormat.Appendable = rulingView(table_rulings(Paged(Seq(oCase.btiCaseExample,
      oCase.btiCaseExample), pageIndex = 1, pageSize = 1, resultCount = 2))(messages))

    behave like normalPage(rulingView(), "index")()
    behave like pageWithNoPaginationAndResults(applicationView())
    behave like pageWithResultsAndNoPagination(paginationRulingIdOneResult, rulingsWithResultsOnly)
    behave like pageWithPaginationAndResults(paginationRulingIdMultipleResult, rulingsWithPaginationAndResults)

  }

  "IndexView" must {
    "contain a start button that redirects to Information you need Page" in {
      val doc = asDocument(applicationView()())

      assertLinkContainsHref(doc, "start-application", routes.BeforeYouStartController.onPageLoad.url)
    }
  }
}
