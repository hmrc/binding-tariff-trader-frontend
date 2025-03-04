/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.libs.json.{JsPath, JsValue, Json, JsonValidationError}

class KeyStoreEntryValidationException(
  val key: String,
  val invalidJson: JsValue,
  val errors: Iterable[(JsPath, Iterable[JsonValidationError])]
) extends Exception {
  override def getMessage: String =
    s"KeyStore entry for key '$key' was '${Json.stringify(invalidJson)}'. Attempt to convert to ${CacheMap.getClass.getName} gave errors: $errors"
}
