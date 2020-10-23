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

package utils

import audit.CaseAuditPayload
import models._
import models.requests.NewEventRequest
import play.api.libs.json._
import uk.gov.hmrc.play.json.Union
import viewmodels.{FileView, PdfViewModel}

object JsonFormatters {

  implicit val caseStatusFormat: Format[CaseStatus.Value] = EnumJson.format(CaseStatus)
  implicit val contactFormat: OFormat[Contact] = Json.format[Contact]
  implicit val eoriDetailsFormat: OFormat[EORIDetails] = Json.format[EORIDetails]
  implicit val agentDetailsFormat: OFormat[AgentDetails] = Json.format[AgentDetails]
  implicit val applicationFormat: OFormat[Application] = Json.format[Application]
  implicit val decisionFormat: OFormat[Decision] = Json.format[Decision]
  implicit val caseFormat: OFormat[Case] = Json.format[Case]
  implicit val newCaseRequestFormat: OFormat[NewCaseRequest] = Json.format[NewCaseRequest]
  implicit val caseAuditPayloadFormat: OFormat[CaseAuditPayload] = Json.format[CaseAuditPayload]
  implicit val operator: Format[Operator] = Json.using[Json.WithDefaultValues].format[Operator]
  implicit val fileViewFormat: OFormat[FileView] = Json.format[FileView]
  implicit val pdfFormat: OFormat[PdfViewModel] = Json.format[PdfViewModel]

  implicit val formatCaseCreated: OFormat[CaseCreated] = Json.using[Json.WithDefaultValues].format[CaseCreated]
  implicit val formatEventDetail: Format[Details] = Union.from[Details]("type")
    .and[CaseCreated](EventType.CASE_CREATED.toString)
    .format

  implicit val eventFormat: OFormat[Event] = Json.format[Event]
  implicit val newEventRequestFormat: OFormat[NewEventRequest] = Json.using[Json.WithDefaultValues].format[NewEventRequest]

}

object EnumJson {

  def format[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(Reads.enumNameReads(enum), Writes.enumNameWrites)
  }

}
