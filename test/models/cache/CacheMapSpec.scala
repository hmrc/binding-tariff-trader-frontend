/*
 * Copyright 2023 HM Revenue & Customs
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
import models.Country
import play.api.libs.json.Json

class CacheMapSpec extends SpecBase {

  private val model = CacheMap("id1", Map("key1" -> Json.toJson("value1")))

  private val expectedJson = Json.parse("""{"id":"id1","data":{"key1":"value1"}}""".stripMargin)

  "CacheMap" when {
    "apply" should {
      "return expected object" in {
        val cache = expectedJson.as[CacheMap]
        cache shouldBe model
      }

      "return expected json from object" in {
        Json.toJson(model) shouldBe expectedJson
      }
    }

    "getEntry" should {
      "return expected object" in {
        model.getEntry[String]("key1") shouldBe Some("value1")
      }

      "return None when missing key" in {
        model.getEntry[String]("key_test") shouldBe None
      }

      "throw an exception when object not matching" in {
        val exception = intercept[KeyStoreEntryValidationException] {
          model.getEntry[Country]("key1")
        }

        exception.getMessage shouldBe "KeyStore entry for key 'key1' was '\"value1\"'. Attempt to convert to " +
          "models.cache.CacheMap$ gave errors: List((,List(JsonValidationError(List(error.expected.jsobject),ArraySeq()))))"
      }
    }
  }
}
