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

sealed trait WhenToSendSample extends Page

object WhenToSendSample {

  case object Yes extends WithName("yesProvideSample") with WhenToSendSample
  case object No extends WithName("notSendingSample") with WhenToSendSample

  val values: Set[WhenToSendSample] = Set(Yes, No)

  val options: Set[RadioOption] = values.map {
    value => RadioOption("whenToSendSample", value.toString)
  }

  implicit val enumerable: Enumerable[WhenToSendSample] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit object WhenToSendSampleWrites extends Writes[WhenToSendSample] {
    def writes(whenToSendSample: WhenToSendSample) = Json.toJson(whenToSendSample.toString)
  }

  implicit object WhenToSendSampleReads extends Reads[WhenToSendSample] {
    override def reads(json: JsValue): JsResult[WhenToSendSample] = json match {
      case JsString(Yes.toString) => JsSuccess(Yes)
      case JsString(No.toString) => JsSuccess(No)
      case _ => JsError("Unknown whenToSendSample")
    }
  }
}
