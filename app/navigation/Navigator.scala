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

import controllers.routes
import javax.inject.{Inject, Singleton}
import models._
import pages.{AddConfidentialInformationPage, CommodityCodeRulingReferencePage, UploadWrittenAuthorisationPage, _}
import play.api.mvc.Call

@Singleton
class Navigator @Inject()() {

  private val routeMap: Map[Page, UserAnswers => Call] = Map(
    // Information you need to complete an application
    IndexPage -> (_ => routes.BeforeYouStartController.onPageLoad()),

    // Removed from the journey for the time being
    // WhichBestDescribesYouPage -> (_ => routes.WhichBestDescribesYouController.onPageLoad(NormalMode)),
    // RegisterBusinessRepresentingPage -> (_ => routes.RegisterBusinessRepresentingController.onPageLoad(NormalMode)),
    // UploadWrittenAuthorisationPage -> (_ => routes.UploadWrittenAuthorisationController.onPageLoad(NormalMode)),

    // About the goods
    ProvideGoodsNamePage -> (_ => routes.ProvideGoodsDescriptionController.onPageLoad(NormalMode)),
    ProvideGoodsDescriptionPage -> (_ => routes.AddConfidentialInformationController.onPageLoad(NormalMode)),

    // Do you want to add any confidential information about the goods?
    AddConfidentialInformationPage -> (answer => answer.get(AddConfidentialInformationPage) match {
      case Some(true) => routes.ProvideConfidentialInformationController.onPageLoad(NormalMode)
      case Some(false) => routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
      case _ => routes.AddConfidentialInformationController.onPageLoad(NormalMode)
    }),
    ProvideConfidentialInformationPage -> (_ => routes.SupportingMaterialFileListController.onPageLoad(NormalMode)),

    // Do you want to upload any supporting documents?
    SupportingMaterialFileListPage -> (answers => {
      answers.get(SupportingMaterialFileListPage).flatMap(_.addAnotherDecision) match {
        case Some(true) => routes.UploadSupportingMaterialMultipleController.onPageLoad(NormalMode)
        case Some(false) => routes.WhenToSendSampleController.onPageLoad(NormalMode)
        case _ => routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
      }
    }),
    UploadSupportingMaterialMultiplePage -> (_ => routes.SupportingMaterialFileListController.onPageLoad(NormalMode)),

    // Will you send a sample of the goods to HMRC?
    WhenToSendSamplePage -> (answers => answers.get(WhenToSendSamplePage) match {
      case Some(true) => routes.IsSampleHazardousController.onPageLoad(NormalMode)
      case Some(false) => routes.CommodityCodeBestMatchController.onPageLoad(NormalMode)
      case _ => routes.WhenToSendSampleController.onPageLoad(NormalMode)
    }),
    IsSampleHazardousPage -> (_ => routes.ReturnSamplesController.onPageLoad(NormalMode)),
    ReturnSamplesPage -> (_ => routes.CommodityCodeBestMatchController.onPageLoad(NormalMode)),

    // Have you found a commodity code for the goods?
    CommodityCodeBestMatchPage -> (answer => answer.get(CommodityCodeBestMatchPage) match {
      case Some(true) => routes.CommodityCodeDigitsController.onPageLoad(NormalMode)
      case Some(false) => routes.LegalChallengeController.onPageLoad(NormalMode)
      case _ => routes.CommodityCodeBestMatchController.onPageLoad(NormalMode)
    }),
    CommodityCodeDigitsPage -> (_ => routes.LegalChallengeController.onPageLoad(NormalMode)),

    // Have there been any legal problems classifying the goods?
    LegalChallengePage -> (answer => answer.get(LegalChallengePage) match {
      case Some(true) => routes.LegalChallengeDetailsController.onPageLoad(NormalMode)
      case Some(false) => routes.SelectApplicationTypeController.onPageLoad(NormalMode)
      case _ => routes.LegalChallengeController.onPageLoad(NormalMode)
    }),
    LegalChallengeDetailsPage -> (_ => routes.SelectApplicationTypeController.onPageLoad(NormalMode)),

    // Do you have a previous BTI ruling reference for the goods?
    SelectApplicationTypePage -> (answer => answer.get(SelectApplicationTypePage) match {
      case Some(true) => routes.PreviousCommodityCodeController.onPageLoad(NormalMode)
      case Some(false) => routes.SimilarItemCommodityCodeController.onPageLoad(NormalMode)
      case _ => routes.SelectApplicationTypeController.onPageLoad(NormalMode)
    }),
    PreviousCommodityCodePage -> (_ => routes.SimilarItemCommodityCodeController.onPageLoad(NormalMode)),

    // Do you know of a similar item that already has an Advance Tariff Ruling?
    SimilarItemCommodityCodePage -> (answer => answer.get(SimilarItemCommodityCodePage) match {
      case Some(true) => routes.CommodityCodeRulingReferenceController.onPageLoad(NormalMode)
      case Some(false) => routes.EnterContactDetailsController.onPageLoad(NormalMode)
      case _ => routes.SimilarItemCommodityCodeController.onPageLoad(NormalMode)
    }),
    CommodityCodeRulingReferencePage -> (_ => routes.RegisteredAddressForEoriController.onPageLoad(NormalMode)),

    // About the applicant
    RegisteredAddressForEoriPage -> (_ => routes.EnterContactDetailsController.onPageLoad(NormalMode)),

    // Provide the contact details for this application
    EnterContactDetailsPage -> (_ => routes.CheckYourAnswersController.onPageLoad()),

    // Check your answers
    CheckYourAnswersPage -> (_ => routes.DeclarationController.onPageLoad(NormalMode)),

    // Your declaration
    DeclarationPage -> (_ => routes.ConfirmationController.onPageLoad())
  )

