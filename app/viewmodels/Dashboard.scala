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

package viewmodels

import controllers.routes
import models.SortDirection.SortDirection
import models.SortField.{CREATED_DATE, SortField}
import models._
import play.api.mvc.Request
import viewmodels.Dashboard._

import java.net.URLEncoder.encode

case class Dashboard(pageData: Paged[Case], sort: Sort) {

  def columnSortUrlFor(columnName: String): String =
    baseUrl + queryParamsFor(columnName)

  private def queryParamsFor(columnName: String): String =
    this
      .copy(
        pageData = pageData.copy(pageIndex = 1),
        sort = Sort(SortField.withName(columnName), sortOrderFor(columnName))
      )
      .toQueryString

  private def toQueryString: String = {
    val query = toMap
      .map { case (k, v) =>
        encode(k, ENCODING) + "=" + encode(v, ENCODING)
      }
      .mkString("&")

    if (query.nonEmpty) "?" + query else ""
  }

  private def toMap: Map[String, String] =
    Map(
      pageParam      -> encode(pageData.pageIndex.toString, ENCODING),
      sortFieldParam -> encode(sort.field.toString, ENCODING),
      orderParam     -> encode(sort.direction.toString, ENCODING)
    )

  private def sortOrderFor(columnName: String): SortDirection = {
    def currentColumn: Boolean =
      sort.field.toString == columnName

    if (currentColumn) {
      SortDirection.reverse(sort.direction)
    } else {
      SortField.defaultDirections(SortField.withName(columnName))
    }
  }
}

object Dashboard {

  val defaultSortField: models.SortField.Value = CREATED_DATE

  private val baseUrl: String = routes.IndexController.getApplicationsAndRulings(sortBy = None, order = None).url

  private val pageParam: String      = "page"
  private val sortFieldParam: String = "sortBy"
  private val orderParam: String     = "order"

  private val ENCODING: String = "UTF-8"

  def getSortBy(implicit request: Request[_]): Option[SortField] =
    request.getQueryString(sortFieldParam).map(s => SortField.withName(s))

  def getSortDirection(implicit request: Request[_]): Option[SortDirection] =
    request.getQueryString(orderParam).map(s => SortDirection.withName(s))

}
