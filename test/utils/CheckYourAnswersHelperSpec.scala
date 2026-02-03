/*
 * Copyright 2026 HM Revenue & Customs
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

import base.SpecBase
import models.*
import models.requests.DataRequest
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.{mock, when}
import pages.*
import play.api.libs.json.Reads
import play.api.test.FakeRequest
import service.CountriesService

class CheckYourAnswersHelperSpec extends SpecBase {

  val countriesService = new CountriesService

  private val userAnswers = mock(classOf[UserAnswers])
  private val checkHelper = new CheckYourAnswersHelper(
    userAnswers,
    countriesService.getAllCountriesById
  )(messages)

  "CheckYourAnswersHelper" when {

    "rendering yes/no pages" must {

      "return a row with the correct answer for CommodityCodeBestMatchPage" in {
        when(userAnswers.get(CommodityCodeBestMatchPage)).thenReturn(Option(true))
        checkHelper.commodityCodeBestMatch.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for AreYouSendingSamplesPage" in {
        when(userAnswers.get(AreYouSendingSamplesPage)).thenReturn(Option(true))
        checkHelper.areYouSendingSamples.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for IsSampleHazardous" in {
        when(userAnswers.get(IsSampleHazardousPage)).thenReturn(Option(true))
        checkHelper.isSampleHazardous.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for ReturnSamplesPage" in {
        when(userAnswers.get(ReturnSamplesPage)).thenReturn(Option(true))
        checkHelper.returnSamples.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for LegalChallengePage" in {
        when(userAnswers.get(LegalChallengePage)).thenReturn(Option(false))
        checkHelper.legalChallenge.get.answer shouldBe "site.no"
      }

      "return a row with the correct answer for SimilarItemCommodityCodePage" in {
        when(userAnswers.get(SimilarItemCommodityCodePage)).thenReturn(Option(true))
        checkHelper.similarItemCommodityCode.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for PreviousBTIRulingPage" in {
        when(userAnswers.get(PreviousBTIRulingPage)).thenReturn(Option(true))
        checkHelper.previousBTIRuling.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for AddConfidentialInformationPage" in {
        when(userAnswers.get(AddConfidentialInformationPage)).thenReturn(Option(true))
        checkHelper.addConfidentialInformation().get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for AddSupportingDocumentsPage" in {
        when(userAnswers.get(AddSupportingDocumentsPage)).thenReturn(Option(true))
        checkHelper.supportingMaterialFileListChoice.get.answer shouldBe "site.yes"
      }
    }

    "rendering information pages" must {

      "return a row with the correct answer for CommodityCodeDigitsPage" in {
        when(userAnswers.get(CommodityCodeDigitsPage)).thenReturn(Option("12131233241324"))
        checkHelper.commodityCodeDigits.get.answer shouldBe "12131233241324"
      }

      "return a row with the correct answer for LegalChallengeDetailsPage" in {
        when(userAnswers.get(LegalChallengeDetailsPage)).thenReturn(Option("Legal challenge"))
        checkHelper.legalChallengeDetails.get.answer shouldBe "Legal challenge"
      }

      "return a row with the correct answer for CommodityCodeRulingReferencePage" in {
        when(userAnswers.get(any[CommodityCodeRulingReferencePage.type])(any[Reads[List[String]]]))
          .thenReturn(Option(List("code ruling")))
        checkHelper.commodityCodeRulingReference.get.answer shouldBe "code ruling"
      }

      "return a row with the correct answer for ProvideBTIReferencePage" in {
        when(userAnswers.get(ProvideBTIReferencePage)).thenReturn(Option(BTIReference("122523847624")))
        checkHelper.provideBTIReference.get.answer shouldBe "122523847624"
      }

      "return a row with the correct answer for EnterContactDetailsPage Name" in {
        when(userAnswers.get(EnterContactDetailsPage))
          .thenReturn(Option(EnterContactDetails("name", "email", "phoneNumber")))
        checkHelper.enterContactDetailsName.get.answer shouldBe "name"
      }

      "return a row with the correct answer for EnterContactDetailsPage Email" in {
        when(userAnswers.get(EnterContactDetailsPage))
          .thenReturn(Option(EnterContactDetails("name", "email", "phoneNumber")))
        checkHelper.enterContactDetailsEmail.get.answer shouldBe "email"
      }

      "return a row with the correct answer for EnterContactDetailsPage Telephone" in {
        when(userAnswers.get(EnterContactDetailsPage))
          .thenReturn(Option(EnterContactDetails("name", "email", "phoneNumber")))
        checkHelper.enterContactDetailsPhone.get.answer shouldBe "phoneNumber"
      }

      "return a row with the correct answer for RegisteredAddressForEoriPage when CDS check disabled" in {
        val requestWithoutEori = DataRequest(FakeRequest(), "", None, mock(classOf[UserAnswers]))
        when(userAnswers.get(RegisteredAddressForEoriPage))
          .thenReturn(Option(RegisteredAddressForEori("eori", "f1", "f2", "f3", Some("f4"), "IE")))
        checkHelper.registeredAddressForEori(requestWithoutEori).get.answer shouldBe "eori\nf1\nf2\nf3\nf4\nIreland"
      }

      "return a row with the correct answer for RegisteredAddressForEoriPage when CDS check enabled" in {
        val requestWithEori = DataRequest(FakeRequest(), "", Some("eori"), mock(classOf[UserAnswers]))
        when(userAnswers.get(RegisteredAddressForEoriPage))
          .thenReturn(Option(RegisteredAddressForEori("eori", "f1", "f2", "f3", Some("f4"), "IE")))
        checkHelper.registeredAddressForEori(requestWithEori).get.answer shouldBe "f1\nf2\nf3\nf4\nIreland"
      }

      "return a row with the correct answer for registered name" in {
        when(userAnswers.get(RegisteredAddressForEoriPage))
          .thenReturn(Option(RegisteredAddressForEori("eori", "f1", "f2", "f3", Some("f4"), "IE")))
        checkHelper.registeredName.get.answer shouldBe "f1"
      }

      "return a row with the correct answer for registered address" in {
        when(userAnswers.get(RegisteredAddressForEoriPage))
          .thenReturn(Option(RegisteredAddressForEori("eori", "f1", "f2", "f3", Some("f4"), "IE")))
        checkHelper.registeredAddress.get.answer shouldBe "f2\nf3\nf4\nIreland"
      }

      "return a row with the correct answer for ProvideGoodsNamePage" in {
        when(userAnswers.get(ProvideGoodsNamePage)).thenReturn(Option("Goods Name"))
        checkHelper.provideGoodsName.get.answer shouldBe "Goods Name"
      }

      "return a row with the correct answer for ProvideGoodsDescriptionPage" in {
        when(userAnswers.get(ProvideGoodsDescriptionPage)).thenReturn(Option("Goods Description"))
        checkHelper.provideGoodsDescription.get.answer shouldBe "Goods Description"
      }

      "return a row with the correct answer for ProvideConfidentialInformationPage" in {
        when(userAnswers.get(ProvideConfidentialInformationPage)).thenReturn(Option("Confidential info"))
        checkHelper.provideConfidentialInformation.get.answer shouldBe "Confidential info"
      }
    }
  }
}
