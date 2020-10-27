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

package unit.viewmodels

import models.SortDirection.DESCENDING
import models.SortField.{CREATED_DATE, REFERENCE}
import models.{Paged, SearchPagination, Sort, SortDirection, oCase}
import unit.utils.UnitSpec
import viewmodels.Dashboard

class DashboardSpec extends UnitSpec {

  "columnSortUrlFor" when {
    "supplied with a new column to sort by whose default ordering is ascending" should {
      "generate a url containing expected sortBy and (asc) order query params" in {
        val dashboard = Dashboard.create(Paged(Seq(oCase.btiCaseExample), SearchPagination(page = 1), resultCount = 1), Sort(CREATED_DATE))

        dashboard.columnSortUrlFor("reference") shouldBe "/applications-and-rulings?page=1&sortBy=reference&order=asc"
      }
    }

    "supplied with a new column to sort by whose default ordering is descending" should {
      "generate a url containing expected sortBy and (desc) order query params" in {
        val dashboard = Dashboard.create(Paged(Seq(oCase.btiCaseExample), SearchPagination(page = 1), resultCount = 1), Sort(REFERENCE))

        dashboard.columnSortUrlFor("created-date") shouldBe "/applications-and-rulings?page=1&sortBy=created-date&order=desc"
      }
    }

    "supplied with the current column to sort by" should {
      "generate a url containing expected sortBy and reversed order query params" in {
        val dashboard = Dashboard.create(Paged(Seq(oCase.btiCaseExample), SearchPagination(page = 1), resultCount = 1), Sort(REFERENCE, DESCENDING))

        dashboard.columnSortUrlFor("reference") shouldBe "/applications-and-rulings?page=1&sortBy=reference&order=asc"
      }
    }

    "supplied with a new column to sort by and dashboard current page higher than 1" should {
      "generate a url containing page param reset to 1" in {
        val dashboard = Dashboard.create(Paged(Seq(oCase.btiCaseExample), SearchPagination(page = 2), resultCount = 1), Sort(CREATED_DATE))

        dashboard.columnSortUrlFor("reference") shouldBe "/applications-and-rulings?page=1&sortBy=reference&order=asc"
      }
    }
  }
}