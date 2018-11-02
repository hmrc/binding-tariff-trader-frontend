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

sealed trait CommodityCodeBestMatch

object CommodityCodeBestMatch {

  case object Yesfoundcommoditycode extends WithName("yesFoundCommodityCode") with CommodityCodeBestMatch
  case object Nohaventfoundcommoditycode extends WithName("noHaventFoundCommodityCode") with CommodityCodeBestMatch

  val values: Set[CommodityCodeBestMatch] = Set(
    Yesfoundcommoditycode, Nohaventfoundcommoditycode
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("commodityCodeBestMatch", value.toString)
  }

  implicit val enumerable: Enumerable[CommodityCodeBestMatch] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit object CommodityCodeBestMatchWrites extends Writes[CommodityCodeBestMatch] {
    def writes(commodityCodeBestMatch: CommodityCodeBestMatch) = Json.toJson(commodityCodeBestMatch.toString)
  }

  implicit object CommodityCodeBestMatchReads extends Reads[CommodityCodeBestMatch] {
    override def reads(json: JsValue): JsResult[CommodityCodeBestMatch] = json match {
      case JsString(Yesfoundcommoditycode.toString) => JsSuccess(Yesfoundcommoditycode)
      case JsString(Nohaventfoundcommoditycode.toString) => JsSuccess(Nohaventfoundcommoditycode)
      case _                          => JsError("Unknown commodityCodeBestMatch")
    }
  }
}
