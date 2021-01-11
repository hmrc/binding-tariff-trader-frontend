/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.i18n.Messages
import play.api.libs.json._

case class Country(code: String, countryName: String, alphaTwoCode: String, countrySynonyms: List[String]) {

  def toAutoCompleteJson(implicit messages: Messages): JsObject = Json.obj("code" -> code, "displayName" -> messages(countryName), "synonyms" -> countrySynonyms)

  def toNewAutoCompleteJson(implicit messages: Messages): Seq[(String, JsObject)] = s"country:$code" ->
      Json.obj(
        "edges" -> Json.obj("from" -> JsArray()),
      "meta" ->
        Json.obj("canonical" -> true, "canonical-mask" -> 1, "display-name" -> true, "stable-name" -> true),
      "names" ->
        Json.obj("cy" -> false, "en-GB" -> messages(countryName))
      )::
      countrySynonyms.map(syn =>
        {
          s"nym:$syn" ->
            Json.obj(
              "edges" -> Json.obj("from" -> Json.arr(s"country:$code")),
          "meta" ->
            Json.obj("canonical" -> false, "canonical-mask" -> 0, "display-name" -> false, "stable-name" -> false),
          "names" ->
            Json.obj("cy" -> false, "en-GB" -> syn)
            )})
}

object Country {

  implicit val formats = Json.format[Country]
}