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

package models

import play.api.libs.json.*

enum CaseStatus {
  case DRAFT, NEW, OPEN, SUPPRESSED, REFERRED, REJECTED, CANCELLED, SUSPENDED, COMPLETED, REVOKED, ANNULLED
}

object CaseStatus {
  implicit val format: Format[CaseStatus] = new Format[CaseStatus] {
    override def reads(json: JsValue): JsResult[CaseStatus] =
      json.validate[String].flatMap { s =>
        try
          JsSuccess(CaseStatus.valueOf(s))
        catch {
          case _: IllegalArgumentException => JsError(s"Unknown CaseStatus: $s")
        }
      }

    override def writes(status: CaseStatus): JsValue = JsString(status.toString)
  }
}
