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

class EmailSpec extends SpecBase with Matchers {

  private val applicationSubmittedParameters = ApplicationSubmittedParameters("recipientName", "ref")
  private val applicationSubmittedEmail = ApplicationSubmittedEmail(Seq("abc", "def"), applicationSubmittedParameters)

  "ApplicationSubmittedEmail" should {
    "do round-trip serialize/deserialize" when {
      "All the fields are present and valid" in {
        Json.toJson(applicationSubmittedEmail).as[ApplicationSubmittedEmail] shouldBe applicationSubmittedEmail
      }
      "to is empty" in {
        val applicationSubmittedEmailWithoutTo = applicationSubmittedEmail.copy(to = List.empty)
        Json
          .toJson(applicationSubmittedEmailWithoutTo)
          .as[ApplicationSubmittedEmail] shouldBe applicationSubmittedEmailWithoutTo
      }
    }
    "fail to deserialize" when {
      "there is type mismatch" in {
        val json = Json.obj(
          "to"         -> true,
          "parameters" -> false
        )
        json.validate[ApplicationSubmittedEmail] shouldBe a[JsError]
      }
      "an empty object" in {
        Json.obj().validate[ApplicationSubmittedEmail] shouldBe a[JsError]
      }
    }
  }
  "ApplicationSubmittedParameters" should {
    "fail to deserialize" when {
      "there is type mismatch" in {
        val json = Json.obj(
          "recipientName_line1" -> true,
          "reference"           -> false
        )
        json.validate[ApplicationSubmittedParameters] shouldBe a[JsError]
      }
      "an empty object" in {
        Json.obj().validate[ApplicationSubmittedParameters] shouldBe a[JsError]
      }
    }
  }
}
