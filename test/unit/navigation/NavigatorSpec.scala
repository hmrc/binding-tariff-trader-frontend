/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.libs.json.Reads
import play.api.mvc.Call

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator(frontendAppConfig)

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {
        case object UnknownPage extends Page {
          def route(mode: Mode) = Call("GET", "/unknown")
        }
        navigator.nextPage(UnknownPage, NormalMode)(mock[UserAnswers]) shouldBe routes.IndexController.getApplications()
      }

      "Go to Before you start from index when required" in {

        navigator.nextPage(IndexPage, NormalMode)(mock[UserAnswers]) shouldBe routes.BeforeYouStartController
          .onPageLoad()

      }

      "go to ProvideGoodsDescriptionPage after ProvideGoodsName page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(ProvideGoodsNamePage)).thenReturn(Some("goods name"))

        navigator.nextPage(ProvideGoodsNamePage, NormalMode)(mockUserAnswers) shouldBe routes.ProvideGoodsDescriptionController
          .onPageLoad(NormalMode)
      }

      "go to AddConfidentialInformationPage after ProvideGoodsDescriptionPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(ProvideGoodsDescriptionPage)).thenReturn(Some("goods description"))

        navigator.nextPage(ProvideGoodsDescriptionPage, NormalMode)(mockUserAnswers) shouldBe routes.AddConfidentialInformationController
          .onPageLoad(NormalMode)
      }

      "go to AddSupportingDocumentsPage when no is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(false))

        navigator.nextPage(AddConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.AddSupportingDocumentsController.onPageLoad(NormalMode)
      }

      "go to ProvideConfidentialInformation when yes is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(true))

        navigator.nextPage(AddConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.ProvideConfidentialInformationController.onPageLoad(NormalMode)
      }

      "go to AddSupportingDocumentsPage after entering confidential info in ProvideConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(ProvideConfidentialInformationPage)).thenReturn(Some("confidential information"))

        navigator.nextPage(ProvideConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.AddSupportingDocumentsController.onPageLoad(NormalMode)
      }

      "redirect to UploadSupportingMaterialMultiplePage when user selects yes to add a file on AddSupportingDocumentsPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddSupportingDocumentsPage)).thenReturn(Some(true))

        navigator.nextPage(AddSupportingDocumentsPage, NormalMode)(mockUserAnswers) shouldBe
          routes.UploadSupportingMaterialMultipleController.onPageLoad(None, NormalMode)
      }

      "redirect to AreYouSendingSamples when user selects no to adding files on AddSupportingDocumentsPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddSupportingDocumentsPage)).thenReturn(Some(false))

        navigator.nextPage(AddSupportingDocumentsPage, NormalMode)(mockUserAnswers) shouldBe
          routes.AreYouSendingSamplesController.onPageLoad(NormalMode)
      }

      "redirect to UploadSupportingMaterialMultiplePage when user selects yes to add another file on SupportingMaterialFileListPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SupportingMaterialFileListPage)).thenReturn(Some(true))

        navigator.nextPage(SupportingMaterialFileListPage, NormalMode)(mockUserAnswers) shouldBe
          routes.UploadSupportingMaterialMultipleController.onPageLoad(None, NormalMode)
      }

      "go to MakeFileConfidentialPage after uploading a file on UploadSupportingMaterialMultiplePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(UploadSupportingMaterialMultiplePage))
          .thenReturn(Some(Seq(FileAttachment("id", "foo.jpg", "image/jpeg", 1L))))

        navigator.nextPage(UploadSupportingMaterialMultiplePage, NormalMode)(mockUserAnswers) shouldBe
          routes.MakeFileConfidentialController.onPageLoad(NormalMode)
      }

      "go to SupportingMaterialFileListPage after entering an answer on MakeFileConfidentialPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(MakeFileConfidentialPage)).thenReturn(Some(Map("id" -> true)))

        navigator.nextPage(MakeFileConfidentialPage, NormalMode)(mockUserAnswers) shouldBe
          routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
      }

      "redirect to AreYouSendingSamples page when user selects no to adding another file on SupportingMaterialFileListPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SupportingMaterialFileListPage)).thenReturn(Some(false))

        navigator.nextPage(SupportingMaterialFileListPage, NormalMode)(mockUserAnswers) shouldBe
          routes.AreYouSendingSamplesController.onPageLoad(NormalMode)
      }

      "redirect to IsSampleHazardous page when user selects YES from AreYouSendingSamplesPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AreYouSendingSamplesPage)).thenReturn(Some(true))

        navigator.nextPage(AreYouSendingSamplesPage, NormalMode)(mockUserAnswers) shouldBe
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

      "redirect to CommodityCodeBestMatchPage page when user selects NO from AreYouSendingSamples page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AreYouSendingSamplesPage)).thenReturn(Some(false))

        navigator.nextPage(AreYouSendingSamplesPage, NormalMode)(mockUserAnswers) shouldBe
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

      "redirect to PreviousBTIRulingPage when user selects NO from LegalChallengePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(LegalChallengePage)).thenReturn(Some(false))

        navigator.nextPage(LegalChallengePage, NormalMode)(mockUserAnswers) shouldBe
          routes.PreviousBTIRulingController.onPageLoad(NormalMode)
      }

      "redirect to PreviousBTIRulingPage page from LegalChallengeDetailsPage" in {
        val mockUserAnswers = mock[UserAnswers]

        navigator.nextPage(LegalChallengeDetailsPage, NormalMode)(mockUserAnswers) shouldBe
          routes.PreviousBTIRulingController.onPageLoad(NormalMode)
      }

      "redirect to ProvideBTIReferencePage when user selects YES from PreviousBTIRulingPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(PreviousBTIRulingPage)).thenReturn(Some(true))

        navigator.nextPage(PreviousBTIRulingPage, NormalMode)(mockUserAnswers) shouldBe
          routes.ProvideBTIReferenceController.onPageLoad(NormalMode)
      }

      "redirect to SimilarItemCommodityCodePage from ProvideBTIReferencePage" in {
        navigator.nextPage(ProvideBTIReferencePage, NormalMode)(mock[UserAnswers]) shouldBe
          routes.SimilarItemCommodityCodeController.onPageLoad(NormalMode)
      }

      "redirect to SimilarItemCommodityCodePage when user selects NO from PreviousBTIRulingPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(PreviousBTIRulingPage)).thenReturn(Some(false))

        navigator.nextPage(PreviousBTIRulingPage, NormalMode)(mockUserAnswers) shouldBe
          routes.SimilarItemCommodityCodeController.onPageLoad(NormalMode)
      }

      "redirect to CommodityCodeRulingReferencePage when user selects YES from SimilarItemCommodityCodePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SimilarItemCommodityCodePage)).thenReturn(Some(true))

        navigator.nextPage(SimilarItemCommodityCodePage, NormalMode)(mockUserAnswers) shouldBe
          routes.CommodityCodeRulingReferenceController.onPageLoad(NormalMode)
      }

      "redirect to AddAnotherRulingPage from CommodityCodeRulingReferencePage" in {
        navigator.nextPage(CommodityCodeRulingReferencePage, NormalMode)(mock[UserAnswers]) shouldBe
          routes.AddAnotherRulingController.onPageLoad(NormalMode)
      }

      "redirect to RegisteredAddressForEoriPage when user selects NO from SimilarItemCommodityCodePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SimilarItemCommodityCodePage)).thenReturn(Some(false))

        navigator.nextPage(SimilarItemCommodityCodePage, NormalMode)(mockUserAnswers) shouldBe
          routes.RegisteredAddressForEoriController.onPageLoad(NormalMode)
      }

      "redirect back to CommodityCodeRulingReferencePage when user selects yes to add another ruling on AddAnotherRulingPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddAnotherRulingPage)).thenReturn(Some(true))

        navigator.nextPage(AddAnotherRulingPage, NormalMode)(mockUserAnswers) shouldBe
          routes.CommodityCodeRulingReferenceController.onPageLoad(NormalMode)
      }

      "redirect to RegisteredAddressForEoriPage when user selects no to adding another ruling from AddAnotherRulingPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddAnotherRulingPage)).thenReturn(Some(false))

        navigator.nextPage(AddAnotherRulingPage, NormalMode)(mockUserAnswers) shouldBe
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

        when(mockUserAnswers.get(any[DataPage[String]])(any[Reads[String]])).thenReturn(None)

        navigator.nextPage(AddConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.AddConfidentialInformationController.onPageLoad(NormalMode)

        navigator.nextPage(AreYouSendingSamplesPage, NormalMode)(mockUserAnswers) shouldBe
          routes.AreYouSendingSamplesController.onPageLoad(NormalMode)

        navigator.nextPage(CommodityCodeBestMatchPage, NormalMode)(mockUserAnswers) shouldBe
          routes.CommodityCodeBestMatchController.onPageLoad(NormalMode)

        navigator.nextPage(LegalChallengePage, NormalMode)(mockUserAnswers) shouldBe
          routes.LegalChallengeController.onPageLoad(NormalMode)

        navigator.nextPage(PreviousBTIRulingPage, NormalMode)(mockUserAnswers) shouldBe
          routes.PreviousBTIRulingController.onPageLoad(NormalMode)

        navigator.nextPage(SimilarItemCommodityCodePage, NormalMode)(mockUserAnswers) shouldBe
          routes.SimilarItemCommodityCodeController.onPageLoad(NormalMode)
      }
    }

    "in Check mode" must {

      "go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {
        case object UnknownPage extends Page {
          def route(mode: Mode) = Call("GET", "/unknown")
        }
        navigator.nextPage(UnknownPage, CheckMode)(mock[UserAnswers]) shouldBe routes.CheckYourAnswersController
          .onPageLoad()
      }

      "go to ProvideConfidentialInformationPage when yes is selected in AddConfidentialInformationPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(true))

        navigator.nextPage(AddConfidentialInformationPage, CheckMode)(mockUserAnswers) shouldBe
          routes.ProvideConfidentialInformationController.onPageLoad(CheckMode)
      }

      "return to CheckYourAnswersPage when no is selected in AddConfidentialInformationPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(false))

        navigator.nextPage(AddConfidentialInformationPage, CheckMode)(mockUserAnswers) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }

      "redirect to UploadSupportingMaterialMultiplePage when user selects yes to add a file on AddSupportingDocumentsPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddSupportingDocumentsPage)).thenReturn(Some(true))

        navigator.nextPage(AddSupportingDocumentsPage, CheckMode)(mockUserAnswers) shouldBe
          routes.UploadSupportingMaterialMultipleController.onPageLoad(None, CheckMode)
      }

      "redirect to CheckYourAnswersPage when user selects no to adding files on AddSupportingDocumentsPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddSupportingDocumentsPage)).thenReturn(Some(false))

        navigator.nextPage(AddSupportingDocumentsPage, CheckMode)(mockUserAnswers) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }

      "redirect to UploadSupportingMaterialMultiplePage when user selects yes to add another file on SupportingMaterialFileListPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SupportingMaterialFileListPage)).thenReturn(Some(true))

        navigator.nextPage(SupportingMaterialFileListPage, CheckMode)(mockUserAnswers) shouldBe
          routes.UploadSupportingMaterialMultipleController.onPageLoad(None, CheckMode)
      }

      "go to MakeFileConfidentialPage after uploading a file on UploadSupportingMaterialMultiplePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(UploadSupportingMaterialMultiplePage))
          .thenReturn(Some(Seq(FileAttachment("id", "foo.jpg", "image/jpeg", 1L))))

        navigator.nextPage(UploadSupportingMaterialMultiplePage, CheckMode)(mockUserAnswers) shouldBe
          routes.MakeFileConfidentialController.onPageLoad(CheckMode)
      }

      "go to SupportingMaterialFileListPage after entering an answer on MakeFileConfidentialPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(MakeFileConfidentialPage)).thenReturn(Some(Map("id" -> true)))

        navigator.nextPage(MakeFileConfidentialPage, CheckMode)(mockUserAnswers) shouldBe
          routes.SupportingMaterialFileListController.onPageLoad(CheckMode)
      }

      "redirect to CheckYourAnswersPage when user selects no to adding another file on SupportingMaterialFileListPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SupportingMaterialFileListPage)).thenReturn(Some(false))

        navigator.nextPage(SupportingMaterialFileListPage, CheckMode)(mockUserAnswers) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }

      "go to IsSampleHazardousPage when yes is selected in AreYouSendingSamplesPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AreYouSendingSamplesPage)).thenReturn(Some(true))

        navigator.nextPage(AreYouSendingSamplesPage, CheckMode)(mockUserAnswers) shouldBe
          routes.IsSampleHazardousController.onPageLoad(CheckMode)
      }

      "go to ReturnSamplesPage when information is provided on IsSampleHazardousPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(IsSampleHazardousPage)).thenReturn(Some(true))

        navigator.nextPage(IsSampleHazardousPage, CheckMode)(mockUserAnswers) shouldBe
          routes.ReturnSamplesController.onPageLoad(CheckMode)

        reset(mockUserAnswers)

        when(mockUserAnswers.get(IsSampleHazardousPage)).thenReturn(Some(false))

        navigator.nextPage(IsSampleHazardousPage, CheckMode)(mockUserAnswers) shouldBe
          routes.ReturnSamplesController.onPageLoad(CheckMode)
      }

      "go back to CheckYourAnswersPage when no is selected in AreYouSendingSamplesPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AreYouSendingSamplesPage)).thenReturn(Some(false))

        navigator.nextPage(AreYouSendingSamplesPage, CheckMode)(mockUserAnswers) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }

      "go to CommodityCodeDigitsPage when yes is selected in CommodityCodeBestMatchPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(CommodityCodeBestMatchPage)).thenReturn(Some(true))

        navigator.nextPage(CommodityCodeBestMatchPage, CheckMode)(mockUserAnswers) shouldBe
          routes.CommodityCodeDigitsController.onPageLoad(CheckMode)
      }

      "go back to CheckYourAnswersPage when no is selected in CommodityCodeBestMatchPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(CommodityCodeBestMatchPage)).thenReturn(Some(false))

        navigator.nextPage(CommodityCodeBestMatchPage, CheckMode)(mockUserAnswers) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }

      "go to LegalChallengeDetailsPage when yes is selected in LegalChallengePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(LegalChallengePage)).thenReturn(Some(true))

        navigator.nextPage(LegalChallengePage, CheckMode)(mockUserAnswers) shouldBe
          routes.LegalChallengeDetailsController.onPageLoad(CheckMode)
      }

      "go back to CheckYourAnswersPage when no is selected in LegalChallengePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(LegalChallengePage)).thenReturn(Some(false))

        navigator.nextPage(LegalChallengePage, CheckMode)(mockUserAnswers) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }

      "go to ProvideBTIReferencePage when yes is selected in PreviousBTIRulingPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(PreviousBTIRulingPage)).thenReturn(Some(true))

        navigator.nextPage(PreviousBTIRulingPage, CheckMode)(mockUserAnswers) shouldBe
          routes.ProvideBTIReferenceController.onPageLoad(CheckMode)
      }

      "go back to CheckYourAnswersPage when no is selected in PreviousBTIRulingPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(PreviousBTIRulingPage)).thenReturn(Some(false))

        navigator.nextPage(PreviousBTIRulingPage, CheckMode)(mockUserAnswers) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }

      "go to CommodityCodeRulingReferencePage when yes is selected in SimilarItemCommodityCodePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SimilarItemCommodityCodePage)).thenReturn(Some(true))

        navigator.nextPage(SimilarItemCommodityCodePage, CheckMode)(mockUserAnswers) shouldBe
          routes.CommodityCodeRulingReferenceController.onPageLoad(CheckMode)
      }

      "go back to CheckYourAnswersPage when no is selected in SimilarItemCommodityCodePage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(SimilarItemCommodityCodePage)).thenReturn(Some(false))

        navigator.nextPage(SimilarItemCommodityCodePage, CheckMode)(mockUserAnswers) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }

      "go to AddAnotherRulingPage from CommodityCodeRulingReferencePage" in {
        navigator.nextPage(CommodityCodeRulingReferencePage, CheckMode)(mock[UserAnswers]) shouldBe
          routes.AddAnotherRulingController.onPageLoad(CheckMode)
      }

      "go to CommodityCodeRulingReferencePage when yes is selected to add another ruling on AddAnotherRulingPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddAnotherRulingPage)).thenReturn(Some(true))

        navigator.nextPage(AddAnotherRulingPage, CheckMode)(mockUserAnswers) shouldBe
          routes.CommodityCodeRulingReferenceController.onPageLoad(CheckMode)
      }

      "go back to CheckYourAnswersPage when no is selected to stop adding rulings on AddAnotherRulingPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddAnotherRulingPage)).thenReturn(Some(false))

        navigator.nextPage(AddAnotherRulingPage, CheckMode)(mockUserAnswers) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
