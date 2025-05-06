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

package models

import base.SpecBase
import models.oCase.btiCaseExample
import play.api.libs.json.{JsError, Json}

class ConfirmationSpec extends SpecBase {

  private val confirmation = Confirmation("ref", "eoriAgent", "email")

  "Confirmation" when {
    "apply" should {
      "produce the expected confirmation model" in {
        Confirmation.apply(btiCaseExample) shouldBe confirmation
      }
    }
    "do round-trip serialize/deserialize" when {
      "All the fields are present and valid" in {
        Json.toJson(confirmation).as[Confirmation] shouldBe confirmation
      }
    }
    "fail to deserialize" when {
      "there is type mismatch" in {
        val json = Json.obj("reference" -> true)
        json.validate[Confirmation] shouldBe a[JsError]
      }
      "an empty object" in {
        Json.obj().validate[Confirmation] shouldBe a[JsError]
      }
    }
  }
}
