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

sealed trait InformationAboutYourItem extends Page

object InformationAboutYourItem {

  case object Yesihaveinfo extends WithName("yesIHaveInfo") with InformationAboutYourItem
  case object No extends WithName("no") with InformationAboutYourItem

  val values: Set[InformationAboutYourItem] = Set(
    Yesihaveinfo, No
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("informationAboutYourItem", value.toString)
  }

  implicit val enumerable: Enumerable[InformationAboutYourItem] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit object InformationAboutYourItemWrites extends Writes[InformationAboutYourItem] {
    def writes(informationAboutYourItem: InformationAboutYourItem) = Json.toJson(informationAboutYourItem.toString)
  }

  implicit object InformationAboutYourItemReads extends Reads[InformationAboutYourItem] {
    override def reads(json: JsValue): JsResult[InformationAboutYourItem] = json match {
      case JsString(Yesihaveinfo.toString) => JsSuccess(Yesihaveinfo)
      case JsString(No.toString) => JsSuccess(No)
      case _                          => JsError("Unknown informationAboutYourItem")
    }
  }
}
