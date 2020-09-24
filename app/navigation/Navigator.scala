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
    IndexPage -> (_ => routes.BeforeYouStartController.onPageLoad()),
    ProvideGoodsNamePage -> (_ => routes.ProvideGoodsDescriptionController.onPageLoad(NormalMode)),
    ProvideGoodsDescriptionPage -> (_ => routes.AddConfidentialInformationController.onPageLoad(NormalMode)),
    AddConfidentialInformationPage -> (answer => answer.get(AddConfidentialInformationPage) match {
      case Some(true) => routes.ProvideConfidentialInformationController.onPageLoad(NormalMode)
      case _ => routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
    }),
    ProvideConfidentialInformationPage -> (_ => routes.SupportingMaterialFileListController.onPageLoad(NormalMode)),
    SupportingMaterialFileListPage -> (_ => routes.SupportingMaterialFileListController.onPageLoad(NormalMode)),
    RegisteredAddressForEoriPage -> (_ => routes.ProvideGoodsNameController.onPageLoad(NormalMode)),
    WhichBestDescribesYouPage -> (_ => routes.WhichBestDescribesYouController.onPageLoad(NormalMode)),
    SelectApplicationTypePage -> (_ => routes.SelectApplicationTypeController.onPageLoad(NormalMode)),
    RegisterBusinessRepresentingPage -> (_ => routes.RegisterBusinessRepresentingController.onPageLoad(NormalMode)),
    UploadWrittenAuthorisationPage -> (_ => routes.UploadWrittenAuthorisationController.onPageLoad(NormalMode)),
    AcceptItemInformationPage -> (_ => routes.AcceptItemInformationListController.onPageLoad()),
    PreviousCommodityCodePage -> (_ => routes.PreviousCommodityCodeController.onPageLoad(NormalMode)),
    UploadSupportingMaterialMultiplePage -> (_ => routes.UploadSupportingMaterialMultipleController.onPageLoad(NormalMode)),
    CommodityCodeBestMatchPage -> (_ => routes.CommodityCodeBestMatchController.onPageLoad(NormalMode)),
    CommodityCodeDigitsPage -> (_ => routes.CommodityCodeDigitsController.onPageLoad(NormalMode)),
    WhenToSendSamplePage -> (_ => routes.WhenToSendSampleController.onPageLoad(NormalMode)),
    ReturnSamplesPage -> (_ => routes.ReturnSamplesController.onPageLoad(NormalMode)),
    SimilarItemCommodityCodePage -> (_ => routes.SimilarItemCommodityCodeController.onPageLoad(NormalMode)),
    CommodityCodeRulingReferencePage -> (_ => routes.CommodityCodeRulingReferenceController.onPageLoad(NormalMode)),
    LegalChallengePage -> (_ => routes.LegalChallengeController.onPageLoad(NormalMode)),
    LegalChallengeDetailsPage -> (_ => routes.LegalChallengeDetailsController.onPageLoad(NormalMode)),
    SupportingInformationPage -> (_ => routes.EnterContactDetailsController.onPageLoad(NormalMode)),
    SupportingInformationDetailsPage -> (_ => routes.EnterContactDetailsController.onPageLoad(NormalMode)),
    EnterContactDetailsPage -> (_ => routes.CheckYourAnswersController.onPageLoad()),
    CheckYourAnswersPage -> (_ => routes.CheckYourAnswersController.onPageLoad()),
    DeclarationPage -> (_ => routes.DeclarationController.onPageLoad(NormalMode)),
    ConfirmationPage -> (_ => routes.ConfirmationController.onPageLoad())
  )

  private val checkRouteMap: Map[Page, UserAnswers => Call] = Map(
    AddConfidentialInformationPage -> (answer => answer.get(AddConfidentialInformationPage) match {
      case Some(true) => routes.ProvideConfidentialInformationController.onPageLoad(CheckMode)
      case _ => routes.CheckYourAnswersController.onPageLoad()
    }),
    LegalChallengeDetailsPage -> (_ => routes.LegalChallengeDetailsController.onPageLoad(CheckMode)),
    CommodityCodeRulingReferencePage -> (_ => routes.CommodityCodeRulingReferenceController.onPageLoad(CheckMode)),
    CommodityCodeDigitsPage -> (_ => routes.CommodityCodeDigitsController.onPageLoad(CheckMode)),
    ReturnSamplesPage -> (_ => routes.ReturnSamplesController.onPageLoad(CheckMode)),
    SupportingInformationDetailsPage -> (_ => routes.SupportingInformationDetailsController.onPageLoad(CheckMode)),
    PreviousCommodityCodePage -> (_ => routes.PreviousCommodityCodeController.onPageLoad(CheckMode)),
    RegisterBusinessRepresentingPage -> (_ => routes.RegisterBusinessRepresentingController.onPageLoad(CheckMode)),
    UploadWrittenAuthorisationPage -> (_ => routes.UploadWrittenAuthorisationController.onPageLoad(CheckMode))
  )

  def nextPage(page: Page, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode => routeMap.getOrElse(page, _ => routes.IndexController.getApplications())
    case CheckMode => checkRouteMap.getOrElse(page, _ => routes.CheckYourAnswersController.onPageLoad())
  }

}
