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

sealed trait CommodityCodeBestMatch extends Page

object CommodityCodeBestMatch {

  case object Yes extends WithName("yesFoundCommodityCode") with CommodityCodeBestMatch
  case object No extends WithName("noHaventFoundCommodityCode") with CommodityCodeBestMatch

  val values: Set[CommodityCodeBestMatch] = Set(Yes, No)

  val options: Set[RadioOption] = values.map {
    value => RadioOption("commodityCodeBestMatch", value.toString)
  }

  implicit val enumerable: Enumerable[CommodityCodeBestMatch] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit object CommodityCodeBestMatchWrites extends Writes[CommodityCodeBestMatch] {
    def writes(commodityCodeBestMatch: CommodityCodeBestMatch) = Json.toJson(commodityCodeBestMatch.toString)
  }

  implicit object CommodityCodeBestMatchReads extends Reads[CommodityCodeBestMatch] {
    override def reads(json: JsValue): JsResult[CommodityCodeBestMatch] = json match {
      case JsString(Yes.toString) => JsSuccess(Yes)
      case JsString(No.toString) => JsSuccess(No)
      case _ => JsError("Unknown commodityCodeBestMatch")
    }
  }
}
