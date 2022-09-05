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

package viewmodels

import models.SortDirection.{ASCENDING, DESCENDING}
import models.SortField.{CREATED_DATE, GOODS_NAME, REFERENCE}
import models.{Paged, SearchPagination, Sort, oCase}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Request
import unit.utils.UnitSpec

class DashboardSpec extends UnitSpec with MockitoSugar {

  "columnSortUrlFor" when {
    "supplied with a new column to sort by whose default ordering is ascending" should {
      "generate a url containing expected sortBy and (asc) order query params" in {
        val dashboard =
          Dashboard(Paged(Seq(oCase.btiCaseExample), SearchPagination(), resultCount = 1), Sort(CREATED_DATE))

        dashboard.columnSortUrlFor("reference") shouldBe "/advance-tariff-application/applications-and-rulings?page=1&sortBy=reference&order=asc"
      }
    }

    "supplied with a new column to sort by whose default ordering is descending" should {
      "generate a url containing expected sortBy and (desc) order query params" in {
        val dashboard =
          Dashboard(Paged(Seq(oCase.btiCaseExample), SearchPagination(), resultCount = 1), Sort(REFERENCE))

        dashboard.columnSortUrlFor("created-date") shouldBe "/advance-tariff-application/applications-and-rulings?page=1&sortBy=created-date&order=desc"
      }
    }

    "supplied with the current column to sort by" should {
      "generate a url containing expected sortBy and reversed order query params" in {
        val dashboard =
          Dashboard(Paged(Seq(oCase.btiCaseExample), SearchPagination(), resultCount = 1), Sort(REFERENCE, DESCENDING))

        dashboard.columnSortUrlFor("reference") shouldBe "/advance-tariff-application/applications-and-rulings?page=1&sortBy=reference&order=asc"
      }
    }

    "supplied with a new column to sort by and dashboard current page higher than 1" should {
      "generate a url containing page reset to 1" in {
        val dashboard =
          Dashboard(Paged(Seq(oCase.btiCaseExample), SearchPagination(page = 2), resultCount = 1), Sort(CREATED_DATE))

        dashboard.columnSortUrlFor("reference") shouldBe "/advance-tariff-application/applications-and-rulings?page=1&sortBy=reference&order=asc"
      }
    }

    "supplied with the current column to sort by and dashboard current page higher than 1" should {
      "generate a url containing page reset to 1 and reversed order" in {
        val dashboard = Dashboard(
          Paged(Seq(oCase.btiCaseExample), SearchPagination(page = 2), resultCount = 1),
          Sort(GOODS_NAME, ASCENDING)
        )

        dashboard.columnSortUrlFor("application.goodName") shouldBe "/advance-tariff-application/applications-and-rulings?page=1&sortBy=application.goodName&order=desc"
      }
    }

    "supplied with an invalid column to sort by" should {
      "throw NoSuchElementException when attempting to create SortField" in {
        val dashboard =
          Dashboard(Paged(Seq(oCase.btiCaseExample), SearchPagination(), resultCount = 1), Sort(CREATED_DATE))

        intercept[NoSuchElementException] {
          dashboard.columnSortUrlFor("referencee")
        }
      }
    }
  }

  "getSortBy" when {
    "supplied with a request containing a valid sortBy param value" should {
      "result in the expected SortField" in {
        implicit val request: Request[_] = mock[Request[_]]

        when(request.getQueryString(org.mockito.ArgumentMatchers.eq("sortBy"))).thenReturn(Some("application.goodName"))

        Dashboard.getSortBy.get shouldBe GOODS_NAME
      }
    }

    "supplied with a request containing an invalid sortBy param value" should {
      "throw NoSuchElementException when attempting to create SortField" in {
        implicit val request: Request[_] = mock[Request[_]]

        when(request.getQueryString(org.mockito.ArgumentMatchers.eq("sortBy")))
          .thenReturn(Some("application.goodsName"))

        intercept[NoSuchElementException] {
          Dashboard.getSortBy
        }
      }
    }

    "supplied with a request containing no sortBy param" should {
      "result in None" in {
        implicit val request: Request[_] = mock[Request[_]]

        when(request.getQueryString(org.mockito.ArgumentMatchers.eq("sortBy"))).thenReturn(None)

        Dashboard.getSortBy shouldBe None
      }
    }
  }

  "getSortDirection" when {
    "supplied with a request containing a valid order param value" should {
      "result in the expected SortDirection" in {
        implicit val request: Request[_] = mock[Request[_]]

        when(request.getQueryString(org.mockito.ArgumentMatchers.eq("order"))).thenReturn(Some("asc"))

        Dashboard.getSortDirection.get shouldBe ASCENDING
      }
    }

    "supplied with a request containing an invalid order param value" should {
      "throw NoSuchElementException when attempting to create SortDirection" in {
        implicit val request: Request[_] = mock[Request[_]]

        when(request.getQueryString(org.mockito.ArgumentMatchers.eq("order"))).thenReturn(Some("ascending"))

        intercept[NoSuchElementException] {
          Dashboard.getSortDirection
        }
      }
    }

    "supplied with a request containing no order param" should {
      "result in None" in {
        implicit val request: Request[_] = mock[Request[_]]

        when(request.getQueryString(org.mockito.ArgumentMatchers.eq("order"))).thenReturn(None)

        Dashboard.getSortDirection shouldBe None
      }
    }
  }
}
