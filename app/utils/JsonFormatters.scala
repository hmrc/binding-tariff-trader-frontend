/*
 * Copyright 2019 HM Revenue & Customs
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
import models.response.FilestoreResponse
import play.api.libs.json._

object JsonFormatters {

  implicit val reviewStatusFormat: Format[ReviewStatus.Value] = EnumJson.format(ReviewStatus)
  implicit val reviewFormat: Format[Review] = Json.format[Review]
  implicit val cancellationStatusFormat: Format[CancelReason.Value] = EnumJson.format(CancelReason)
  implicit val cancellationFormat: Format[Cancellation] = Json.format[Cancellation]
  implicit val appealStatusFormat: Format[AppealStatus.Value] = EnumJson.format(AppealStatus)
  implicit val appealFormat: Format[Appeal] = Json.format[Appeal]
  implicit val decisionFormat: Format[Decision] = Json.format[Decision]
  implicit val caseStatusFormat: Format[CaseStatus.Value] = EnumJson.format(CaseStatus)
  implicit val contactFormat: OFormat[Contact] = Json.format[Contact]
  implicit val eoriDetailsFormat: OFormat[EORIDetails] = Json.format[EORIDetails]
  implicit val agentDetailsFormat: OFormat[AgentDetails] = Json.format[AgentDetails]
  implicit val applicationFormat: OFormat[Application] = Json.format[Application]
  implicit val caseFormat: OFormat[Case] = Json.format[Case]
  implicit val newCaseRequestFormat: OFormat[NewCaseRequest] = Json.format[NewCaseRequest]
  implicit val caseAuditPayloadFormat: OFormat[CaseAuditPayload] = Json.format[CaseAuditPayload]
}

object EnumJson {

  def format[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(Reads.enumNameReads(enum), Writes.enumNameWrites)
  }

}
