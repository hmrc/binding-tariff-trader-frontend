/*
 * Copyright 2018 HM Revenue & Customs
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

package generators

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryLegalChallengePage: Arbitrary[LegalChallengePage.type] =
    Arbitrary(LegalChallengePage)

  implicit lazy val arbitraryCommodityCodeRulingReferencePage: Arbitrary[CommodityCodeRulingReferencePage.type] =
    Arbitrary(CommodityCodeRulingReferencePage)

  implicit lazy val arbitrarySimilarItemCommodityCodePage: Arbitrary[SimilarItemCommodityCodePage.type] =
    Arbitrary(SimilarItemCommodityCodePage)

  implicit lazy val arbitraryReturnSamplesPage: Arbitrary[ReturnSamplesPage.type] =
    Arbitrary(ReturnSamplesPage)

  implicit lazy val arbitraryWhenToSendSamplePage: Arbitrary[WhenToSendSamplePage.type] =
    Arbitrary(WhenToSendSamplePage)

  implicit lazy val arbitraryCommodityCodeDigitsPage: Arbitrary[CommodityCodeDigitsPage.type] =
    Arbitrary(CommodityCodeDigitsPage)

  implicit lazy val arbitraryCommodityCodeBestMatchPage: Arbitrary[CommodityCodeBestMatchPage.type] =
    Arbitrary(CommodityCodeBestMatchPage)

  implicit lazy val arbitraryUploadSupportingMaterialMultiplePage: Arbitrary[UploadSupportingMaterialMultiplePage.type] =
    Arbitrary(UploadSupportingMaterialMultiplePage)

  implicit lazy val arbitraryConfidentialInformationPage: Arbitrary[ConfidentialInformationPage.type] =
    Arbitrary(ConfidentialInformationPage)

  implicit lazy val arbitraryDescribeYourItemPage: Arbitrary[DescribeYourItemPage.type] =
    Arbitrary(DescribeYourItemPage)

  implicit lazy val arbitraryPreviousCommodityCodePage: Arbitrary[PreviousCommodityCodePage.type] =
    Arbitrary(PreviousCommodityCodePage)

  implicit lazy val arbitraryInformationAboutYourItemPage: Arbitrary[InformationAboutYourItemPage.type] =
    Arbitrary(InformationAboutYourItemPage)

  implicit lazy val arbitraryEnterContactDetailsPage: Arbitrary[EnterContactDetailsPage.type] =
    Arbitrary(EnterContactDetailsPage)

  implicit lazy val arbitraryRegisterBusinessRepresentingPage: Arbitrary[RegisterBusinessRepresentingPage.type] =
    Arbitrary(RegisterBusinessRepresentingPage)

  implicit lazy val arbitrarySelectApplicationTypePage: Arbitrary[SelectApplicationTypePage.type] =
    Arbitrary(SelectApplicationTypePage)

  implicit lazy val arbitraryWhichBestDescribesYouPage: Arbitrary[WhichBestDescribesYouPage.type] =
    Arbitrary(WhichBestDescribesYouPage)

  implicit lazy val arbitraryRegisteredAddressForEoriPage: Arbitrary[RegisteredAddressForEoriPage.type] =
    Arbitrary(RegisteredAddressForEoriPage)

}
