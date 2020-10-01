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
import pages._
import play.api.mvc.Call

@Singleton
class Navigator @Inject()() {

  def normalPage(
    page: Page,
    continuingTo: Page,
    mode: Mode
  ): Map[Page, UserAnswers => Call] =
    Map(page -> ((_: UserAnswers) => continuingTo.route(mode)))

  def sequentialJourney(
    pages: List[Page],
    continuingTo: Page,
    mode: Mode
  ): Map[Page, UserAnswers => Call] = pages match {
    case Nil =>
      Map.empty[Page, UserAnswers => Call]
    case _ =>
      val journey = pages.sliding(2).flatMap {
        case from :: to :: Nil =>
          normalPage(from, to, mode)
        case _ =>
          Map.empty[Page, UserAnswers => Call]
      }.toMap

      journey ++ normalPage(pages.last, continuingTo, mode)
  }

  def yesNoJourney(
    questionPage: QuestionPage[Boolean],
    detailPage: QuestionPage[_],
    continuingTo: Page,
    mode: Mode
  ): Map[Page, UserAnswers => Call] =
    yesNoJourney(questionPage, List(detailPage), continuingTo, mode)

  def yesNoJourney(
    questionPage: QuestionPage[Boolean],
    detailPages: List[QuestionPage[_]],
    continuingTo: Page,
    mode: Mode
  ): Map[Page, UserAnswers => Call] = detailPages match {
    case Nil =>
      normalPage(questionPage, continuingTo, mode)

    case detailPages =>
      val questionPageRoute = questionPage -> { (answer: UserAnswers) =>
        answer.get(questionPage) match {
          case Some(true) => detailPages.head.route(mode)
          case Some(false) => continuingTo.route(mode)
          case _ => questionPage.route(mode)
        }
      }

      val detailPageRoutes = sequentialJourney(detailPages, continuingTo, mode)

      detailPageRoutes + questionPageRoute
  }

  private val routeMap: Map[Page, UserAnswers => Call] = List(
    // Information you need to complete an application
    normalPage(IndexPage, BeforeYouStartPage, NormalMode),

    // About the goods
    normalPage(ProvideGoodsNamePage, ProvideGoodsDescriptionPage, NormalMode),
    normalPage(ProvideGoodsDescriptionPage, AddConfidentialInformationPage, NormalMode),

    // Do you want to add any confidential information about the goods?
    yesNoJourney(
      questionPage = AddConfidentialInformationPage,
      detailPage = ProvideConfidentialInformationPage,
      continuingTo = SupportingMaterialFileListPage,
      mode = NormalMode
    ),

    // Do you want to upload any supporting documents?
    Map(
      SupportingMaterialFileListPage -> { (answers: UserAnswers) =>
        answers.get(SupportingMaterialFileListPage).flatMap(_.addAnotherDecision) match {
          case Some(true) => routes.UploadSupportingMaterialMultipleController.onPageLoad(NormalMode)
          case Some(false) => routes.WhenToSendSampleController.onPageLoad(NormalMode)
          case _ => routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
        }
      },
      UploadSupportingMaterialMultiplePage -> ((_: UserAnswers) => routes.SupportingMaterialFileListController.onPageLoad(NormalMode)),
    ),

    // Will you send a sample of the goods to HMRC?
    yesNoJourney(
      questionPage = WhenToSendSamplePage,
      detailPages = List(IsSampleHazardousPage, ReturnSamplesPage),
      continuingTo = CommodityCodeBestMatchPage,
      mode = NormalMode
    ),

    // Have you found a commodity code for the goods?
    yesNoJourney(
      questionPage = CommodityCodeBestMatchPage,
      detailPage = CommodityCodeDigitsPage,
      continuingTo = LegalChallengePage,
      mode = NormalMode
    ),

    // Have there been any legal problems classifying the goods?
    yesNoJourney(
      questionPage = LegalChallengePage,
      detailPage = LegalChallengeDetailsPage,
      continuingTo = SelectApplicationTypePage,
      mode = NormalMode
    ),

    // Do you have a previous BTI ruling reference for the goods?
    yesNoJourney(
      questionPage = SelectApplicationTypePage,
      detailPage = PreviousCommodityCodePage,
      continuingTo = SimilarItemCommodityCodePage,
      mode = NormalMode
    ),

    // Do you know of a similar item that already has an Advance Tariff Ruling?
    yesNoJourney(
      questionPage = SimilarItemCommodityCodePage,
      detailPage = CommodityCodeRulingReferencePage,
      continuingTo = RegisteredAddressForEoriPage,
      mode = NormalMode
    ),

    // About the applicant
    normalPage(RegisteredAddressForEoriPage, EnterContactDetailsPage, NormalMode),

    // Provide the contact details for this application
    normalPage(EnterContactDetailsPage, CheckYourAnswersPage, NormalMode),

    // Check your answers
    normalPage(CheckYourAnswersPage, DeclarationPage, NormalMode),

    // Your declaration
    normalPage(DeclarationPage, ConfirmationPage, NormalMode)

  ).foldLeft(Map.empty[Page, UserAnswers => Call])(_ ++ _)

