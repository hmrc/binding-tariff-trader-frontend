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

package utils

import audit.CaseAuditPayload
import models._
import models.requests.NewEventRequest
import play.api.libs.json._
import uk.gov.hmrc.play.json.Union
import viewmodels.{FileView, PdfViewModel}

import scala.annotation.nowarn
import scala.language.implicitConversions
import scala.util.Try

object JsonFormatters {

  implicit val caseStatusFormat: Format[CaseStatus.Value]        = EnumJson.format(CaseStatus)
  implicit val contactFormat: OFormat[Contact]                   = Json.format[Contact]
  implicit val eoriDetailsFormat: OFormat[EORIDetails]           = Json.format[EORIDetails]
  implicit val agentDetailsFormat: OFormat[AgentDetails]         = Json.format[AgentDetails]
  implicit val applicationFormat: OFormat[Application]           = Json.format[Application]
  implicit val decisionFormat: OFormat[Decision]                 = Json.format[Decision]
  implicit val operator: Format[Operator]                        = Json.using[Json.WithDefaultValues].format[Operator]
  implicit val caseFormat: OFormat[Case]                         = Json.format[Case]
  implicit val newCaseRequestFormat: OFormat[NewCaseRequest]     = Json.format[NewCaseRequest]
  implicit val caseAuditPayloadFormat: OFormat[CaseAuditPayload] = Json.format[CaseAuditPayload]
  implicit val fileViewFormat: OFormat[FileView]                 = Json.format[FileView]
  implicit val pdfFormat: OFormat[PdfViewModel]                  = Json.format[PdfViewModel]

  implicit val formatCaseCreated: OFormat[CaseCreated] = Json.using[Json.WithDefaultValues].format[CaseCreated]
  implicit val formatEventDetail: Format[Details] = Union
    .from[Details]("type")
    .and[CaseCreated](EventType.CASE_CREATED.toString)
    .format

  implicit val eventFormat: OFormat[Event] = Json.format[Event]
  implicit val newEventRequestFormat: OFormat[NewEventRequest] =
    Json.using[Json.WithDefaultValues].format[NewEventRequest]

  implicit def formatSetValue[A: Format]: OFormat[SetValue[A]] = Json.format[SetValue[A]]
  @nowarn("msg=local val config\\$macro\\$30 in value formatNoChange is never used")
  implicit val formatNoChange: OFormat[NoChange.type] = Json.format[NoChange.type]

  implicit def formatUpdate[A: Format]: Format[Update[A]] =
    Union
      .from[Update[A]]("type")
      .and[SetValue[A]](UpdateType.SetValue.name)
      .and[NoChange.type](UpdateType.NoChange.name)
      .format

  implicit def formatApplicationUpdate: OFormat[ApplicationUpdate] = {
    implicit def optFormat[A: Format]: Format[Option[A]] = Format(
      Reads.optionNoError[A],
      Writes.optionWithNull[A]
    )
    Json.format[ApplicationUpdate]
  }
  implicit val formatCaseUpdate: OFormat[CaseUpdate] = Json.format[CaseUpdate]
}

object EnumJson {

  private def enumReads[E <: Enumeration](`enum`: E): Reads[E#Value] = {
    case JsString(s) =>
      Try(JsSuccess(enum.withName(s))).recover { case _: NoSuchElementException =>
        JsError(
          s"Expected an enumeration of type: '${enum.getClass.getSimpleName}', but it does not contain the name: '$s'"
        )
      }.get

    case _ => JsError("String value is expected")
  }

  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] = (v: E#Value) => JsString(v.toString)

  implicit def format[E <: Enumeration](`enum`: E): Format[E#Value] = Format(enumReads(enum), enumWrites)

}
