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

package mapper

import base.SpecBase
import models._
import pages._
import play.api.libs.json.{JsValue, Json, Writes}
import models.cache.CacheMap

class CaseRequestMapperTest extends SpecBase {

  private val mapper = new CaseRequestMapper(frontendAppConfig)

  "Mapper" should {

    "Fail" when {
      "mandatory field goodDescription is missing" in {
        intercept[IllegalStateException] {
          mapper.map(mandatoryAnswers().remove(ProvideGoodsDescriptionPage))
        }.getMessage shouldBe "Missing User Session Data: goods description"
      }

      "mandatory field goodName is missing" in {
        intercept[IllegalStateException] {
          mapper.map(mandatoryAnswers().remove(ProvideGoodsNamePage))
        }.getMessage shouldBe "Missing User Session Data: goods name"
      }

      "mandatory field contact is missing" in {
        intercept[IllegalStateException] {
          mapper.map(mandatoryAnswers().remove(EnterContactDetailsPage))
        }.getMessage shouldBe "Missing User Session Data: contact details"
      }

      "mandatory field holder is missing" in {
        intercept[IllegalStateException] {
          mapper.map(mandatoryAnswers().remove(RegisteredAddressForEoriPage))
        }.getMessage shouldBe "Missing User Session Data: holder EORI details"
      }

      "mandatory field sampleToBeProvided is missing" in {
        intercept[IllegalStateException] {
          mapper.map(mandatoryAnswers().remove(AreYouSendingSamplesPage))
        }.getMessage shouldBe "Missing User Session Data: when to send a sample"
      }
    }

    "not fail but set sampleToBeProvided to false" when {
      "the toggle.samplesNotAccepted is set to true and AreYouSendingSamplesPage is removed" in {
        val mapperWithToggle = new CaseRequestMapper(frontendAppConfigWithToggle)
        val response         = mapperWithToggle.map(mandatoryAnswers().remove(AreYouSendingSamplesPage))

        val application = response.application

        application.sampleToBeProvided shouldBe false
      }
    }

    "Map Mandatory Fields" in {
      // When
      val response = mapper.map(mandatoryAnswers())

      // Then Mandatory fields should be present
      response.attachments shouldBe Seq.empty

      val application = response.application

      val holder: EORIDetails = application.holder
      holder.eori         shouldBe "Trader EORI"
      holder.businessName shouldBe "Trader Business Name"
      holder.addressLine1 shouldBe "Trader Address Line 1"
      holder.addressLine2 shouldBe "Trader Town"
      holder.postcode     shouldBe "Trader Post Code"
      holder.country      shouldBe "Trader Country"

      val contact: Contact = application.contact
      contact.name  shouldBe "Name"
      contact.email shouldBe "Email"
      contact.phone shouldBe Some("Tel No")

      application.offline            shouldBe false
      application.goodName           shouldBe "Good Name"
      application.goodDescription    shouldBe "Good Description"
      application.sampleToBeProvided shouldBe true
      application.sampleToBeReturned shouldBe false

      // Then optional Fields should be blank
      application.agent                   shouldBe None
      application.confidentialInformation shouldBe None
      application.otherInformation        shouldBe None
      application.reissuedBTIReference    shouldBe None
      application.relatedBTIReferences    shouldBe Nil
      application.knownLegalProceedings   shouldBe None
      application.envisagedCommodityCode  shouldBe None
    }

    "Map optional fields" in {
      val response = mapper.map(
        mandatoryAnswers()
          .remove(AddConfidentialInformationPage)
          .set(AddConfidentialInformationPage, true)
          .set(ProvideConfidentialInformationPage, "confidential info")
          .set(ProvideBTIReferencePage, BTIReference("ref"))
      )

      val application = response.application

      application.confidentialInformation shouldBe Some("confidential info")
      application.reissuedBTIReference    shouldBe Some("ref")
    }
  }

  // trader filling the form
  def mandatoryAnswers(): UserAnswers =
    UserAnswers(
      CacheMap(
        "id",
        Map(
          RegisteredAddressForEoriPage.toString -> js(
            RegisteredAddressForEori(
              "Trader EORI",
              "Trader Business Name",
              "Trader Address Line 1",
              "Trader Town",
              Some("Trader Post Code"),
              "Trader Country"
            )
          ),
          EnterContactDetailsPage.toString -> js(
            EnterContactDetails(
              "Name",
              "Email",
              "Tel No"
            )
          ),
          ProvideGoodsNamePage.toString -> js(
            "Good Name"
          ),
          ProvideGoodsDescriptionPage.toString -> js(
            "Good Description"
          ),
          AddConfidentialInformationPage.toString -> js(
            false
          ),
          AreYouSendingSamplesPage.toString -> js(true)
        )
      )
    )

  def js[T](obj: T)(implicit writes: Writes[T]): JsValue = Json.toJson(obj)

}
