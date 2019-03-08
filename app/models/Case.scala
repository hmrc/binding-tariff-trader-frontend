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

package models

import java.time.Instant
import models.CaseStatus.CaseStatus

case class NewCaseRequest
(
  application: Application,
  attachments: Seq[Attachment] = Seq.empty
)

case class Case
(
  reference: String,
  status: CaseStatus,
  createdDate: Instant = Instant.now,
  application: Application,
  decision: Option[Decision] = None,
  attachments: Seq[Attachment] = Seq.empty
) {

  def hasEoriNumber(eoriNumber: String): Boolean = application.holder.eori == eoriNumber || application.agent.exists(_.eoriDetails.eori == eoriNumber)

  def hasActiveDecision: Boolean = this.decision.flatMap(_.effectiveEndDate).exists(_.compareTo(Instant.now) >= 0)

  def hasExpiredDecision: Boolean = this.decision.flatMap(_.effectiveEndDate).exists(_.compareTo(Instant.now) < 0)

  def hasRuling: Boolean = rulingStates.contains(status) && decision.isDefined

  private def rulingStates = Set(CaseStatus.COMPLETED, CaseStatus.CANCELLED)

}
