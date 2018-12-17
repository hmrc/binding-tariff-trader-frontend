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

sealed trait SimilarItemCommodityCode extends Page

object SimilarItemCommodityCode {

  case object Yes extends WithName("yesAwareSimilarCode") with SimilarItemCommodityCode
  case object No extends WithName("noNotAware") with SimilarItemCommodityCode

  val values: Set[SimilarItemCommodityCode] = Set(Yes, No)

  val options: Set[RadioOption] = values.map {
    value => RadioOption("similarItemCommodityCode", value.toString)
  }

  implicit val enumerable: Enumerable[SimilarItemCommodityCode] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit object SimilarItemCommodityCodeWrites extends Writes[SimilarItemCommodityCode] {
    def writes(similarItemCommodityCode: SimilarItemCommodityCode) = Json.toJson(similarItemCommodityCode.toString)
  }

  implicit object SimilarItemCommodityCodeReads extends Reads[SimilarItemCommodityCode] {
    override def reads(json: JsValue): JsResult[SimilarItemCommodityCode] = json match {
      case JsString(Yes.toString) => JsSuccess(Yes)
      case JsString(No.toString) => JsSuccess(No)
      case _ => JsError("Unknown similarItemCommodityCode")
    }
  }
}
