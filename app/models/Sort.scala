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

package models

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

  val DESCENDING = Value("desc")
  val ASCENDING = Value("asc")

  def reverse(order: SortDirection): SortDirection =
    order match {
      case ASCENDING  => DESCENDING
      case DESCENDING => ASCENDING
      case o          => throw new IllegalArgumentException(s"Unrecognised SortDirection: $o")
    }

  implicit def queryStringBindable(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[SortDirection] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SortDirection]] = {
      for {
        order <- stringBinder.bind("order", params)
      } yield {
        order match {
          case Right(sortDirection) => Right(SortDirection.withName(sortDirection))
          case _                    => Left("Unable to bind a SortDirection")
        }
      }
    }

    override def unbind(key: String, sortDirection: SortDirection): String = {
      stringBinder.unbind("order", sortDirection.toString)
    }
  }
}

object SortField extends Enumeration {

  type SortField = Value
  val REFERENCE = Value("reference")
  val CREATED_DATE = Value("created-date")
  val DECISION_START_DATE = Value("decision-start-date")
  val APPLICATION_STATUS = Value("application.status")
  val GOODS_NAME = Value("application.goodName")

  val defaultDirections = Map(
    REFERENCE -> ASCENDING,
    CREATED_DATE -> DESCENDING,
    DECISION_START_DATE -> DESCENDING,
    APPLICATION_STATUS -> ASCENDING,
    GOODS_NAME -> ASCENDING
  )

  implicit def queryStringBindable(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[SortField] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SortField]] = {
      for {
        sortBy <- stringBinder.bind("sortBy", params)
      } yield {
        sortBy match {
          case Right(sortByField) => Right(SortField.withName(sortByField))
          case _                  => Left("Unable to bind a SortField")
        }
      }
    }

    override def unbind(key: String, sortField: SortField): String = {
      stringBinder.unbind("sortBy", sortField.toString)
    }
  }
}