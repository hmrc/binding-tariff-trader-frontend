/*
 * Copyright 2023 HM Revenue & Customs
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

import models.EventType.EventType

import java.time.Instant

case class Event(
  id: String,
  details: Details,
  operator: Operator,
  caseReference: String,
  timestamp: Instant = Instant.now()
)

sealed trait Details {
  val `type`: EventType
}

case class CaseCreated(comment: String) extends Details {
  override val `type`: EventType = EventType.CASE_CREATED
}

object EventType extends Enumeration {
  type EventType = Value
  val CASE_CREATED: models.EventType.Value = Value
}
