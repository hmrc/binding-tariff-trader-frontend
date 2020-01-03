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

import models.SortDirection.SortDirection
import models.SortField.SortField


object SortDirection extends Enumeration {
  type SortDirection = Value
  val DESCENDING = Value("desc")
  val ASCENDING = Value("asc")
}

object SortField extends Enumeration {
  type SortField = Value
  val CREATED_DATE = Value("created-date")
  val DECISION_START_DATE = Value("decision-start-date")
}

case class Sort
(
  field: SortField = SortField.CREATED_DATE,
  direction: SortDirection = SortDirection.DESCENDING
)