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

package models.requests

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsObject, JsSuccess, Json}

class FileStoreInitiateRequestSpec extends AnyWordSpec with Matchers {

  "FileStoreInitiateRequest" should {
    "serialize to JSON" in {
      val request = FileStoreInitiateRequest(
        id = Some("test-id"),
        successRedirect = Some("http://success.example.com"),
        errorRedirect = Some("http://error.example.com"),
        expectedContentType = Some("application/pdf"),
        publishable = true,
        maxFileSize = 5242880 // 5MB
      )

      val expectedJson = Json.obj(
        "id"                  -> "test-id",
        "successRedirect"     -> "http://success.example.com",
        "errorRedirect"       -> "http://error.example.com",
        "expectedContentType" -> "application/pdf",
        "publishable"         -> true,
        "maxFileSize"         -> 5242880
      )

      Json.toJson(request) shouldBe expectedJson
    }

    "deserialize from JSON" in {
      val json = Json.obj(
        "id"                  -> "test-id",
        "successRedirect"     -> "http://success.example.com",
        "errorRedirect"       -> "http://error.example.com",
        "expectedContentType" -> "application/pdf",
        "publishable"         -> true,
        "maxFileSize"         -> 5242880
      )

      val expectedRequest = FileStoreInitiateRequest(
        id = Some("test-id"),
        successRedirect = Some("http://success.example.com"),
        errorRedirect = Some("http://error.example.com"),
        expectedContentType = Some("application/pdf"),
        publishable = true,
        maxFileSize = 5242880
      )

      json.validate[FileStoreInitiateRequest] shouldBe JsSuccess(expectedRequest)
    }

    "handle missing optional fields" in {
      val json = Json.obj(
        "maxFileSize" -> 1048576
      )

      val expectedRequest = FileStoreInitiateRequest(
        id = None,
        successRedirect = None,
        errorRedirect = None,
        expectedContentType = None,
        publishable = false,
        maxFileSize = 1048576
      )

      json.validate[FileStoreInitiateRequest] shouldBe JsSuccess(expectedRequest)
    }

    "serialize to JSON omitting None values" in {
      val request = FileStoreInitiateRequest(
        id = None,
        successRedirect = None,
        errorRedirect = None,
        expectedContentType = None,
        publishable = false,
        maxFileSize = 1048576
      )

      val resultJson = Json.toJson(request).as[JsObject]

      resultJson.value should contain("publishable" -> Json.toJson(false))
      resultJson.value should contain("maxFileSize" -> Json.toJson(1048576))
      resultJson.value should not contain key("id")
      resultJson.value should not contain key("successRedirect")
      resultJson.value should not contain key("errorRedirect")
      resultJson.value should not contain key("expectedContentType")
    }

    "fail to deserialize when maxFileSize is missing" in {
      val json = Json.obj(
        "id"          -> "test-id",
        "publishable" -> true
      )

      json.validate[FileStoreInitiateRequest].isError shouldBe true
    }

    "handle default values correctly" in {
      val json = Json.obj(
        "maxFileSize" -> 2097152
      )

      val request = json.as[FileStoreInitiateRequest]
      request.id                  shouldBe None
      request.successRedirect     shouldBe None
      request.errorRedirect       shouldBe None
      request.expectedContentType shouldBe None
      request.publishable         shouldBe false
      request.maxFileSize         shouldBe 2097152
    }

    "handle all fields correctly" in {
      val request = FileStoreInitiateRequest(
        id = Some("custom-id"),
        successRedirect = Some("https://success.url"),
        errorRedirect = Some("https://error.url"),
        expectedContentType = Some("image/png"),
        publishable = true,
        maxFileSize = 10485760
      )

      val json                = Json.toJson(request)
      val deserializedRequest = json.as[FileStoreInitiateRequest]

      deserializedRequest shouldBe request
    }
  }
}
