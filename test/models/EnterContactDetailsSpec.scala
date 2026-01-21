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

class EnterContactDetailsSpec extends SpecBase with Matchers {
  "EnterContactDetails" should {
    "do round-trip serialoize/deserialize" when {
      "All the fields are present and valid" in {
        val enterContactDetails = EnterContactDetails("mane", "email", "phone")
        Json.toJson(enterContactDetails).as[EnterContactDetails] shouldBe enterContactDetails
      }
    }
    "fail to deserialize" when {
      "there is type mismatch" in {
        val json = Json.obj("name" -> true, "email" -> false, "phoneNumber" -> false)
        json.validate[EnterContactDetails] shouldBe a[JsError]
      }
      "an empty object" in {
        Json.obj().validate[EnterContactDetails] shouldBe a[JsError]
      }
    }
  }
}
