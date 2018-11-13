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

package models

import play.api.libs.json._
import viewmodels.RadioOption
import pages.Page

sealed trait SupportingInformation extends Page

object SupportingInformation {

  case object Yesinformation extends WithName("yesInformation") with SupportingInformation
  case object Noinformation extends WithName("noInformation") with SupportingInformation

  val values: Set[SupportingInformation] = Set(
    Yesinformation, Noinformation
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("supportingInformation", value.toString)
  }

  implicit val enumerable: Enumerable[SupportingInformation] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit object SupportingInformationWrites extends Writes[SupportingInformation] {
    def writes(supportingInformation: SupportingInformation) = Json.toJson(supportingInformation.toString)
  }

  implicit object SupportingInformationReads extends Reads[SupportingInformation] {
    override def reads(json: JsValue): JsResult[SupportingInformation] = json match {
      case JsString(Yesinformation.toString) => JsSuccess(Yesinformation)
      case JsString(Noinformation.toString) => JsSuccess(Noinformation)
      case _                          => JsError("Unknown supportingInformation")
    }
  }
}
