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
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import pages._
import play.api.mvc.Call

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {
        case object UnknownPage extends Page {
          def route(mode: Mode) = Call("GET", "/unknown")
        }
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

      "redirect to UploadSupportingMaterialMultiplePage when user selects YES to add a file on SupportingMaterialFileListPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SupportingMaterialFileListPage)).thenReturn(Some(FileListAnswers(Some(true), Nil)))

        navigator.nextPage(SupportingMaterialFileListPage, NormalMode)(mockUserAnswers) shouldBe
          routes.UploadSupportingMaterialMultipleController.onPageLoad(NormalMode)
      }

      "return to SupportingMaterialFileList page when file is removed" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SupportingMaterialFileListPage)).thenReturn(Some(FileListAnswers(None, Nil)))

        navigator.nextPage(SupportingMaterialFileListPage, NormalMode)(mockUserAnswers) shouldBe
          routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
      }

      "redirect to WhenToSendSample page when no further files are uploaded" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SupportingMaterialFileListPage)).thenReturn(Some(FileListAnswers(Some(false), Nil)))

        navigator.nextPage(SupportingMaterialFileListPage, NormalMode)(mockUserAnswers) shouldBe
          routes.WhenToSendSampleController.onPageLoad(NormalMode)
      }

      "redirect to IsSampleHazardous page when user selects YES from WhenToSendSample page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(WhenToSendSamplePage)).thenReturn(Some(true))

        navigator.nextPage(WhenToSendSamplePage, NormalMode)(mockUserAnswers) shouldBe
          routes.IsSampleHazardousController.onPageLoad(NormalMode)
      }

      "redirect to ReturnSamplesPage from IsSampleHazardousPage" in {
        navigator.nextPage(IsSampleHazardousPage, NormalMode)(mock[UserAnswers]) shouldBe
          routes.ReturnSamplesController.onPageLoad(NormalMode)
      }

      "redirect to CommodityCodeBestMatchPage from ReturnSamplesPage" in {
        navigator.nextPage(ReturnSamplesPage, NormalMode)(mock[UserAnswers]) shouldBe
          routes.CommodityCodeBestMatchController.onPageLoad(NormalMode)
      }

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

      "redirect to PreviousCommodityCodePage when user selects YES from SelectApplicationTypePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SelectApplicationTypePage)).thenReturn(Some(true))

        navigator.nextPage(SelectApplicationTypePage, NormalMode)(mockUserAnswers) shouldBe
          routes.PreviousCommodityCodeController.onPageLoad(NormalMode)
      }

      "redirect to SimilarItemCommodityCodePage from PreviousCommodityCodePage" in {
        navigator.nextPage(PreviousCommodityCodePage, NormalMode)(mock[UserAnswers]) shouldBe
          routes.SimilarItemCommodityCodeController.onPageLoad(NormalMode)
      }

      "redirect to SimilarItemCommodityCodePage when user selects NO from SelectApplicationTypePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SelectApplicationTypePage)).thenReturn(Some(false))

        navigator.nextPage(SelectApplicationTypePage, NormalMode)(mockUserAnswers) shouldBe
          routes.SimilarItemCommodityCodeController.onPageLoad(NormalMode)
      }

      "redirect to CommodityCodeRulingReferencePage when user selects YES from SimilarItemCommodityCodePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SimilarItemCommodityCodePage)).thenReturn(Some(true))

        navigator.nextPage(SimilarItemCommodityCodePage, NormalMode)(mockUserAnswers) shouldBe
          routes.CommodityCodeRulingReferenceController.onPageLoad(NormalMode)
      }

      "redirect to RegisteredAddressForEoriPage from CommodityCodeRulingReferencePage" in {
        navigator.nextPage(CommodityCodeRulingReferencePage, NormalMode)(mock[UserAnswers]) shouldBe
          routes.RegisteredAddressForEoriController.onPageLoad(NormalMode)
      }

      "redirect to RegisteredAddressForEoriPage when user selects NO from SimilarItemCommodityCodePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SimilarItemCommodityCodePage)).thenReturn(Some(false))

        navigator.nextPage(SimilarItemCommodityCodePage, NormalMode)(mockUserAnswers) shouldBe
          routes.RegisteredAddressForEoriController.onPageLoad(NormalMode)
      }

      "redirect to EnterContactDetailsPage from RegisteredAddressForEoriPage" in {
        navigator.nextPage(RegisteredAddressForEoriPage, NormalMode)(mock[UserAnswers]) shouldBe
          routes.EnterContactDetailsController.onPageLoad(NormalMode)
      }

      "redirect to CheckYourAnswersPage from EnterContactDetailsPage" in {
        navigator.nextPage(EnterContactDetailsPage, NormalMode)(mock[UserAnswers]) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }

      "redirect to ConfirmationPage from CheckYourAnswersPage" in {
        navigator.nextPage(CheckYourAnswersPage, NormalMode)(mock[UserAnswers]) shouldBe
          routes.ConfirmationController.onPageLoad()
      }

      "redirect to self if no answers were entered" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(any())(any())).thenReturn(None)

        navigator.nextPage(AddConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.AddConfidentialInformationController.onPageLoad(NormalMode)

        navigator.nextPage(WhenToSendSamplePage, NormalMode)(mockUserAnswers) shouldBe
          routes.WhenToSendSampleController.onPageLoad(NormalMode)

        navigator.nextPage(CommodityCodeBestMatchPage, NormalMode)(mockUserAnswers) shouldBe
          routes.CommodityCodeBestMatchController.onPageLoad(NormalMode)

        navigator.nextPage(LegalChallengePage, NormalMode)(mockUserAnswers) shouldBe
          routes.LegalChallengeController.onPageLoad(NormalMode)

        navigator.nextPage(SelectApplicationTypePage, NormalMode)(mockUserAnswers) shouldBe
          routes.SelectApplicationTypeController.onPageLoad(NormalMode)

        navigator.nextPage(SimilarItemCommodityCodePage, NormalMode)(mockUserAnswers) shouldBe
          routes.SimilarItemCommodityCodeController.onPageLoad(NormalMode)
      }
    }

    "in Check mode" must {

      "go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {
        case object UnknownPage extends Page {
          def route(mode: Mode) = Call("GET", "/unknown")
        }
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
