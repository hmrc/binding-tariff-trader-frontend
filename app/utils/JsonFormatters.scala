/*
 * Copyright 2018 HM Revenue & Customs
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

import models._
import play.api.libs.json._

object JsonFormatters {

  implicit val caseStatusFormat = EnumJson.format(CaseStatus)
  implicit val contactFormat = Json.format[Contact]
  implicit val eoriDetailsFormat = Json.format[EORIDetails]
  implicit val applicationFormat = Json.format[Application]
  implicit val caseFormat = Json.format[Case]
  implicit val newCaseRequestFormat = Json.format[NewCaseRequest]
}

object EnumJson {

  def format[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(Reads.enumNameReads(enum), Writes.enumNameWrites)
  }

}
