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

package utils

import models.SelectApplicationType.NewCommodity
import models.WhichBestDescribesYou.BusinessOwner
import models._
import models.requests.DataRequest
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import pages._
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec

class CheckYourAnswersHelperSpec extends UnitSpec with MockitoSugar {

  private val userAnswers = mock[UserAnswers]
  private val checkHelper = new CheckYourAnswersHelper(userAnswers)
  private val fileAttachment = FileAttachment("id", "fileName", "mime", 1)

  "CheckYourAnswersHelper" when {

    "rendering yes/no pages" must {

      "return a row with the correct answer for CommodityCodeBestMatchPage" in {
        given(userAnswers.get(CommodityCodeBestMatchPage)).willReturn(Option(true))
        checkHelper.commodityCodeBestMatch.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for WhenToSendSamplePage" in {
        given(userAnswers.get(WhenToSendSamplePage)).willReturn(Option(true))
        checkHelper.whenToSendSample.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for SupportingInformationDetailsPage" in {
        given(userAnswers.get(SupportingInformationPage)).willReturn(Option(false))
        checkHelper.supportingInformation.get.answer shouldBe "site.no"
      }

      "return a row with the correct answer for LegalChallengePage" in {
        given(userAnswers.get(LegalChallengePage)).willReturn(Option(false))
        checkHelper.legalChallenge.get.answer shouldBe "site.no"
      }

      "return a row with the correct answer for SimilarItemCommodityCodePage" in {
        given(userAnswers.get(SimilarItemCommodityCodePage)).willReturn(Option(true))
        checkHelper.similarItemCommodityCode.get.answer shouldBe "site.yes"
      }

    }

    "rendering information pages" must {

      "return a row with the correct answer for CommodityCodeDigitsPage" in {
        given(userAnswers.get(CommodityCodeDigitsPage)).willReturn(Option("12131233241324"))
        checkHelper.commodityCodeDigits.get.answer shouldBe "12131233241324"
      }

      "return a row with the correct answer for ReturnSamplesPage" in {
        given(userAnswers.get(ReturnSamplesPage)).willReturn(Option(ReturnSamples.Yes))
        checkHelper.returnSamples.get.answer shouldBe "returnSamples.yesReturnSamples"
      }

      "return a row with the correct answer for UploadWrittenAuthorisationPage" in {
        given(userAnswers.get(UploadWrittenAuthorisationPage)).willReturn(Option(fileAttachment))
        checkHelper.uploadWrittenAuthorisation.get.answer shouldBe "fileName"
      }

      "return a row with the correct answer for SupportingInformationDetailsPage" in {
        given(userAnswers.get(SupportingInformationDetailsPage)).willReturn(Option("Supporting information"))
        checkHelper.supportingInformationDetails.get.answer shouldBe "Supporting information"
      }

      "return a row with the correct answer for LegalChallengeDetailsPage" in {
        given(userAnswers.get(LegalChallengeDetailsPage)).willReturn(Option("Legal challenge"))
        checkHelper.legalChallengeDetails.get.answer shouldBe "Legal challenge"
      }

      "return a row with the correct answer for CommodityCodeRulingReferencePage" in {
        given(userAnswers.get(CommodityCodeRulingReferencePage)).willReturn(Option("code ruling"))
        checkHelper.commodityCodeRulingReference.get.answer shouldBe "code ruling"
      }

      "return a row with the correct answer for DescribeYourItemPage" in {
        given(userAnswers.get(DescribeYourItemPage)).willReturn(Option(DescribeYourItem("field1", "field2", None)))
        checkHelper.describeYourItem.get.answer shouldBe "field1\nfield2"
      }

      "return a row with the correct answer for PreviousCommodityCodePage" in {
        given(userAnswers.get(PreviousCommodityCodePage)).willReturn(Option(PreviousCommodityCode("122523847624")))
        checkHelper.previousCommodityCode.get.answer shouldBe "122523847624"
      }

      "return a row with the correct answer for EnterContactDetailsPage" in {
        given(userAnswers.get(EnterContactDetailsPage)).willReturn(Option(EnterContactDetails("field1", "field2", Some("field3"))))
        checkHelper.enterContactDetails.get.answer shouldBe "field1\nfield2\nfield3"
      }

      "return a row with the correct answer for RegisterBusinessRepresentingPage" in {
        given(userAnswers.get(RegisterBusinessRepresentingPage)).willReturn(
          Option(RegisterBusinessRepresenting("eoriNumber", "businessName", "addressLine1", "town", "postCode", "country")))
        checkHelper.registerBusinessRepresenting.get.answer shouldBe "eoriNumber\nbusinessName\naddressLine1\ntown\npostCode\ncountry"
      }

      "return a row with the correct answer for SelectApplicationTypePage" in {
        given(userAnswers.get(SelectApplicationTypePage)).willReturn(Option(NewCommodity))
        checkHelper.selectApplicationType.get.answer shouldBe "selectApplicationType.newCommodity"
      }

      "return a row with the correct answer for importOrExportPage" in {
        given(userAnswers.get(ImportOrExportPage)).willReturn(Option(ImportOrExport.Import))
        checkHelper.importOrExport.get.answer shouldBe "importOrExport.import"
      }

      "return a row with the correct answer for WhichBestDescribesYouPage" in {
        given(userAnswers.get(WhichBestDescribesYouPage)).willReturn(Option(BusinessOwner))
        checkHelper.whichBestDescribesYou.get.answer shouldBe "whichBestDescribesYou.businessOwner"
      }

      "return a row with the correct answer for RegisteredAddressForEoriPage when CDS check disabled" in {
        val requestWithoutEori = DataRequest(FakeRequest(), "", None, mock[UserAnswers])
        given(userAnswers.get(RegisteredAddressForEoriPage)).willReturn(Option(RegisteredAddressForEori("eori", "f1", "f2", "f3", "f4", "f5")))
        checkHelper.registeredAddressForEori(requestWithoutEori).get.answer shouldBe "eori\nf1\nf2\nf3\nf4\nf5"
      }

      "return a row with the correct answer for RegisteredAddressForEoriPage when CDS check enabled" in {
        val requestWithEori = DataRequest(FakeRequest(), "", Some("eori"), mock[UserAnswers])
        given(userAnswers.get(RegisteredAddressForEoriPage)).willReturn(Option(RegisteredAddressForEori("eori", "f1", "f2", "f3", "f4", "f5")))
        checkHelper.registeredAddressForEori(requestWithEori).get.answer shouldBe "f1\nf2\nf3\nf4\nf5"
      }

    }
  }
}
