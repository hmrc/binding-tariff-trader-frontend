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

package utils

import audit.CaseAuditPayload
import base.SpecBase
import utils.JsonFormatters.*
import models.*
import models.requests.NewEventRequest
import play.api.libs.json.{JsError, JsSuccess, Json}
import viewmodels.{FileView, PdfViewModel}

import java.time.{Instant, ZonedDateTime}

class JsonFormattersSpec extends SpecBase {
  "Contact" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = new Contact(
          name = "wds",
          email = "sdaf",
          phone = Some("sdf")
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[Contact]

        deserialized shouldBe original
      }
      "phone is none" in {
        val original = new Contact(
          name = "wds",
          email = "sdaf",
          phone = None
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[Contact]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "maxFileSize is not present" in {
        val json = Json.obj()
        json.validate[Contact] shouldBe a[JsError]
      }
      "there is type mismatch" in {
        val json = Json.obj("name" -> 32, "email" -> 324)
        json.validate[Contact] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[Contact] shouldBe a[JsError]
      }
    }
  }
  "EORIDetails" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = new EORIDetails(
          eori = "sfd",
          businessName = "sdf",
          addressLine1 = "sdf",
          addressLine2 = "sdfd",
          addressLine3 = "dsf",
          postcode = "sdf",
          country = "sdf"
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[EORIDetails]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "maxFileSize is not present" in {
        val json = Json.obj()
        json.validate[EORIDetails] shouldBe a[JsError]
      }
      "there is type mismatch" in {
        val json = Json.obj("min" -> "ad", "max" -> 23)
        json.validate[EORIDetails] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[EORIDetails] shouldBe a[JsError]
      }
    }
  }
  "AgentDetails" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = AgentDetails(
          eoriDetails = EORIDetails(
            eori = "sdf",
            businessName = "fdds",
            addressLine1 = "sdfd",
            addressLine2 = "sdfsd",
            addressLine3 = "dsfds",
            postcode = "dfds",
            country = "sdf"
          ),
          letterOfAuthorisation = Some(Attachment(id = "sda", public = true))
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[AgentDetails]

        deserialized shouldBe original
      }
      "all the values are None" in {
        val original = AgentDetails(
          eoriDetails = EORIDetails(
            eori = "sdf",
            businessName = "fdds",
            addressLine1 = "sdfd",
            addressLine2 = "sdfsd",
            addressLine3 = "dsfds",
            postcode = "dfds",
            country = "sdf"
          ),
          letterOfAuthorisation = None
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[AgentDetails]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "json is empty" in {
        val json = Json.obj()
        json.validate[AgentDetails] shouldBe a[JsError]
      }
      "there is type mismatch" in {
        val json = Json.obj("eoriDetails" -> 1, "letterOfAuthorisation" -> 23)
        json.validate[AgentDetails] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[AgentDetails] shouldBe a[JsError]
      }
    }
  }
  "Application" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = Application(
          `type` = "other",
          holder = EORIDetails("eori", "name", "ln1", "ln2", "ln3", "code", "country"),
          contact = Contact(name = "efwef", email = "wefd", None),
          agent = Some(AgentDetails(EORIDetails("agent", "name", "ln1", "ln2", "ln3", "code", "country"), None)),
          offline = true,
          goodName = "good",
          goodDescription = "good desc",
          confidentialInformation = Some("info"),
          otherInformation = Some("info"),
          reissuedBTIReference = Some("ref"),
          relatedBTIReferences = List("ref"),
          knownLegalProceedings = Some("legal"),
          envisagedCommodityCode = Some("code"),
          sampleToBeProvided = true,
          sampleIsHazardous = Some(true),
          sampleToBeReturned = true,
          applicationPdf = Some(Attachment("id", false))
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[Application]

        deserialized shouldBe original
      }
      "all the values are None" in {
        val original = Application(
          `type` = "other",
          holder = EORIDetails("eori", "name", "ln1", "ln2", "ln3", "code", "country"),
          contact = Contact(name = "efwef", email = "wefd", None),
          agent = None,
          offline = true,
          goodName = "good",
          goodDescription = "good desc",
          confidentialInformation = None,
          otherInformation = None,
          reissuedBTIReference = None,
          relatedBTIReferences = List.empty,
          knownLegalProceedings = None,
          envisagedCommodityCode = None,
          sampleToBeProvided = true,
          sampleIsHazardous = None,
          sampleToBeReturned = true,
          applicationPdf = None
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[Application]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "json is empty" in {
        val json = Json.obj()
        json.validate[Application] shouldBe a[JsError]
      }
      "there is type mismatch" in {
        val json = Json.obj(
          "type"                    -> true,
          "holder"                  -> true,
          "contact"                 -> true,
          "agent"                   -> true,
          "offline"                 -> 2,
          "goodName"                -> 3,
          "goodDescription"         -> 1,
          "confidentialInformation" -> 3,
          "otherInformation"        -> 1,
          "reissuedBTIReference"    -> 43,
          "relatedBTIReferences"    -> true,
          "knownLegalProceedings"   -> 3,
          "envisagedCommodityCode"  -> 45,
          "sampleToBeProvided"      -> 2,
          "sampleIsHazardous"       -> 2,
          "sampleToBeReturned"      -> 4,
          "applicationPdf"          -> 4
        )
        json.validate[Application] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[Application] shouldBe a[JsError]
      }
    }
  }
  "Decision" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = new Decision(
          bindingCommodityCode = "dsff",
          effectiveStartDate = Some(Instant.parse("2020-01-01T09:00:00.00Z")),
          effectiveEndDate = Some(Instant.parse("2020-01-01T09:00:00.00Z")),
          justification = "fds",
          goodsDescription = "sdf",
          methodSearch = Some("sdf"),
          methodExclusion = Some("sdf"),
          methodCommercialDenomination = Some("sdf"),
          explanation = Some("wedw"),
          decisionPdf = Some(Attachment("asdd", true)),
          letterPdf = Some(Attachment("asdd", true))
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[Decision]

        deserialized shouldBe original
      }
      "all the values are None" in {
        val original = new Decision(
          bindingCommodityCode = "dsff",
          effectiveStartDate = None,
          effectiveEndDate = None,
          justification = "fds",
          goodsDescription = "sdf",
          methodSearch = None,
          methodExclusion = None,
          methodCommercialDenomination = None,
          decisionPdf = None,
          letterPdf = None
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[Decision]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "maxFileSize is not present" in {
        val json = Json.obj()
        json.validate[Decision] shouldBe a[JsError]
      }
      "there is type mismatch" in {
        val json = Json.obj("bindingCommodityCode" -> true, "justification" -> 23, "goodsDescription" -> 32)
        json.validate[Decision] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[Decision] shouldBe a[JsError]
      }
    }
  }
  "Operator" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = Operator(
          id = "fsdf",
          name = Some("efdd")
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[Operator]

        deserialized shouldBe original
      }
      "all the values are None" in {
        val original     = new Operator("dsacd")
        val json         = Json.toJson(original)
        val deserialized = json.as[Operator]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "json is empty" in {
        val json = Json.obj()
        json.validate[Operator] shouldBe a[JsError]
      }
      "there is type mismatch" in {
        val json = Json.obj(
          "email"          -> true,
          "id"             -> 32,
          "name"           -> 34,
          "email"          -> 23,
          "role"           -> "dfs",
          "memberOfTeams"  -> 3,
          "managerOfTeams" -> "sdf",
          "permissions"    -> false,
          "deleted"        -> 2
        )
        json.validate[Operator] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[Operator] shouldBe a[JsError]
      }
    }
  }
  "Case" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = Case(
          reference = "asd",
          status = CaseStatus.REFERRED,
          createdDate = Instant.parse("2020-01-01T09:00:00.00Z"),
          assignee = Some(Operator("dsacd")),
          application = Application(
            `type` = "other",
            holder = EORIDetails("eori", "name", "ln1", "ln2", "ln3", "code", "country"),
            contact = Contact(name = "efwef", email = "wefd", None),
            agent = Some(AgentDetails(EORIDetails("agent", "name", "ln1", "ln2", "ln3", "code", "country"), None)),
            offline = true,
            goodName = "good",
            goodDescription = "good desc",
            confidentialInformation = Some("info"),
            otherInformation = Some("info"),
            reissuedBTIReference = Some("ref"),
            relatedBTIReferences = List("ref"),
            knownLegalProceedings = Some("legal"),
            envisagedCommodityCode = Some("code"),
            sampleToBeProvided = true,
            sampleIsHazardous = Some(true),
            sampleToBeReturned = true,
            applicationPdf = Some(Attachment("id", false))
          ),
          decision = Some(
            Decision(
              bindingCommodityCode = "dsff",
              effectiveStartDate = Some(Instant.parse("2020-01-01T09:00:00.00Z")),
              effectiveEndDate = Some(Instant.parse("2020-01-01T09:00:00.00Z")),
              justification = "fds",
              goodsDescription = "sdf",
              methodSearch = Some("sdf"),
              methodExclusion = Some("sdf"),
              methodCommercialDenomination = Some("sdf"),
              explanation = Some("wedw"),
              decisionPdf = Some(Attachment("asdd", true)),
              letterPdf = Some(Attachment("asdd", true))
            )
          ),
          attachments = Seq(
            Attachment(
              id = "asd",
              public = false
            )
          ),
          keywords = Set("dfs"),
          dateOfExtract = Some(Instant.parse("2020-01-01T09:00:00.00Z"))
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[Case]

        deserialized shouldBe original
      }
      "all the values are None" in {
        val original = Case(
          reference = "asd",
          status = CaseStatus.REFERRED,
          createdDate = Instant.parse("2020-01-01T09:00:00.00Z"),
          assignee = None,
          application = Application(
            `type` = "other",
            holder = EORIDetails("eori", "name", "ln1", "ln2", "ln3", "code", "country"),
            contact = Contact(name = "efwef", email = "wefd", None),
            agent = Some(AgentDetails(EORIDetails("agent", "name", "ln1", "ln2", "ln3", "code", "country"), None)),
            offline = true,
            goodName = "good",
            goodDescription = "good desc",
            confidentialInformation = Some("info"),
            otherInformation = Some("info"),
            reissuedBTIReference = Some("ref"),
            relatedBTIReferences = List("ref"),
            knownLegalProceedings = Some("legal"),
            envisagedCommodityCode = Some("code"),
            sampleToBeProvided = true,
            sampleIsHazardous = Some(true),
            sampleToBeReturned = true,
            applicationPdf = Some(Attachment("id", false))
          ),
          decision = None,
          attachments = Seq.empty,
          keywords = Set.empty,
          dateOfExtract = None
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[Case]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "json is empty" in {
        val json = Json.obj()
        json.validate[Case] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[Case] shouldBe a[JsError]
      }
    }
  }
  "Attachment" should {
    "fail to deserialize" when {
      "maxFileSize is not present" in {
        val json = Json.obj()
        json.validate[Attachment] shouldBe a[JsError]
      }
      "there is type mismatch" in {
        val json = Json.obj("id" -> 23, "public" -> 432)
        json.validate[Attachment] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("a" -> "b", "c" -> 2)
        json.validate[Attachment] shouldBe a[JsError]
      }
    }
  }
  "CaseCreated" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = CaseCreated(
          comment = "asd"
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[CaseCreated]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "json is empty" in {
        val json = Json.obj()
        json.validate[CaseCreated] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[CaseCreated] shouldBe a[JsError]
      }
    }
  }
  "Event" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = new Event(
          id = "dsdf",
          details = CaseCreated("dfsd"),
          operator = Operator("dscd"),
          caseReference = "sdf",
          timestamp = Instant.parse("2021-01-01T09:00:00.00Z")
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[Event]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "maxFileSize is not present" in {
        val json = Json.obj()
        json.validate[Event] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[Event] shouldBe a[JsError]
      }
    }
  }
  "NewEventRequest" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = NewEventRequest(
          details = CaseCreated("dfsd"),
          operator = Operator("dscd"),
          timestamp = Instant.parse("2021-01-01T09:00:00.00Z")
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[NewEventRequest]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "maxFileSize is not present" in {
        val json = Json.obj()
        json.validate[NewEventRequest] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[NewEventRequest] shouldBe a[JsError]
      }
    }
  }
  "NewCaseRequest" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = NewCaseRequest(
          Application(
            `type` = "other",
            holder = EORIDetails("eori", "name", "ln1", "ln2", "ln3", "code", "country"),
            contact = Contact(name = "efwef", email = "wefd", None),
            agent = Some(AgentDetails(EORIDetails("agent", "name", "ln1", "ln2", "ln3", "code", "country"), None)),
            offline = true,
            goodName = "good",
            goodDescription = "good desc",
            confidentialInformation = Some("info"),
            otherInformation = Some("info"),
            reissuedBTIReference = Some("ref"),
            relatedBTIReferences = List("ref"),
            knownLegalProceedings = Some("legal"),
            envisagedCommodityCode = Some("code"),
            sampleToBeProvided = true,
            sampleIsHazardous = Some(true),
            sampleToBeReturned = true,
            applicationPdf = Some(Attachment("id", false))
          ),
          Seq(Attachment("id", false))
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[NewCaseRequest]

        deserialized shouldBe original
      }
      "all the values are None" in {
        val original = NewCaseRequest(
          Application(
            `type` = "other",
            holder = EORIDetails("eori", "name", "ln1", "ln2", "ln3", "code", "country"),
            contact = Contact(name = "efwef", email = "wefd", None),
            agent = Some(AgentDetails(EORIDetails("agent", "name", "ln1", "ln2", "ln3", "code", "country"), None)),
            offline = true,
            goodName = "good",
            goodDescription = "good desc",
            confidentialInformation = Some("info"),
            otherInformation = Some("info"),
            reissuedBTIReference = Some("ref"),
            relatedBTIReferences = List("ref"),
            knownLegalProceedings = Some("legal"),
            envisagedCommodityCode = Some("code"),
            sampleToBeProvided = true,
            sampleIsHazardous = Some(true),
            sampleToBeReturned = true,
            applicationPdf = Some(Attachment("id", false))
          ),
          Seq.empty
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[NewCaseRequest]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "json is empty" in {
        val json = Json.obj()
        json.validate[NewCaseRequest] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[NewCaseRequest] shouldBe a[JsError]
      }
    }
  }
  "CaseAuditPayload" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = CaseAuditPayload(
          "case ref",
          Application(
            `type` = "other",
            holder = EORIDetails("eori", "name", "ln1", "ln2", "ln3", "code", "country"),
            contact = Contact(name = "efwef", email = "wefd", None),
            agent = Some(AgentDetails(EORIDetails("agent", "name", "ln1", "ln2", "ln3", "code", "country"), None)),
            offline = true,
            goodName = "good",
            goodDescription = "good desc",
            confidentialInformation = Some("info"),
            otherInformation = Some("info"),
            reissuedBTIReference = Some("ref"),
            relatedBTIReferences = List("ref"),
            knownLegalProceedings = Some("legal"),
            envisagedCommodityCode = Some("code"),
            sampleToBeProvided = true,
            sampleIsHazardous = Some(true),
            sampleToBeReturned = true,
            applicationPdf = Some(Attachment("id", false))
          )
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[CaseAuditPayload]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "json is empty" in {
        val json = Json.obj()
        json.validate[CaseAuditPayload] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[CaseAuditPayload] shouldBe a[JsError]
      }
    }
  }
  "FileView" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original     = FileView("id", "name", false)
        val json         = Json.toJson(original)
        val deserialized = json.as[FileView]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "json is empty" in {
        val json = Json.obj()
        json.validate[FileView] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[FileView] shouldBe a[JsError]
      }
    }
  }
  "PdfViewModel" should {
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original = new PdfViewModel(
          eori = "eori",
          reference = "ref",
          accountDetails = EORIDetails("eori", "name", "ln1", "ln2", "ln3", "code", "country"),
          contact = Contact("name", "email", None),
          dateSubmitted = Instant.now(),
          goodsName = "goods",
          goodsDetails = "details",
          confidentialInformation = Some("info"),
          sendingSample = true,
          hazardousSample = true,
          returnSample = true,
          attachments = Seq(FileView("id", "name", false)),
          foundCommodityCode = Some("code"),
          legalProblems = Some("legal"),
          similarAtarReferences = List("Atar Ref"),
          reissuedBTIReference = Some("BTI Ref")
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[PdfViewModel]
        deserialized shouldBe original
      }
      "all the values are none" in {
        val original = new PdfViewModel(
          eori = "eori",
          reference = "ref",
          accountDetails = EORIDetails("eori", "name", "ln1", "ln2", "ln3", "code", "country"),
          contact = Contact("name", "email", None),
          dateSubmitted = Instant.now(),
          goodsName = "goods",
          goodsDetails = "details",
          confidentialInformation = None,
          sendingSample = true,
          hazardousSample = true,
          returnSample = true,
          attachments = Seq.empty,
          foundCommodityCode = None,
          legalProblems = None,
          similarAtarReferences = List.empty,
          reissuedBTIReference = None
        )
        val json         = Json.toJson(original)
        val deserialized = json.as[PdfViewModel]
        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "maxFileSize is not present" in {
        val json = Json.obj()
        json.validate[PdfViewModel] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[PdfViewModel] shouldBe a[JsError]
      }
    }
  }
  "SetValue" should {
    "map correctly" in {
      val original = SetValue[String]("test")
      original.map(x => x.length) shouldBe SetValue[Int](4)
    }
    "handle round-trip serialization/deserialization" when {
      "all the values are present" in {
        val original     = SetValue[String]("test")
        val json         = Json.toJson(original)
        val deserialized = json.as[SetValue[String]]

        deserialized shouldBe original
      }
    }
    "fail to deserialize" when {
      "json is empty" in {
        val json = Json.obj()
        json.validate[SetValue[String]] shouldBe a[JsError]
      }
      "there is a json array" in {
        val json = Json.arr("min" -> "ad", "max" -> 23)
        json.validate[SetValue[String]] shouldBe a[JsError]
      }
    }
  }
}
