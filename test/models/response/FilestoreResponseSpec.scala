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

import models.ScanStatus
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class FilestoreResponseSpec extends AnyWordSpec with Matchers {

  "FilestoreResponse" should {
    "serialize to JSON with all fields" in {
      val response = FilestoreResponse(
        id = "file-123",
        fileName = "test.pdf",
        mimeType = "application/pdf",
        url = Some("https://example.com/files/test.pdf"),
        scanStatus = Some(ScanStatus.READY)
      )

      val expectedJson = Json.obj(
        "id"         -> "file-123",
        "fileName"   -> "test.pdf",
        "mimeType"   -> "application/pdf",
        "url"        -> "https://example.com/files/test.pdf",
        "scanStatus" -> "READY"
      )

      Json.toJson(response) shouldBe expectedJson
    }

    "serialize to JSON with minimal required fields" in {
      val response = FilestoreResponse(
        id = "file-123",
        fileName = "test.pdf",
        mimeType = "application/pdf"
      )

      val expectedJson = Json.obj(
        "id"       -> "file-123",
        "fileName" -> "test.pdf",
        "mimeType" -> "application/pdf"
      )

      Json.toJson(response) shouldBe expectedJson
    }

    "deserialize from JSON with all fields" in {
      val json = Json.obj(
        "id"         -> "file-123",
        "fileName"   -> "test.pdf",
        "mimeType"   -> "application/pdf",
        "url"        -> "https://example.com/files/test.pdf",
        "scanStatus" -> "READY"
      )

      val expectedResponse = FilestoreResponse(
        id = "file-123",
        fileName = "test.pdf",
        mimeType = "application/pdf",
        url = Some("https://example.com/files/test.pdf"),
        scanStatus = Some(ScanStatus.READY)
      )

      json.validate[FilestoreResponse] shouldBe JsSuccess(expectedResponse)
    }

    "deserialize from JSON with minimal required fields" in {
      val json = Json.obj(
        "id"       -> "file-123",
        "fileName" -> "test.pdf",
        "mimeType" -> "application/pdf"
      )

      val expectedResponse = FilestoreResponse(
        id = "file-123",
        fileName = "test.pdf",
        mimeType = "application/pdf",
        url = None,
        scanStatus = None
      )

      json.validate[FilestoreResponse] shouldBe JsSuccess(expectedResponse)
    }

    "fail to deserialize when id is missing" in {
      val incompleteJson = Json.obj(
        "fileName" -> "test.pdf",
        "mimeType" -> "application/pdf"
      )

      incompleteJson.validate[FilestoreResponse].isError shouldBe true
    }

    "fail to deserialize when fileName is missing" in {
      val incompleteJson = Json.obj(
        "id"       -> "file-123",
        "mimeType" -> "application/pdf"
      )

      incompleteJson.validate[FilestoreResponse].isError shouldBe true
    }

    "fail to deserialize when mimeType is missing" in {
      val incompleteJson = Json.obj(
        "id"       -> "file-123",
        "fileName" -> "test.pdf"
      )

      incompleteJson.validate[FilestoreResponse].isError shouldBe true
    }

    "handle all scan statuses" in {
      val statuses = List(
        ScanStatus.READY,
        ScanStatus.FAILED
      )

      statuses.foreach { status =>
        val response = FilestoreResponse(
          id = "file-123",
          fileName = "test.pdf",
          mimeType = "application/pdf",
          scanStatus = Some(status)
        )

        val json                 = Json.toJson(response)
        val deserializedResponse = json.as[FilestoreResponse]

        deserializedResponse.scanStatus shouldBe Some(status)
      }
    }

    "handle round-trip serialization/deserialization" in {
      val response = FilestoreResponse(
        id = "file-123",
        fileName = "test.pdf",
        mimeType = "application/pdf",
        url = Some("https://example.com/files/test.pdf"),
        scanStatus = Some(ScanStatus.READY)
      )

      val json                 = Json.toJson(response)
      val deserializedResponse = json.as[FilestoreResponse]

      deserializedResponse shouldBe response
    }
  }
}
