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

import play.api.libs.json._
import viewmodels.RadioOption
import pages.Page

sealed trait SelectApplicationType extends Page

object SelectApplicationType {

  case object NewCommodity extends WithName("newCommodity") with SelectApplicationType
  case object PreviousCommodity extends WithName("previousCommodity") with SelectApplicationType

  val values: Set[SelectApplicationType] = Set(
    NewCommodity, PreviousCommodity
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("selectApplicationType", value.toString)
  }

  implicit val enumerable: Enumerable[SelectApplicationType] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit object SelectApplicationTypeWrites extends Writes[SelectApplicationType] {
    def writes(selectApplicationType: SelectApplicationType): JsValue = Json.toJson(selectApplicationType.toString)
  }

  implicit object SelectApplicationTypeReads extends Reads[SelectApplicationType] {
    override def reads(json: JsValue): JsResult[SelectApplicationType] = json match {
      case JsString(NewCommodity.toString) => JsSuccess(NewCommodity)
      case JsString(PreviousCommodity.toString) => JsSuccess(PreviousCommodity)
      case _ => JsError("Unknown selectApplicationType")
    }
  }
}
