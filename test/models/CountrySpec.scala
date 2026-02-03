/*
 * Copyright 2026 HM Revenue & Customs
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

import base.SpecBase
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsError, JsResultException, JsValue, Json}

class CountrySpec extends SpecBase with Matchers {

  private val model = Country("IE", "title.ireland", "IE", List("Republic of Ireland", "Eire"))

  private val expectedJson: JsValue = Json.parse(
    """
      |{
      |  "code": "IE",
      |  "displayName": "Ireland",
      |  "synonyms": ["Republic of Ireland", "Eire"]
      |}
    """.stripMargin
  )

  "Country" should {
    "toAutoCompleteJson" when {
      "return expected JsObject" in {
        model.toAutoCompleteJson(messages) shouldBe expectedJson
      }
    }
    "do round-trip serialoize/deserialize" when {
      "All the fields are present and valid" in {
        Json.toJson(model).as[Country] shouldBe model
      }
      "countrySynonyms is empty" in {
        val modelWithoutCountrySynonyms = model.copy(countrySynonyms = List.empty)
        Json.toJson(modelWithoutCountrySynonyms).as[Country] shouldBe modelWithoutCountrySynonyms
      }
    }
    "fail to deserialize" when {
      "there is type mismatch" in {
        val json = Json.obj(
          "code"        -> true,
          "displayName" -> 2,
          "synonyms"    -> false
        )
        json.validate[Country] shouldBe a[JsError]
      }
      "an empty object" in {
        Json.obj().validate[Country] shouldBe a[JsError]
      }
    }
  }
}
