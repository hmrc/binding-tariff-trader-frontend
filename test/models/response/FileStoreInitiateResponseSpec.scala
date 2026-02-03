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

package models.response

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class FileStoreInitiateResponseSpec extends AnyWordSpec with Matchers {

  "FileStoreInitiateResponse" should {

    val formTemplate = UpscanFormTemplate(
      href = "https://upscan.example.com/upload",
      fields = Map(
        "key"             -> "value",
        "x-amz-algorithm" -> "AWS4-HMAC-SHA256",
        "policy"          -> "base64policy"
      )
    )

    val response = FileStoreInitiateResponse(
      id = "file-id-123",
      upscanReference = "upscan-ref-456",
      uploadRequest = formTemplate
    )

    val expectedJson = Json.obj(
      "id"              -> "file-id-123",
      "upscanReference" -> "upscan-ref-456",
      "uploadRequest" -> Json.obj(
        "href" -> "https://upscan.example.com/upload",
        "fields" -> Json.obj(
          "key"             -> "value",
          "x-amz-algorithm" -> "AWS4-HMAC-SHA256",
          "policy"          -> "base64policy"
        )
      )
    )

    "serialize to JSON correctly" in {
      Json.toJson(response) shouldBe expectedJson
    }

    "deserialize from JSON correctly" in {
      expectedJson.validate[FileStoreInitiateResponse] shouldBe JsSuccess(response)
    }

    "fail to deserialize when id is missing" in {
      val incompleteJson = Json.obj(
        "upscanReference" -> "upscan-ref-456",
        "uploadRequest" -> Json.obj(
          "href"   -> "https://upscan.example.com/upload",
          "fields" -> Json.obj("key" -> "value")
        )
      )

      incompleteJson.validate[FileStoreInitiateResponse].isError shouldBe true
    }

    "fail to deserialize when upscanReference is missing" in {
      val incompleteJson = Json.obj(
        "id" -> "file-id-123",
        "uploadRequest" -> Json.obj(
          "href"   -> "https://upscan.example.com/upload",
          "fields" -> Json.obj("key" -> "value")
        )
      )

      incompleteJson.validate[FileStoreInitiateResponse].isError shouldBe true
    }

    "fail to deserialize when uploadRequest is missing" in {
      val incompleteJson = Json.obj(
        "id"              -> "file-id-123",
        "upscanReference" -> "upscan-ref-456"
      )

      incompleteJson.validate[FileStoreInitiateResponse].isError shouldBe true
    }

    "handle round-trip serialization/deserialization" in {
      val json                 = Json.toJson(response)
      val deserializedResponse = json.as[FileStoreInitiateResponse]

      deserializedResponse shouldBe response
    }
  }
}
