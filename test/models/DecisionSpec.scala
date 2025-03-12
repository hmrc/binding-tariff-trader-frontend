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
import play.api.libs.json.*
import utils.JsonFormatters.*

import java.time.{Instant, ZonedDateTime}

class DecisionSpec extends AnyWordSpec with Matchers {

  // Fixed values for reproducible tests
  private val fixedInstantStartString = "2023-01-01T00:00:00Z"
  private val fixedInstantEndString   = "2025-01-01T00:00:00Z"
  private val fixedInstantString      = "2023-06-01T12:00:00Z"
  private val fixedInstantStart       = Instant.parse(fixedInstantStartString)
  private val fixedInstantEnd         = Instant.parse(fixedInstantEndString)
  private val fixedTimestamp          = ZonedDateTime.parse(fixedInstantString)

  "Decision" should {
    "create an instance with minimum required fields" in {
      val decision = Decision(
        bindingCommodityCode = "0123456789",
        justification = "Test justification",
        goodsDescription = "Test goods"
      )

      decision.bindingCommodityCode         shouldBe "0123456789"
      decision.justification                shouldBe "Test justification"
      decision.goodsDescription             shouldBe "Test goods"
      decision.effectiveStartDate           shouldBe None
      decision.effectiveEndDate             shouldBe None
      decision.methodSearch                 shouldBe None
      decision.methodCommercialDenomination shouldBe None
      decision.methodExclusion              shouldBe None
      decision.explanation                  shouldBe None
      decision.decisionPdf                  shouldBe None
      decision.letterPdf                    shouldBe None
    }

    "create an instance with all fields" in {
      val decisionPdf = Attachment("decision-123", true, fixedTimestamp)
      val letterPdf   = Attachment("letter-123", false, fixedTimestamp)

      val decision = Decision(
        bindingCommodityCode = "0123456789",
        effectiveStartDate = Some(fixedInstantStart),
        effectiveEndDate = Some(fixedInstantEnd),
        justification = "Test justification",
        goodsDescription = "Test goods",
        methodSearch = Some("Search method"),
        methodCommercialDenomination = Some("Commercial denomination method"),
        methodExclusion = Some("Exclusion method"),
        explanation = Some("Test explanation"),
        decisionPdf = Some(decisionPdf),
        letterPdf = Some(letterPdf)
      )

      decision.bindingCommodityCode         shouldBe "0123456789"
      decision.effectiveStartDate           shouldBe Some(fixedInstantStart)
      decision.effectiveEndDate             shouldBe Some(fixedInstantEnd)
      decision.justification                shouldBe "Test justification"
      decision.goodsDescription             shouldBe "Test goods"
      decision.methodSearch                 shouldBe Some("Search method")
      decision.methodCommercialDenomination shouldBe Some("Commercial denomination method")
      decision.methodExclusion              shouldBe Some("Exclusion method")
      decision.explanation                  shouldBe Some("Test explanation")
      decision.decisionPdf                  shouldBe Some(decisionPdf)
      decision.letterPdf                    shouldBe Some(letterPdf)
    }

    "be equal when values match" in {
      val decisionPdf = Attachment("decision-123", true, fixedTimestamp)
      val letterPdf   = Attachment("letter-123", false, fixedTimestamp)

      val decision1 = Decision(
        bindingCommodityCode = "0123456789",
        effectiveStartDate = Some(fixedInstantStart),
        effectiveEndDate = Some(fixedInstantEnd),
        justification = "Test justification",
        goodsDescription = "Test goods",
        methodSearch = Some("Search method"),
        methodCommercialDenomination = Some("Commercial denomination"),
        methodExclusion = Some("Exclusion method"),
        explanation = Some("Test explanation"),
        decisionPdf = Some(decisionPdf),
        letterPdf = Some(letterPdf)
      )

      val decision2 = Decision(
        bindingCommodityCode = "0123456789",
        effectiveStartDate = Some(fixedInstantStart),
        effectiveEndDate = Some(fixedInstantEnd),
        justification = "Test justification",
        goodsDescription = "Test goods",
        methodSearch = Some("Search method"),
        methodCommercialDenomination = Some("Commercial denomination"),
        methodExclusion = Some("Exclusion method"),
        explanation = Some("Test explanation"),
        decisionPdf = Some(decisionPdf),
        letterPdf = Some(letterPdf)
      )

      decision1 shouldBe decision2
    }

    "not be equal when fields differ" in {
      val decisionPdf = Attachment("decision-123", true, fixedTimestamp)
      val letterPdf   = Attachment("letter-123", false, fixedTimestamp)

      val decision1 = Decision(
        bindingCommodityCode = "0123456789",
        effectiveStartDate = Some(fixedInstantStart),
        effectiveEndDate = Some(fixedInstantEnd),
        justification = "Test justification",
        goodsDescription = "Test goods",
        methodSearch = Some("Search method"),
        methodCommercialDenomination = Some("Commercial denomination"),
        methodExclusion = Some("Exclusion method"),
        explanation = Some("Test explanation"),
        decisionPdf = Some(decisionPdf),
        letterPdf = Some(letterPdf)
      )

      val decision2 = Decision(
        bindingCommodityCode = "9876543210", // Different commodity code
        effectiveStartDate = Some(fixedInstantStart),
        effectiveEndDate = Some(fixedInstantEnd),
        justification = "Test justification",
        goodsDescription = "Test goods",
        methodSearch = Some("Search method"),
        methodCommercialDenomination = Some("Commercial denomination"),
        methodExclusion = Some("Exclusion method"),
        explanation = Some("Test explanation"),
        decisionPdf = Some(decisionPdf),
        letterPdf = Some(letterPdf)
      )

      decision1 should not be decision2
    }
  }

