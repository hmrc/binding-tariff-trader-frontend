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

import base.SpecBase
import models._
import models.requests.DataRequest
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import pages._
import play.api.test.FakeRequest
import service.CountriesService
import play.api.libs.json.Reads

class CheckYourAnswersHelperSpec extends SpecBase {

  val countriesService = new CountriesService

  private val userAnswers = mock[UserAnswers]
  private val checkHelper = new CheckYourAnswersHelper(
    userAnswers,
    countriesService.getAllCountriesById,
    messagesApi,
    lang
  )
  private val fileAttachment = FileAttachment("id", "fileName", "mime", 1)

  "CheckYourAnswersHelper" when {

    "rendering yes/no pages" must {

      "return a row with the correct answer for CommodityCodeBestMatchPage" in {
        given(userAnswers.get(CommodityCodeBestMatchPage)).willReturn(Option(true))
        checkHelper.commodityCodeBestMatch.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for AreYouSendingSamplesPage" in {
        given(userAnswers.get(AreYouSendingSamplesPage)).willReturn(Option(true))
        checkHelper.areYouSendingSamples.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for IsSampleHazardous" in {
        given(userAnswers.get(IsSampleHazardousPage)).willReturn(Option(true))
        checkHelper.isSampleHazardous.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for ReturnSamplesPage" in {
        given(userAnswers.get(ReturnSamplesPage)).willReturn(Option(true))
        checkHelper.returnSamples.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for LegalChallengePage" in {
        given(userAnswers.get(LegalChallengePage)).willReturn(Option(false))
        checkHelper.legalChallenge.get.answer shouldBe "site.no"
      }

      "return a row with the correct answer for SimilarItemCommodityCodePage" in {
        given(userAnswers.get(SimilarItemCommodityCodePage)).willReturn(Option(true))
        checkHelper.similarItemCommodityCode.get.answer shouldBe "site.yes"
      }

      "return a row with the correct answer for PreviousBTIRulingPage" in {
        given(userAnswers.get(PreviousBTIRulingPage)).willReturn(Option(true))
        checkHelper.previousBTIRuling.get.answer shouldBe "site.yes"
      }
    }

    "rendering information pages" must {

      "return a row with the correct answer for CommodityCodeDigitsPage" in {
        given(userAnswers.get(CommodityCodeDigitsPage)).willReturn(Option("12131233241324"))
        checkHelper.commodityCodeDigits.get.answer shouldBe "12131233241324"
      }

      "return a row with the correct answer for LegalChallengeDetailsPage" in {
        given(userAnswers.get(LegalChallengeDetailsPage)).willReturn(Option("Legal challenge"))
        checkHelper.legalChallengeDetails.get.answer shouldBe "Legal challenge"
      }

      "return a row with the correct answer for CommodityCodeRulingReferencePage" in {
        given(userAnswers.get(any[CommodityCodeRulingReferencePage.type])(any[Reads[List[String]]])).willReturn(Option(List("code ruling")))
        checkHelper.commodityCodeRulingReference.get.answer shouldBe "code ruling"
      }

      "return a row with the correct answer for ProvideBTIReferencePage" in {
        given(userAnswers.get(ProvideBTIReferencePage)).willReturn(Option(BTIReference("122523847624")))
        checkHelper.provideBTIReference.get.answer shouldBe "122523847624"
      }

      "return a row with the correct answer for EnterContactDetailsPage Name" in {
        given(userAnswers.get(EnterContactDetailsPage)).willReturn(Option(EnterContactDetails("name", "email", "phoneNumber")))
        checkHelper.enterContactDetailsName.get.answer shouldBe "name"
      }

      "return a row with the correct answer for EnterContactDetailsPage Email" in {
        given(userAnswers.get(EnterContactDetailsPage)).willReturn(Option(EnterContactDetails("name", "email", "phoneNumber")))
        checkHelper.enterContactDetailsEmail.get.answer shouldBe "email"
      }

      "return a row with the correct answer for EnterContactDetailsPage Telephone" in {
        given(userAnswers.get(EnterContactDetailsPage)).willReturn(Option(EnterContactDetails("name", "email", "phoneNumber")))
        checkHelper.enterContactDetailsPhone.get.answer shouldBe "phoneNumber"
      }

      "return a row with the correct answer for RegisteredAddressForEoriPage when CDS check disabled" in {
        val requestWithoutEori = DataRequest(FakeRequest(), "", None, mock[UserAnswers])
        given(userAnswers.get(RegisteredAddressForEoriPage)).willReturn(Option(RegisteredAddressForEori("eori", "f1", "f2", "f3", Some("f4"), "IE")))
        checkHelper.registeredAddressForEori(requestWithoutEori).get.answer shouldBe "eori\nf1\nf2\nf3\nf4\nIreland"
      }

      "return a row with the correct answer for RegisteredAddressForEoriPage when CDS check enabled" in {
        val requestWithEori = DataRequest(FakeRequest(), "", Some("eori"), mock[UserAnswers])
        given(userAnswers.get(RegisteredAddressForEoriPage)).willReturn(Option(RegisteredAddressForEori("eori", "f1", "f2", "f3", Some("f4"), "IE")))
        checkHelper.registeredAddressForEori(requestWithEori).get.answer shouldBe "f1\nf2\nf3\nf4\nIreland"
      }

      "return a row with the correct answer for registered name" in {
        given(userAnswers.get(RegisteredAddressForEoriPage)).willReturn(Option(RegisteredAddressForEori("eori", "f1", "f2", "f3", Some("f4"), "IE")))
        checkHelper.registeredName.get.answer shouldBe "f1"
      }

      "return a row with the correct answer for registered address" in {
        given(userAnswers.get(RegisteredAddressForEoriPage)).willReturn(Option(RegisteredAddressForEori("eori", "f1", "f2", "f3", Some("f4"), "IE")))
        checkHelper.registeredAddress.get.answer shouldBe "f2\nf3\nf4\nIreland"
      }
    }
  }
}
