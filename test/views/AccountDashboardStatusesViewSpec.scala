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

package views

import models.{Case, Paged, Sort, oCase}
import play.api.Application
import play.twirl.api.HtmlFormat
import viewmodels.Dashboard
import views.behaviours.{PaginationViewBehaviours, ViewBehaviours}
import views.html.account_dashboard_statuses

class AccountDashboardStatusesViewSpec extends ViewBehaviours with PaginationViewBehaviours {

  val emptyPaged: Paged[Case]        = Paged.empty[Case]
  val pagedNoPagination: Paged[Case] = Paged(Seq(oCase.btiCaseExample))
  val paginationIdOneResult          = "bottom-applications-pagination-one-result"
  val paginationIdMultipleResult     = "bottom-applications-pagination-some-result"

  override implicit lazy val app: Application = baseConfigBuilder
    .configure(
      "toggle.samplesNotAccepted" -> false
    )
    .build()

  def accountDashboardStatusesView: account_dashboard_statuses = app.injector.instanceOf[account_dashboard_statuses]

  def applicationView(dashboard: Dashboard, isBTAUser: Boolean = false): () => HtmlFormat.Appendable =
    () => accountDashboardStatusesView(frontendAppConfig, dashboard, isBTAUser)(fakeRequest, messages)

  "no previous applications view" must {
    behave like normalPage(applicationView(Dashboard(emptyPaged, Sort())), "index")()
  }

  "has previous applications view" must {
    behave like normalPage(applicationView(Dashboard(pagedNoPagination, Sort())), "index")()
  }

  "Applications and ruling view" must {
    behave like pageWithPaginationAndResults(
      paginationIdMultipleResult,
      applicationView(
        Dashboard(
          Paged(Seq(oCase.btiCaseExample, oCase.btiCaseExample), pageIndex = 1, pageSize = 1, resultCount = 2),
          Sort()
        )
      )
    )

    behave like pageWithNoPaginationAndResults(applicationView(Dashboard(emptyPaged, Sort())))
    behave like pageWithResultsAndNoPagination(
      paginationIdOneResult,
      applicationView(Dashboard(Paged(Seq(oCase.btiCaseExample)), Sort()))
    )

    "display a return to BTA link" when {
      "a user has arrived from BTA" in {
        val isBTAUser = true
        val doc       = asDocument(applicationView(Dashboard(emptyPaged, Sort()), isBTAUser)())

        assertLinkContainsHrefAndText(
          doc,
          "bta-return-link",
          controllers.routes.BTARedirectController.redirectToBTA.url,
          messages("index.bta.link")
        )
      }
    }

    "not display a return to BTA link" when {
      "a user has not arrived from BTA" in {
        val doc = asDocument(applicationView(Dashboard(emptyPaged, Sort()))())
        assertNotRenderedById(doc, "bta-return-link")
      }
    }

    "show message to say no previous applications when there are none supplied" in {
      val doc = asDocument(applicationView(Dashboard(emptyPaged, Sort()))())
      assertContainsText(doc, messages("index.noapplications"))
    }

    "show table containing previous applications when there are some" in {
      val doc = asDocument(applicationView(Dashboard(pagedNoPagination, Sort()))())
      assert(!doc.html().contains(messages("index.noapplications")))
      assertRenderedById(doc, "applications-rulings-list")
    }
  }
}