  "Decision JSON Format" should {
    "serialize a Decision with minimum required fields" in {
      val decision = Decision(
        bindingCommodityCode = "0123456789",
        justification = "Test justification",
        goodsDescription = "Test goods"
      )

      val expectedJson = Json.obj(
        "bindingCommodityCode" -> "0123456789",
        "justification"        -> "Test justification",
        "goodsDescription"     -> "Test goods"
      )

      Json.toJson(decision) shouldBe expectedJson
    }

    "serialize a Decision with all fields" in {
      val decisionPdf = Attachment("decision-123", true, fixedTimestamp)
      val letterPdf   = Attachment("letter-123", false, fixedTimestamp)

      val decision = Decision(
        bindingCommodityCode = "0123456789",
        effectiveStartDate = Some(fixedInstantStart),
        effectiveEndDate = Some(fixedInstantEnd),
        justification = "Test justification",
        goodsDescription = "Test goods",
        methodSearch = Some("Search method"),
        methodCommercialDenomination = Some("Commercial denomination method"),
        methodExclusion = Some("Exclusion method"),
        explanation = Some("Test explanation"),
        decisionPdf = Some(decisionPdf),
        letterPdf = Some(letterPdf)
      )

      val expectedJson = Json.obj(
        "bindingCommodityCode"         -> "0123456789",
        "effectiveStartDate"           -> fixedInstantStartString,
        "effectiveEndDate"             -> fixedInstantEndString,
        "justification"                -> "Test justification",
        "goodsDescription"             -> "Test goods",
        "methodSearch"                 -> "Search method",
        "methodCommercialDenomination" -> "Commercial denomination method",
        "methodExclusion"              -> "Exclusion method",
        "explanation"                  -> "Test explanation",
        "decisionPdf" -> Json.obj(
          "id"        -> "decision-123",
          "public"    -> true,
          "timestamp" -> fixedInstantString
        ),
        "letterPdf" -> Json.obj(
          "id"        -> "letter-123",
          "public"    -> false,
          "timestamp" -> fixedInstantString
        )
      )

      Json.toJson(decision) shouldBe expectedJson
    }

    "deserialize a JSON with minimum required fields" in {
      val json = Json.obj(
        "bindingCommodityCode" -> "0123456789",
        "justification"        -> "Test justification",
        "goodsDescription"     -> "Test goods"
      )

      val expected = Decision(
        bindingCommodityCode = "0123456789",
        justification = "Test justification",
        goodsDescription = "Test goods"
      )

      json.as[Decision] shouldBe expected
    }

    "deserialize a JSON with all fields" in {
      val json = Json.obj(
        "bindingCommodityCode"         -> "0123456789",
        "effectiveStartDate"           -> fixedInstantStart.toString,
        "effectiveEndDate"             -> fixedInstantEnd.toString,
        "justification"                -> "Test justification",
        "goodsDescription"             -> "Test goods",
        "methodSearch"                 -> "Search method",
        "methodCommercialDenomination" -> "Commercial denomination method",
        "methodExclusion"              -> "Exclusion method",
        "explanation"                  -> "Test explanation",
        "decisionPdf" -> Json.obj(
          "id"        -> "decision-123",
          "public"    -> true,
          "timestamp" -> fixedTimestamp.toString
        ),
        "letterPdf" -> Json.obj(
          "id"        -> "letter-123",
          "public"    -> false,
          "timestamp" -> fixedTimestamp.toString
        )
      )

      val decisionPdf = Attachment("decision-123", true, fixedTimestamp)
      val letterPdf   = Attachment("letter-123", false, fixedTimestamp)

      val expected = Decision(
        bindingCommodityCode = "0123456789",
        effectiveStartDate = Some(fixedInstantStart),
        effectiveEndDate = Some(fixedInstantEnd),
        justification = "Test justification",
        goodsDescription = "Test goods",
        methodSearch = Some("Search method"),
        methodCommercialDenomination = Some("Commercial denomination method"),
        methodExclusion = Some("Exclusion method"),
        explanation = Some("Test explanation"),
        decisionPdf = Some(decisionPdf),
        letterPdf = Some(letterPdf)
      )

      json.as[Decision] shouldBe expected
    }

    "fail to deserialize when bindingCommodityCode is missing" in {
      val json = Json.obj(
        "justification"    -> "Test justification",
        "goodsDescription" -> "Test goods"
      )

      intercept[JsResultException] {
        json.as[Decision]
      }
    }

    "fail to deserialize when justification is missing" in {
      val json = Json.obj(
        "bindingCommodityCode" -> "0123456789",
        "goodsDescription"     -> "Test goods"
      )

      intercept[JsResultException] {
        json.as[Decision]
      }
    }

    "fail to deserialize when goodsDescription is missing" in {
      val json = Json.obj(
        "bindingCommodityCode" -> "0123456789",
        "justification"        -> "Test justification"
      )

      intercept[JsResultException] {
        json.as[Decision]
      }
    }

    "handle null values for optional fields" in {
      val json = Json.obj(
        "bindingCommodityCode"         -> "0123456789",
        "justification"                -> "Test justification",
        "goodsDescription"             -> "Test goods",
        "methodSearch"                 -> JsNull,
        "methodCommercialDenomination" -> JsNull,
        "methodExclusion"              -> JsNull,
        "explanation"                  -> JsNull,
        "decisionPdf"                  -> JsNull,
        "letterPdf"                    -> JsNull
      )

      val expected = Decision(
        bindingCommodityCode = "0123456789",
        justification = "Test justification",
        goodsDescription = "Test goods"
      )

      json.as[Decision] shouldBe expected
    }

    "handle round-trip serialization/deserialization" in {
      val decisionPdf = Attachment("decision-123", true, fixedTimestamp)
      val letterPdf   = Attachment("letter-123", false, fixedTimestamp)

      val original = Decision(
        bindingCommodityCode = "0123456789",
        effectiveStartDate = Some(fixedInstantStart),
        effectiveEndDate = Some(fixedInstantEnd),
        justification = "Test justification",
        goodsDescription = "Test goods",
        methodSearch = Some("Search method"),
        methodCommercialDenomination = Some("Commercial denomination method"),
        methodExclusion = Some("Exclusion method"),
        explanation = Some("Test explanation"),
        decisionPdf = Some(decisionPdf),
        letterPdf = Some(letterPdf)
      )

      val json         = Json.toJson(original)
      val deserialized = json.as[Decision]

      deserialized shouldBe original
    }

    "fail to deserialize with invalid date formats" in {
      val json = Json.obj(
        "bindingCommodityCode" -> "0123456789",
        "effectiveStartDate"   -> "not-a-date",
        "justification"        -> "Test justification",
        "goodsDescription"     -> "Test goods"
      )

      intercept[JsResultException] {
        json.as[Decision]
      }
    }

    "fail to deserialize with invalid Attachment format" in {
      val json = Json.obj(
        "bindingCommodityCode" -> "0123456789",
        "justification"        -> "Test justification",
        "goodsDescription"     -> "Test goods",
        "decisionPdf"          -> "not-an-object"
      )

      intercept[JsResultException] {
        json.as[Decision]
      }
    }
  }
}