  private val checkRouteMap: Map[Page, UserAnswers => Call] = Map(
    // Do you want to add any confidential information about the goods?
    AddConfidentialInformationPage -> (answer => answer.get(AddConfidentialInformationPage) match {
      case Some(true) => routes.ProvideConfidentialInformationController.onPageLoad(CheckMode)
      case Some(false) => routes.CheckYourAnswersController.onPageLoad()
      case _ => routes.AddConfidentialInformationController.onPageLoad(CheckMode)
    }),

    // Do you want to upload any supporting documents?
    SupportingMaterialFileListPage -> (answers => {
      answers.get(SupportingMaterialFileListPage).flatMap(_.addAnotherDecision) match {
        case Some(true) => routes.UploadSupportingMaterialMultipleController.onPageLoad(CheckMode)
        case Some(false) => routes.CheckYourAnswersController.onPageLoad()
        case _ => routes.SupportingMaterialFileListController.onPageLoad(CheckMode)
      }
    }),

    // Will you send a sample of the goods to HMRC?
    WhenToSendSamplePage -> (answers => answers.get(WhenToSendSamplePage) match {
      case Some(true) => routes.IsSampleHazardousController.onPageLoad(CheckMode)
      case Some(false) => routes.CheckYourAnswersController.onPageLoad()
      case _ => routes.WhenToSendSampleController.onPageLoad(CheckMode)
    }),
    IsSampleHazardousPage -> (_ => routes.ReturnSamplesController.onPageLoad(CheckMode)),

    // Have you found a commodity code for the goods?
    CommodityCodeBestMatchPage -> (answer => answer.get(CommodityCodeBestMatchPage) match {
      case Some(true) => routes.CommodityCodeDigitsController.onPageLoad(CheckMode)
      case Some(false) => routes.CheckYourAnswersController.onPageLoad()
      case _ => routes.CommodityCodeBestMatchController.onPageLoad(CheckMode)
    }),

    // Have there been any legal problems classifying the goods?
    LegalChallengePage -> (answer => answer.get(LegalChallengePage) match {
      case Some(true) => routes.LegalChallengeDetailsController.onPageLoad(CheckMode)
      case Some(false) => routes.CheckYourAnswersController.onPageLoad()
      case _ => routes.LegalChallengeController.onPageLoad(CheckMode)
    }),

    // Do you have a previous BTI ruling reference for the goods?
    SelectApplicationTypePage -> (answer => answer.get(SelectApplicationTypePage) match {
      case Some(true) => routes.PreviousCommodityCodeController.onPageLoad(CheckMode)
      case Some(false) => routes.CheckYourAnswersController.onPageLoad()
      case _ => routes.SelectApplicationTypeController.onPageLoad(CheckMode)
    }),

    // Do you know of a similar item that already has an Advance Tariff Ruling?
    SimilarItemCommodityCodePage -> (answer => answer.get(SimilarItemCommodityCodePage) match {
      case Some(true) => routes.CommodityCodeRulingReferenceController.onPageLoad(CheckMode)
      case Some(false) => routes.CheckYourAnswersController.onPageLoad()
      case _ => routes.SimilarItemCommodityCodeController.onPageLoad(CheckMode)
    })
  )

  def nextPage(page: Page, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode => routeMap.getOrElse(page, _ => routes.IndexController.getApplications())
    case CheckMode => checkRouteMap.getOrElse(page, _ => routes.CheckYourAnswersController.onPageLoad())
  }
}
