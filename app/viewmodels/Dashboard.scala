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

package viewmodels

import java.net.URLEncoder.encode

import controllers.routes
import models.SortDirection.{ASCENDING, SortDirection}
import models.SortField.{CREATED_DATE, SortField}
import models.{SortField, _}
import play.api.mvc.Request
import viewmodels.Dashboard.{baseUrl, orderParam, pageParam, sortFieldParam}

class Dashboard(val pageData: Paged[Case], sort: Sort) {

  def columnSortUrlFor(columnName: String): String =
    baseUrl + queryParamsFor(columnName)

  private def queryParamsFor(columnName: String): String =
    Dashboard
      .create(pageData, Sort(SortField.withName(columnName), sortOrderFor(columnName)))
      .toQueryString

  private def toQueryString: String = {
    val query = toMap.map { case (k,v) =>
      encode(k, "UTF8") + "=" + encode(v, "UTF8")
    }.mkString("&")

    if(query.length > 0) "?" + query else ""
  }

  private def toMap: Map[String, String] = {
    Map(
      pageParam -> encode(pageData.pageIndex.toString, "utf-8"),
      sortFieldParam -> encode(sort.field.toString, "utf-8"),
      orderParam -> encode(sort.direction.toString, "utf-8")
    )
  }

  private def sortOrderFor(columnName: String): SortDirection = {
    def currentColumn: Boolean =
      sort.field.toString == columnName

    if (currentColumn)
      SortDirection.reverse(sort.direction)
    else
      ASCENDING
  }
}

object Dashboard {

  val defaultSortField = CREATED_DATE

  private val baseUrl = routes.IndexController.getApplicationsAndRulings(sortBy = None, order = None).url

  private val pageParam = "page"
  private val sortFieldParam = "sortBy"
  private val orderParam = "order"

  def create(pageData: Paged[Case], sort: Sort): Dashboard =
    new Dashboard(pageData, sort)

  def getSortBy(implicit request: Request[_]): Option[SortField] =
    request.getQueryString("sortBy").map(s => SortField.withName(s))

  def getSortDirection(implicit request: Request[_]): Option[SortDirection] =
    request.getQueryString("order").map(s => SortDirection.withName(s))

}






