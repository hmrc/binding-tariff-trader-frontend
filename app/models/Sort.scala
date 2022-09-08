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

package models

import cats.syntax.either._
import models.SortDirection.{ASCENDING, DESCENDING, SortDirection}
import models.SortField.{CREATED_DATE, SortField}
import play.api.mvc.QueryStringBindable

case class Sort(field: SortField, direction: SortDirection)

object Sort {

  def apply(): Sort =
    apply(CREATED_DATE)

  def apply(field: SortField, sortDirection: Option[SortDirection] = None): Sort =
    Sort(
      field,
      sortDirection.getOrElse(SortField.defaultDirections(field))
    )
}

object SortDirection extends Enumeration {

  type SortDirection = Value

  val DESCENDING: models.SortDirection.Value = Value("desc")
  val ASCENDING: models.SortDirection.Value  = Value("asc")

  def reverse(order: SortDirection): SortDirection =
    order match {
      case ASCENDING  => DESCENDING
      case DESCENDING => ASCENDING
      case o          => throw new IllegalArgumentException(s"Unrecognised SortDirection: $o")
    }

  implicit def queryStringBindable(
    implicit stringBinder: QueryStringBindable[String]
  ): QueryStringBindable[SortDirection] = new QueryStringBindable[SortDirection] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SortDirection]] =
      stringBinder.bind("order", params).map {
        case Right(sortDirection) =>
          Either
            .catchOnly[NoSuchElementException](SortDirection.withName(sortDirection))
            .leftMap(_ => "Unable to bind a SortDirection")
        case _ =>
          Left("Unable to bind a SortDirection")
      }

    override def unbind(key: String, sortDirection: SortDirection): String =
      stringBinder.unbind("order", sortDirection.toString)
  }
}

object SortField extends Enumeration {

  type SortField = Value
  val REFERENCE: models.SortField.Value           = Value("reference")
  val CREATED_DATE: models.SortField.Value        = Value("created-date")
  val DECISION_START_DATE: models.SortField.Value = Value("decision-start-date")
  val STATUS: models.SortField.Value              = Value("status")
  val GOODS_NAME: models.SortField.Value          = Value("application.goodName")

  val defaultDirections = Map(
    REFERENCE           -> ASCENDING,
    CREATED_DATE        -> DESCENDING,
    DECISION_START_DATE -> DESCENDING,
    STATUS              -> ASCENDING,
    GOODS_NAME          -> ASCENDING
  )

  implicit def queryStringBindable(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[SortField] =
    new QueryStringBindable[SortField] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SortField]] =
        stringBinder.bind("sortBy", params).map {
          case Right(sortByField) =>
            Either
              .catchOnly[NoSuchElementException](SortField.withName(sortByField))
              .leftMap(_ => "Unable to bind a SortField")
          case _ =>
            Left("Unable to bind a SortField")
        }

      override def unbind(key: String, sortField: SortField): String =
        stringBinder.unbind("sortBy", sortField.toString)
    }
}
