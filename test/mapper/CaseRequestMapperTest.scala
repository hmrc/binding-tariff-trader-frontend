package mapper

import models._
import pages._
import play.api.libs.json.{JsValue, Json, Writes}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.UnitSpec

class CaseRequestMapperTest extends UnitSpec {

  private val mapper = new CaseRequestMapper()


  "Mapper" should {
    "Map Mandatory Fields" in {
      // When
      val response = mapper.map(mandatoryAnswers())

      // Then Mandatory fields should be present
      response.attachments shouldBe Seq.empty

      val application = response.application

      val holder: EORIDetails = application.holder
      holder.eori shouldBe ""
      holder.traderName shouldBe "Trader Name"
      holder.addressLine1 shouldBe "Address Line 1"
      holder.addressLine2 shouldBe "Address Line 2"
      holder.postcode shouldBe "Post Code"
      holder.country shouldBe "Country"

      val contact: Contact = application.contact
      contact.name shouldBe "Name"
      contact.email shouldBe "Email"
      contact.phone shouldBe "Phone"

      application.agent shouldBe None // TODO Implement This
      application.offline shouldBe false
      application.goodName shouldBe "Good Name"
      application.goodDescription shouldBe "Good Description"
      application.sampleToBeProvided shouldBe false
      application.sampleToBeReturned shouldBe false

      // Then optional Fields should be blank
      application.confidentialInformation shouldBe None
      application.otherInformation shouldBe None
      application.reissuedBTIReference shouldBe None
      application.relatedBTIReference shouldBe None
      application.knownLegalProceedings shouldBe None
      application.envisagedCommodityCode shouldBe None
    }

    "Map Optional Fields" in {
      // When
      val response = mapper.map(allAnswers())

      // Then Mandatory fields should be present
      response.attachments shouldBe Seq.empty

      val application = response.application

      val holder: EORIDetails = application.holder
      holder.eori shouldBe ""
      holder.traderName shouldBe "Trader Name"
      holder.addressLine1 shouldBe "Address Line 1"
      holder.addressLine2 shouldBe "Address Line 2"
      holder.postcode shouldBe "Post Code"
      holder.country shouldBe "Country"

      val contact: Contact = application.contact
      contact.name shouldBe "Name"
      contact.email shouldBe "Email"
      contact.phone shouldBe "Phone"

      application.agent shouldBe None // TODO Implement This
      application.offline shouldBe false
      application.goodName shouldBe "Good Name"
      application.goodDescription shouldBe "Good Description"
      application.sampleToBeProvided shouldBe false
      application.sampleToBeReturned shouldBe false

      // Then optional Fields should be blank
      application.confidentialInformation shouldBe Some("Confidential Info")
      application.otherInformation shouldBe Some("Other Info")
      application.reissuedBTIReference shouldBe Some("Reissued BTI Reference")
      application.relatedBTIReference shouldBe Some("Related BTI Reference")
      application.knownLegalProceedings shouldBe Some("Known Legal Proceedings")
      application.envisagedCommodityCode shouldBe Some("Envisaged Commodity Code")
    }
  }

  def allAnswers(): UserAnswers = {
    UserAnswers(
      CacheMap(
        "id",
        Map(
          RegisteredAddressForEoriPage.toString -> js(
            RegisteredAddressForEori(
              "Trader Name",
              "Address Line 1",
              "Address Line 2",
              "Post Code",
              "Country"
            )
          ),
          EnterContactDetailsPage.toString -> js(
            EnterContactDetails(
              "Name",
              "Email",
              "Phone"
            )
          ),
          DescribeYourItemPage.toString -> js(
            DescribeYourItem(
              "Good Name",
              "Good Description"
            )
          ),
          ConfidentialInformationPage.toString -> js(
            ConfidentialInformation(
              "Confidential Info"
            )
          ),
          SupportingInformationDetailsPage.toString -> js("Other Info"),
          PreviousCommodityCodePage.toString -> js(
            PreviousCommodityCode(
              "Reissued BTI Reference"
            )
          ),
          CommodityCodeRulingReferencePage.toString -> js("Related BTI Reference"),
          LegalChallengeDetailsPage.toString -> js("Known Legal Proceedings"),
          CommodityCodeDigitsPage.toString -> js("Envisaged Commodity Code")
        )
      )
    )

  }

  def mandatoryAnswers(): UserAnswers = {
    UserAnswers(
      CacheMap(
        "id",
        Map(
          RegisteredAddressForEoriPage.toString -> js(
            RegisteredAddressForEori(
              "Trader Name",
              "Address Line 1",
              "Address Line 2",
              "Post Code",
              "Country"
            )
          ),
          EnterContactDetailsPage.toString -> js(
            EnterContactDetails(
              "Name",
              "Email",
              "Phone"
            )
          ),
          DescribeYourItemPage.toString -> js(
            DescribeYourItem(
              "Good Name",
              "Good Description"
            )
          )
        )
      )
    )
  }

  def js[T](obj: T)(implicit writes: Writes[T]): JsValue = Json.toJson(obj)

}
