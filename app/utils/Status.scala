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

import java.time.Instant

import models.CaseStatus.CaseStatus
import models.{Case, CaseStatus}


object Status {

  def applicationFormat: CaseStatus => String = {
    case CaseStatus.NEW | CaseStatus.OPEN | CaseStatus.SUSPENDED => "In progress"
    case CaseStatus.SUPPRESSED | CaseStatus.REJECTED => "Rejected"
    case CaseStatus.REFERRED => "Info requested"
    case CaseStatus.COMPLETED => "Completed"
    case _ => "Unknown"
  }

  def hasActiveDecision = { c: Case =>
    c.decision.flatMap(_.effectiveEndDate).exists(_.compareTo(Instant.now) >= 0)
  }

  def hasExpiredDecision = { c: Case =>
    c.decision.flatMap(_.effectiveEndDate).exists(_.compareTo(Instant.now) < 0)
  }

  def rulingFormat(c: Case) = {
    c.status match {
      case CaseStatus.CANCELLED => "Cancelled"
      case CaseStatus.COMPLETED if hasActiveDecision(c) => "Active"
      case CaseStatus.COMPLETED if hasExpiredDecision(c) => "Expired"
      case _ => "Unknown"
    }
  }

}
