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
import play.api.libs.json.{JsError, Json}

class RegisteredAddressForEoriSpec extends SpecBase with Matchers {

  private val model = RegisteredAddressForEori(
    eori = "abc",
    businessName = "business",
    addressLine1 = "address",
    townOrCity = "city",
    postcode = Some("code"),
    country = "Country"
  )
  private val modelWithoutPostCode = RegisteredAddressForEori(
    eori = "abc",
    businessName = "business",
    addressLine1 = "address",
    townOrCity = "city",
    postcode = None,
    country = "Country"
  )

  "RegisteredAddressForEori" should {
    "do round-trip serialoize/deserialize" when {
      "All the fields are present and valid" in {
        Json.toJson(model).as[RegisteredAddressForEori] shouldBe model
      }
      "to is empty" in {
        Json
          .toJson(modelWithoutPostCode)
          .as[RegisteredAddressForEori] shouldBe modelWithoutPostCode
      }
    }
    "fail to deserialize" when {
      "there is type mismatch" in {
        val json = Json.obj(
          "eori"         -> true,
          "businessName" -> false,
          "addressLine1" -> true,
          "townOrCity"   -> false,
          "postcode"     -> true,
          "country"      -> false
        )
        json.validate[RegisteredAddressForEori] shouldBe a[JsError]
      }
      "an empty object" in {
        Json.obj().validate[RegisteredAddressForEori] shouldBe a[JsError]
      }
    }
  }
}
