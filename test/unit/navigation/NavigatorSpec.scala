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

package navigation

import base.SpecBase
import controllers.routes
import models._
import org.mockito.Mockito._
import pages._
import utils.JsonFormatters._

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode)(mock[UserAnswers]) shouldBe routes.IndexController.getApplications()
      }

      "Go to Before you start from index when required" in {

        navigator.nextPage(IndexPage, NormalMode)(mock[UserAnswers]) shouldBe routes.BeforeYouStartController.onPageLoad()

      }


      "go to ProvideGoodsDescriptionPage after ProvideGoodsName page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(ProvideGoodsNamePage)).thenReturn(Some("goods name"))

        navigator.nextPage(ProvideGoodsNamePage, NormalMode)(mockUserAnswers) shouldBe routes.ProvideGoodsDescriptionController.onPageLoad(NormalMode)
      }

      "go to AddConfidentialInformationPage after ProvideGoodsDescriptionPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(ProvideGoodsDescriptionPage)).thenReturn(Some("goods description"))

        navigator.nextPage(ProvideGoodsDescriptionPage, NormalMode)(mockUserAnswers) shouldBe routes.AddConfidentialInformationController.onPageLoad(NormalMode)
      }


      "go to SupportingMaterialFileListController when no is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(false))

        navigator.nextPage(AddConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
      }

      "go to ProvideConfidentialInformation when yes is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(true))

        navigator.nextPage(AddConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.ProvideConfidentialInformationController.onPageLoad(NormalMode)
      }

      "go to SupportingMaterialFileListPage after entering confidential info in ProvideConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(ProvideConfidentialInformationPage)).thenReturn(Some("confidential information"))

        navigator.nextPage(ProvideConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
      }

      "return to SupportingMaterialFileList page when file is removed" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SupportingMaterialFileListPage)).thenReturn(Some(FileListAnswers(None, Nil)))

        navigator.nextPage(SupportingMaterialFileListPage, NormalMode)(mockUserAnswers) shouldBe
          routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
      }

      "return to SupportingMaterialFileList page when add another file is selected" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SupportingMaterialFileListPage)).thenReturn(Some(FileListAnswers(Some(true), Nil)))

        navigator.nextPage(UploadSupportingMaterialMultiplePage, NormalMode)(mockUserAnswers) shouldBe
          routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
      }

      "redirect to WhenToSendSample page when no further files are uploaded" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SupportingMaterialFileListPage)).thenReturn(Some(FileListAnswers(Some(false), Nil)))

        navigator.nextPage(SupportingMaterialFileListPage, NormalMode)(mockUserAnswers) shouldBe
          routes.WhenToSendSampleController.onPageLoad(NormalMode)
      }

      //TODO should go to Hazardous page when user selects true
/*      "redirect to IsSampleHazardous page when user selects YES from WhenToSendSample page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SupportingMaterialFileListPage)).thenReturn(Some(FileListAnswers(Some(false), Nil)))

        navigator.nextPage(SupportingMaterialFileListPage, NormalMode)(mockUserAnswers) shouldBe
          routes.WhenToSendSampleController.onPageLoad(NormalMode)
      }*/

      "redirect to CommodityCodeBestMatchPage page when user selects NO from WhenToSendSample page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(WhenToSendSamplePage)).thenReturn(Some(false))

        navigator.nextPage(WhenToSendSamplePage, NormalMode)(mockUserAnswers) shouldBe
          routes.CommodityCodeBestMatchController.onPageLoad(NormalMode)
      }

      "redirect to CommodityCodeDigitsPage page when user selects YES from CommodityCodeBestMatchPage page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(CommodityCodeBestMatchPage)).thenReturn(Some(true))

        navigator.nextPage(CommodityCodeBestMatchPage, NormalMode)(mockUserAnswers) shouldBe
          routes.CommodityCodeDigitsController.onPageLoad(NormalMode)
      }

      "redirect to LegalChallengePage page when user selects NO from CommodityCodeBestMatchPage page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(CommodityCodeBestMatchPage)).thenReturn(Some(false))

        navigator.nextPage(CommodityCodeBestMatchPage, NormalMode)(mockUserAnswers) shouldBe
          routes.LegalChallengeController.onPageLoad(NormalMode)
      }

      "redirect to LegalChallengePage page from CommodityCodeDigitsPage page" in {
        val mockUserAnswers = mock[UserAnswers]

        navigator.nextPage(CommodityCodeDigitsPage, NormalMode)(mockUserAnswers) shouldBe
          routes.LegalChallengeController.onPageLoad(NormalMode)
      }

      "redirect to LegalChallengeDetailsPage page when user selects YES from LegalChallengePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(LegalChallengePage)).thenReturn(Some(true))

        navigator.nextPage(LegalChallengePage, NormalMode)(mockUserAnswers) shouldBe
          routes.LegalChallengeDetailsController.onPageLoad(NormalMode)
      }

      "redirect to SelectApplicationTypePage when user selects NO from LegalChallengePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(LegalChallengePage)).thenReturn(Some(false))

        navigator.nextPage(LegalChallengePage, NormalMode)(mockUserAnswers) shouldBe
          routes.SelectApplicationTypeController.onPageLoad(NormalMode)
      }

      "redirect to SelectApplicationTypePage page from LegalChallengeDetailsPage" in {
        val mockUserAnswers = mock[UserAnswers]

        navigator.nextPage(LegalChallengeDetailsPage, NormalMode)(mockUserAnswers) shouldBe
          routes.SelectApplicationTypeController.onPageLoad(NormalMode)
      }
    }

    "in Check mode" must {

      "go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode)(mock[UserAnswers]) shouldBe routes.CheckYourAnswersController.onPageLoad()
      }

      "go to next page (ProvideConfidentialInformation) when yes is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(true))

        navigator.nextPage(AddConfidentialInformationPage, CheckMode)(mockUserAnswers) shouldBe
          routes.ProvideConfidentialInformationController.onPageLoad(CheckMode)
      }

      "return to CheckYourAnswers when no is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(false))

        navigator.nextPage(AddConfidentialInformationPage, CheckMode)(mockUserAnswers) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
