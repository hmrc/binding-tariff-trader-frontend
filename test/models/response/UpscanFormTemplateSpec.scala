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

package models.response

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsObject, JsSuccess, Json}

class UpscanFormTemplateSpec extends AnyWordSpec with Matchers {

  "UpscanFormTemplate" should {
    "serialize to JSON correctly" in {
      val template = UpscanFormTemplate(
        href = "https://upscan.example.com/upload",
        fields = Map(
          "key"              -> "12345",
          "x-amz-algorithm"  -> "AWS4-HMAC-SHA256",
          "x-amz-credential" -> "credential123",
          "policy"           -> "base64policy"
        )
      )

      val expectedJson = Json.obj(
        "href" -> "https://upscan.example.com/upload",
        "fields" -> Json.obj(
          "key"              -> "12345",
          "x-amz-algorithm"  -> "AWS4-HMAC-SHA256",
          "x-amz-credential" -> "credential123",
          "policy"           -> "base64policy"
        )
      )

      Json.toJson(template) shouldBe expectedJson
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "href" -> "https://upscan.example.com/upload",
        "fields" -> Json.obj(
          "key"              -> "12345",
          "x-amz-algorithm"  -> "AWS4-HMAC-SHA256",
          "x-amz-credential" -> "credential123",
          "policy"           -> "base64policy"
        )
      )

      val expectedTemplate = UpscanFormTemplate(
        href = "https://upscan.example.com/upload",
        fields = Map(
          "key"              -> "12345",
          "x-amz-algorithm"  -> "AWS4-HMAC-SHA256",
          "x-amz-credential" -> "credential123",
          "policy"           -> "base64policy"
        )
      )

      json.validate[UpscanFormTemplate] shouldBe JsSuccess(expectedTemplate)
    }

    "serialize an empty fields map correctly" in {
      val template = UpscanFormTemplate(
        href = "https://upscan.example.com/upload",
        fields = Map.empty
      )

      val expectedJson = Json.obj(
        "href"   -> "https://upscan.example.com/upload",
        "fields" -> Json.obj()
      )

      Json.toJson(template) shouldBe expectedJson
    }

    "fail to deserialize when href is missing" in {
      val incompleteJson = Json.obj(
        "fields" -> Json.obj(
          "key" -> "12345"
        )
      )

      incompleteJson.validate[UpscanFormTemplate].isError shouldBe true
    }

    "fail to deserialize when fields is missing" in {
      val incompleteJson = Json.obj(
        "href" -> "https://upscan.example.com/upload"
      )

      incompleteJson.validate[UpscanFormTemplate].isError shouldBe true
    }

    "handle round-trip serialization/deserialization" in {
      val template = UpscanFormTemplate(
        href = "https://upscan.example.com/upload",
        fields = Map(
          "key"             -> "12345",
          "x-amz-algorithm" -> "AWS4-HMAC-SHA256",
          "policy"          -> "base64policy"
        )
      )

      val json                 = Json.toJson(template)
      val deserializedTemplate = json.as[UpscanFormTemplate]

      deserializedTemplate shouldBe template
    }

    "handle fields with special characters" in {
      val template = UpscanFormTemplate(
        href = "https://upscan.example.com/upload",
        fields = Map(
          "key-with-hyphens"     -> "value-with-hyphens",
          "key_with_underscores" -> "value_with_underscores",
          "key.with.dots"        -> "value.with.dots"
        )
      )

      val json                 = Json.toJson(template)
      val deserializedTemplate = json.as[UpscanFormTemplate]

      deserializedTemplate shouldBe template
    }
  }
}
