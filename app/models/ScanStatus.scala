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

enum ScanStatus {
  case READY, FAILED
}

object ScanStatus {
  implicit val format: Format[ScanStatus] = new Format[ScanStatus] {
    override def reads(json: JsValue): JsResult[ScanStatus] =
      json.validate[String].flatMap { s =>
        try
          JsSuccess(ScanStatus.valueOf(s))
        catch {
          case _: IllegalArgumentException => JsError(s"Unknown ScanStatus: $s")
        }
      }

    override def writes(status: ScanStatus): JsValue = JsString(status.toString)
  }
}
