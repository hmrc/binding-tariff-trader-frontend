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

sealed trait ReturnSamples

object ReturnSamples {

  case object Yes extends WithName("yesReturnSamples") with ReturnSamples
  case object No extends WithName("noDontReturnSamples") with ReturnSamples

  val values: Set[ReturnSamples] = Set(Yes, No)

  val options: Set[RadioOption] = values.map {
    value => RadioOption("returnSamples", value.toString)
  }

  implicit val enumerable: Enumerable[ReturnSamples] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit object ReturnSamplesWrites extends Writes[ReturnSamples] {
    def writes(returnSamples: ReturnSamples) = Json.toJson(returnSamples.toString)
  }

  implicit object ReturnSamplesReads extends Reads[ReturnSamples] {
    override def reads(json: JsValue): JsResult[ReturnSamples] = json match {
      case JsString(Yes.toString) => JsSuccess(Yes)
      case JsString(No.toString) => JsSuccess(No)
      case _ => JsError("Unknown returnSamples")
    }
  }
}
