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

import base.SpecBase
import play.api.libs.json.{JsPath, Json}

class KeyStoreEntryValidationExceptionSpec extends SpecBase {

  private val model = new KeyStoreEntryValidationException(
    "key1",
    Json.parse("""{"key1":"value1"}"""),
    Seq(
      (JsPath \ "key1", Seq.empty)
    )
  )

  "KeyStoreEntryValidationException" when {
    "getMessage" should {
      "return expected message" in {
        model.getMessage shouldBe "KeyStore entry for key 'key1' was '{\"key1\":\"value1\"}'. Attempt to convert to " +
          "models.cache.CacheMap$ gave errors: List((/key1,List()))"
      }
    }
  }
}
