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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json._

import java.time.ZonedDateTime

class AttachmentSpec extends AnyWordSpec with Matchers {

  "Attachment" should {
    "create an instance with only required values" in {
      val id         = "attachment-123"
      val isPublic   = true
      val attachment = Attachment(id, isPublic)

      attachment.id                                                     shouldBe id
      attachment.public                                                 shouldBe isPublic
      attachment.timestamp.isAfter(ZonedDateTime.now().minusMinutes(1)) shouldBe true
    }

    "create an instance with all values" in {
      val id         = "attachment-123"
      val isPublic   = true
      val timestamp  = ZonedDateTime.now().minusHours(1)
      val attachment = Attachment(id, isPublic, timestamp)

      attachment.id        shouldBe id
      attachment.public    shouldBe isPublic
      attachment.timestamp shouldBe timestamp
    }

    "be equal when all fields match" in {
      val timestamp   = ZonedDateTime.now().minusHours(1)
      val attachment1 = Attachment("id-123", true, timestamp)
      val attachment2 = Attachment("id-123", true, timestamp)

      attachment1 shouldBe attachment2
    }

    "not be equal when ids differ" in {
      val timestamp   = ZonedDateTime.now().minusHours(1)
      val attachment1 = Attachment("id-123", true, timestamp)
      val attachment2 = Attachment("id-456", true, timestamp)

      attachment1 should not be attachment2
    }

    "not be equal when public flags differ" in {
      val timestamp   = ZonedDateTime.now().minusHours(1)
      val attachment1 = Attachment("id-123", true, timestamp)
      val attachment2 = Attachment("id-123", false, timestamp)

      attachment1 should not be attachment2
    }

    "not be equal when timestamps differ" in {
      val timestamp1  = ZonedDateTime.now().minusHours(1)
      val timestamp2  = ZonedDateTime.now().minusHours(2)
      val attachment1 = Attachment("id-123", true, timestamp1)
      val attachment2 = Attachment("id-123", true, timestamp2)

      attachment1 should not be attachment2
    }
  }

  "Attachment JSON Format" should {
    "serialize an Attachment with all fields" in {
      val timestamp  = ZonedDateTime.parse("2023-01-01T12:00:00Z")
      val attachment = Attachment("id-123", true, timestamp)

      val json = Json.toJson(attachment)

      (json \ "id").as[String]        shouldBe "id-123"
      (json \ "public").as[Boolean]   shouldBe true
      (json \ "timestamp").as[String] shouldBe "2023-01-01T12:00:00Z"
    }

    "deserialize a complete JSON to an Attachment" in {
      val json = Json.obj(
        "id"        -> "id-123",
        "public"    -> true,
        "timestamp" -> "2023-01-01T12:00:00Z"
      )

      val result = json.as[Attachment]

      result.id        shouldBe "id-123"
      result.public    shouldBe true
      result.timestamp shouldBe ZonedDateTime.parse("2023-01-01T12:00:00Z")
    }

    "fail to deserialize when id is missing" in {
      val json = Json.obj(
        "public"    -> true,
        "timestamp" -> "2023-01-01T12:00:00Z"
      )

      intercept[JsResultException] {
        json.as[Attachment]
      }
    }

    "fail to deserialize when public flag is missing" in {
      val json = Json.obj(
        "id"        -> "id-123",
        "timestamp" -> "2023-01-01T12:00:00Z"
      )

      intercept[JsResultException] {
        json.as[Attachment]
      }
    }

    "deserialize with default timestamp when it's missing" in {
      val json = Json.obj(
        "id"     -> "id-123",
        "public" -> true
      )

      val result = json.as[Attachment]

      result.id                                                     shouldBe "id-123"
      result.public                                                 shouldBe true
      result.timestamp.isAfter(ZonedDateTime.now().minusMinutes(1)) shouldBe true
    }

    "fail to deserialize when timestamp is invalid" in {
      val json = Json.obj(
        "id"        -> "id-123",
        "public"    -> true,
        "timestamp" -> "not-a-timestamp"
      )

      intercept[JsResultException] {
        json.as[Attachment]
      }
    }

    "handle round-trip serialization/deserialization" in {
      val timestamp = ZonedDateTime.parse("2023-01-01T12:00:00Z")
      val original  = Attachment("id-123", true, timestamp)

      val json         = Json.toJson(original)
      val deserialized = json.as[Attachment]

      deserialized shouldBe original
    }
  }
}
