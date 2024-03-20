/*
 * Copyright 2024 HM Revenue & Customs
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

package models.cache

import play.api.libs.json.{JsValue, Json, OFormat, Reads}

case class CacheMap(id: String, data: Map[String, JsValue]) {

  def getEntry[T](key: String)(implicit fjs: Reads[T]): Option[T] =
    data
      .get(key)
      .map(json =>
        json
          .validate[T]
          .fold(
            errors => throw new KeyStoreEntryValidationException(key, json, errors),
            valid => valid
          )
      )
}

object CacheMap {
  implicit val formats: OFormat[CacheMap] = Json.format[CacheMap]
}
