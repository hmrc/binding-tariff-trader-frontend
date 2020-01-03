/*
 * Copyright 2020 HM Revenue & Customs
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

import pages.Page
import play.api.libs.json._
import viewmodels.RadioOption

sealed trait ImportOrExport extends Page

object ImportOrExport {

  case object Import extends WithName("import") with ImportOrExport
  case object Export extends WithName("export") with ImportOrExport

  val values: Set[ImportOrExport] = Set(Import, Export)

  val options: Set[RadioOption] = values.map {
    value => RadioOption("importOrExport", value.toString)
  }

  implicit val enumerable: Enumerable[ImportOrExport] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit object ImportOrExportWrites extends Writes[ImportOrExport] {
    def writes(importOrExport: ImportOrExport): JsValue = Json.toJson(importOrExport.toString)
  }

  implicit object ImportOrExportReads extends Reads[ImportOrExport] {
    override def reads(json: JsValue): JsResult[ImportOrExport] = json match {
      case JsString(Import.toString) => JsSuccess(Import)
      case JsString(Export.toString) => JsSuccess(Export)
      case _ => JsError("Unknown importOrExport")
    }
  }

}
