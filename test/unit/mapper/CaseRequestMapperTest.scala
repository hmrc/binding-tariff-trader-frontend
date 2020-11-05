/*
 * Copyright 2020 HM Revenue & Customs
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

import controllers.actions.FakeIdentifierAction.frontendAppConfig
import models._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages._
import play.api.libs.json.{JsValue, Json, Writes}
import uk.gov.hmrc.http.cache.client.CacheMap
import unit.utils.UnitSpec

class CaseRequestMapperTest extends UnitSpec with GuiceOneAppPerSuite {

  private val mapper = new CaseRequestMapper(frontendAppConfig)

  "Mapper" should {

    "Fail when mandatory fields are missing" in {
      intercept[IllegalStateException] {
        mapper.map(missingGoodDetails())
      }
    }

    "Map Mandatory Fields" in {
      // When
      val response = mapper.map(mandatoryAnswers())

      // Then Mandatory fields should be present
      response.attachments shouldBe Seq.empty

      val application = response.application

      val holder: EORIDetails = application.holder
      holder.eori shouldBe "Trader EORI"
      holder.businessName shouldBe "Trader Business Name"
      holder.addressLine1 shouldBe "Trader Address Line 1"
      holder.addressLine2 shouldBe "Trader Town"
      holder.postcode shouldBe "Trader Post Code"
      holder.country shouldBe "Trader Country"

      val contact: Contact = application.contact
      contact.name shouldBe "Name"
      contact.email shouldBe "Email"
      contact.phone shouldBe Some("Tel No")

      application.offline shouldBe false
      application.goodName shouldBe "Good Name"
      application.goodDescription shouldBe "Good Description"
      application.sampleToBeProvided shouldBe true
      application.sampleToBeReturned shouldBe false

      // Then optional Fields should be blank
      application.agent shouldBe None
      application.confidentialInformation shouldBe None
      application.otherInformation shouldBe None
      application.reissuedBTIReference shouldBe None
      application.relatedBTIReferences shouldBe Nil
      application.knownLegalProceedings shouldBe None
      application.envisagedCommodityCode shouldBe None
    }
  }

  // trader filling the form
  def mandatoryAnswers(): UserAnswers = {
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
  }

  def missingGoodDetails(): UserAnswers = {
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
              "Phone"
            )
          ),
          AreYouSendingSamplesPage.toString -> js(true)
        )
      )
    )
  }

  def js[T](obj: T)(implicit writes: Writes[T]): JsValue = Json.toJson(obj)

}