  private val checkRouteMap: Map[Page, UserAnswers => Call] = List(
    // Do you want to add any confidential information about the goods?
    yesNoJourney(
      questionPage = AddConfidentialInformationPage,
      detailPage = ProvideConfidentialInformationPage,
      continuingTo = CheckYourAnswersPage,
      mode = CheckMode
    ),

    // Do you want to upload any supporting documents?
    Map(
      SupportingMaterialFileListPage -> { (answers: UserAnswers) =>
        answers.get(SupportingMaterialFileListPage).flatMap(_.addAnotherDecision) match {
          case Some(true) => routes.UploadSupportingMaterialMultipleController.onPageLoad(CheckMode)
          case Some(false) => routes.CheckYourAnswersController.onPageLoad()
          case _ => routes.SupportingMaterialFileListController.onPageLoad(CheckMode)
        }
      },
      UploadSupportingMaterialMultiplePage -> ((_: UserAnswers) => routes.SupportingMaterialFileListController.onPageLoad(CheckMode)),
    ),

    // Will you send a sample of the goods to HMRC?
    yesNoJourney(
      questionPage = WhenToSendSamplePage,
      detailPages = List(IsSampleHazardousPage, ReturnSamplesPage),
      continuingTo = CheckYourAnswersPage,
      mode = CheckMode
    ),

    // Have you found a commodity code for the goods?
    yesNoJourney(
      questionPage = CommodityCodeBestMatchPage,
      detailPage = CommodityCodeDigitsPage,
      continuingTo = CheckYourAnswersPage,
      mode = CheckMode
    ),

    // Have there been any legal problems classifying the goods?
    yesNoJourney(
      questionPage = LegalChallengePage,
      detailPage = LegalChallengeDetailsPage,
      continuingTo = CheckYourAnswersPage,
      mode = CheckMode
    ),

    // Do you have a previous BTI ruling reference for the goods?
    yesNoJourney(
      questionPage = SelectApplicationTypePage,
      detailPage = PreviousCommodityCodePage,
      continuingTo = CheckYourAnswersPage,
      mode = CheckMode
    ),

    // Do you know of a similar item that already has an Advance Tariff Ruling?
    yesNoJourney(
      questionPage = SimilarItemCommodityCodePage,
      detailPage = CommodityCodeRulingReferencePage,
      continuingTo = CheckYourAnswersPage,
      mode = CheckMode
    ),

  ).foldLeft(Map.empty[Page, UserAnswers => Call])(_ ++ _)

  def nextPage(page: Page, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode => routeMap.getOrElse(page, _ => routes.IndexController.getApplications())
    case CheckMode => checkRouteMap.getOrElse(page, _ => routes.CheckYourAnswersController.onPageLoad())
  }
}
