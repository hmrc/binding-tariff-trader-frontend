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

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsNull, JsResultException, Json}
import utils.JsonFormatters.*

class OperatorSpec extends AnyWordSpec with Matchers {

  "Operator" should {
    "create an instance with only ID" in {
      val operator = Operator("user123")

      operator.id   must be("user123")
      operator.name must be(None)
    }

    "create an instance with ID and name" in {
      val operator = Operator("user123", Some("John Doe"))

      operator.id   must be("user123")
      operator.name must be(Some("John Doe"))
    }

    "be equal when IDs and names match" in {
      val operator1 = Operator("user123", Some("John Doe"))
      val operator2 = Operator("user123", Some("John Doe"))

      operator1 must be(operator2)
    }

    "not be equal when IDs differ" in {
      val operator1 = Operator("user123", Some("John Doe"))
      val operator2 = Operator("user456", Some("John Doe"))

      operator1 must not be operator2
    }

    "not be equal when names differ" in {
      val operator1 = Operator("user123", Some("John Doe"))
      val operator2 = Operator("user123", Some("Jane Doe"))

      operator1 must not be operator2
    }
  }

  "Operator JSON Format" should {
    "serialize an Operator with ID only" in {
      val operator     = Operator("user123")
      val expectedJson = Json.obj("id" -> "user123")

      Json.toJson(operator) must be(expectedJson)
    }

    "serialize an Operator with ID and name" in {
      val operator = Operator("user123", Some("John Doe"))
      val expectedJson = Json.obj(
        "id"   -> "user123",
        "name" -> "John Doe"
      )

      Json.toJson(operator) must be(expectedJson)
    }

    "deserialize an Operator with ID only" in {
      val json             = Json.obj("id" -> "user123")
      val expectedOperator = Operator("user123")

      json.as[Operator] must be(expectedOperator)
    }

    "deserialize an Operator with ID and name" in {
      val json = Json.obj(
        "id"   -> "user123",
        "name" -> "John Doe"
      )
      val expectedOperator = Operator("user123", Some("John Doe"))

      json.as[Operator] must be(expectedOperator)
    }

    "fail to deserialize when ID is missing" in {
      val json = Json.obj("name" -> "John Doe")

      intercept[JsResultException] {
        json.as[Operator]
      }
    }

    "deserialize when name is null" in {
      val json = Json.obj(
        "id"   -> "user123",
        "name" -> JsNull
      )
      val expectedOperator = Operator("user123", None)

      json.as[Operator] must be(expectedOperator)
    }

    "deserialize when name is missing (using default value)" in {
      val json             = Json.obj("id" -> "user123")
      val expectedOperator = Operator("user123")

      json.as[Operator] must be(expectedOperator)
    }

    "handle round-trip serialization/deserialization" in {
      val original     = Operator("user123", Some("John Doe"))
      val json         = Json.toJson(original)
      val deserialized = json.as[Operator]

      deserialized must be(original)
    }

    "fail to deserialize when ID is not a string" in {
      val json = Json.obj(
        "id"   -> 123,
        "name" -> "John Doe"
      )

      intercept[JsResultException] {
        json.as[Operator]
      }
    }

    "fail to deserialize when name is not a string" in {
      val json = Json.obj(
        "id"   -> "user123",
        "name" -> 123
      )

      intercept[JsResultException] {
        json.as[Operator]
      }
    }
  }
}
