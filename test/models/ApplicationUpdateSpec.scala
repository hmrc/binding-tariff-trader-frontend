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
import utils.JsonFormatters.*

class ApplicationUpdateSpec extends AnyWordSpec with Matchers {

  "ApplicationUpdate" should {
    "create an instance with default values" in {
      val update = ApplicationUpdate()

      update.`type`         shouldBe "BTI"
      update.applicationPdf shouldBe NoChange
    }

    "create an instance with custom values" in {
      val attachment = Attachment("id", true)
      val update     = ApplicationUpdate("BTI", SetValue(Some(attachment)))

      update.`type`         shouldBe "BTI"
      update.applicationPdf shouldBe SetValue(Some(attachment))
    }

    "be equal when values match" in {
      val update1 = ApplicationUpdate("BTI", NoChange)
      val update2 = ApplicationUpdate("BTI", NoChange)

      update1 shouldBe update2
    }

    "not be equal when types differ" in {
      val update1 = ApplicationUpdate("BTI", NoChange)
      val update2 = ApplicationUpdate("Other", NoChange)

      update1 should not be update2
    }

    "not be equal when applicationPdf values differ" in {
      val attachment = Attachment("id", true)
      val update1    = ApplicationUpdate("BTI", NoChange)
      val update2    = ApplicationUpdate("BTI", SetValue(Some(attachment)))

      update1 should not be update2
    }
  }

  "CaseUpdate" should {
    "create an instance with default values" in {
      val update = CaseUpdate()

      update.application shouldBe None
    }

    "create an instance with custom values" in {
      val appUpdate = ApplicationUpdate("BTI", NoChange)
      val update    = CaseUpdate(Some(appUpdate))

      update.application shouldBe Some(appUpdate)
    }

    "be equal when values match" in {
      val update1 = CaseUpdate(None)
      val update2 = CaseUpdate(None)

      update1 shouldBe update2
    }

    "not be equal when application values differ" in {
      val appUpdate = ApplicationUpdate("BTI", NoChange)
      val update1   = CaseUpdate(None)
      val update2   = CaseUpdate(Some(appUpdate))

      update1 should not be update2
    }
  }

  "ApplicationUpdate JSON Format" should {
    "serialize an ApplicationUpdate with default values" in {
      val update = ApplicationUpdate()
      val expectedJson = Json.obj(
        "type" -> "BTI",
        "applicationPdf" -> Json.obj(
          "type" -> "NO_CHANGE"
        )
      )

      Json.toJson(update) shouldBe expectedJson
    }

    "serialize an ApplicationUpdate with custom values" in {
      val fixedTime  = ZonedDateTime.parse("2023-01-01T12:00:00Z")
      val attachment = Attachment("id", true, fixedTime)
      val update     = ApplicationUpdate("BTI", SetValue(Some(attachment)))

      val expectedJson = Json.obj(
        "type" -> "BTI",
        "applicationPdf" -> Json.obj(
          "type" -> "SET_VALUE",
          "a" -> Json.obj(
            "id"        -> "id",
            "public"    -> true,
            "timestamp" -> "2023-01-01T12:00:00Z"
          )
        )
      )

      Json.toJson(update) shouldBe expectedJson
    }

    "deserialize an ApplicationUpdate with default values" in {
      val json = Json.obj(
        "type" -> "BTI",
        "applicationPdf" -> Json.obj(
          "type" -> "NO_CHANGE"
        )
      )

      json.as[ApplicationUpdate] shouldBe ApplicationUpdate()
    }

    "deserialize an ApplicationUpdate with custom values" in {
      val fixedTime = ZonedDateTime.parse("2023-01-01T12:00:00Z")
      val json = Json.obj(
        "type" -> "BTI",
        "applicationPdf" -> Json.obj(
          "type" -> "SET_VALUE",
          "a" -> Json.obj(
            "id"        -> "id123",
            "public"    -> false,
            "timestamp" -> "2023-01-01T12:00:00Z"
          )
        )
      )

      val expectedAttachment = Attachment("id123", false, fixedTime)
      val expected           = ApplicationUpdate("BTI", SetValue(Some(expectedAttachment)))
      json.as[ApplicationUpdate] shouldBe expected
    }

    "deserialize an ApplicationUpdate with minimal values" in {
      val json = Json.obj("type" -> "BTI")

      json.as[ApplicationUpdate] shouldBe ApplicationUpdate()
    }

    "handle round-trip serialization/deserialization" in {
      val fixedTime  = ZonedDateTime.parse("2023-01-01T12:00:00Z")
      val attachment = Attachment("id", true, fixedTime)
      val original   = ApplicationUpdate("BTI", SetValue(Some(attachment)))

      val json         = Json.toJson(original)
      val deserialized = json.as[ApplicationUpdate]

      deserialized shouldBe original
    }

    "handle null applicationPdf" in {
      val json = Json.obj(
        "type"           -> "BTI",
        "applicationPdf" -> JsNull
      )

      json.as[ApplicationUpdate] shouldBe ApplicationUpdate()
    }

  }

  "CaseUpdate JSON Format" should {
    "serialize a CaseUpdate with default values" in {
      val update       = CaseUpdate()
      val expectedJson = Json.obj()

      Json.toJson(update) shouldBe expectedJson
    }

    "serialize a CaseUpdate with custom values" in {
      val appUpdate = ApplicationUpdate("BTI", NoChange)
      val update    = CaseUpdate(Some(appUpdate))

      val expectedJson = Json.obj(
        "application" -> Json.obj(
          "type" -> "BTI",
          "applicationPdf" -> Json.obj(
            "type" -> "NO_CHANGE"
          )
        )
      )

      Json.toJson(update) shouldBe expectedJson
    }

    "deserialize a CaseUpdate with default values" in {
      val json = Json.obj(
        "application" -> JsNull
      )

      json.as[CaseUpdate] shouldBe CaseUpdate()
    }

    "deserialize a CaseUpdate with custom values" in {
      val json = Json.obj(
        "application" -> Json.obj(
          "type" -> "BTI",
          "applicationPdf" -> Json.obj(
            "type" -> "NO_CHANGE"
          )
        )
      )

      val expected = CaseUpdate(Some(ApplicationUpdate()))
      json.as[CaseUpdate] shouldBe expected
    }

    "deserialize a CaseUpdate with minimal JSON" in {
      val json = Json.obj()

      json.as[CaseUpdate] shouldBe CaseUpdate()
    }

    "handle round-trip serialization/deserialization" in {
      val appUpdate = ApplicationUpdate("BTI", NoChange)
      val original  = CaseUpdate(Some(appUpdate))

      val json         = Json.toJson(original)
      val deserialized = json.as[CaseUpdate]

      deserialized shouldBe original
    }
  }

  "Custom Option format for ApplicationUpdate" should {
    "handle missing fields using optionNoError" in {
      val json = Json.obj("type" -> "BTI")

      json.as[ApplicationUpdate] shouldBe ApplicationUpdate()
    }

  }
}
