/*
 * Copyright 2025 HM Revenue & Customs
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

package service

import models.UserAnswers
import pages.*

class UserAnswerDeletionService {

  val allPages: Seq[Page] =
    Seq(
      AcceptItemInformationPage,
      AddAnotherRulingPage,
      AddConfidentialInformationPage,
      AddSupportingDocumentsPage,
      AreYouSendingSamplesPage,
      BeforeYouStartPage,
      CheckYourAnswersPage,
      CommodityCodeBestMatchPage,
      CommodityCodeDigitsPage,
      CommodityCodeRulingReferencePage,
      ConfirmationPage,
      EnterContactDetailsPage,
      IndexPage,
      IsSampleHazardousPage,
      LegalChallengeDetailsPage,
      LegalChallengePage,
      MakeFileConfidentialPage,
      PdfViewPage,
      PreviousBTIRulingPage,
      ProvideBTIReferencePage,
      ProvideConfidentialInformationPage,
      ProvideGoodsDescriptionPage,
      ProvideGoodsNamePage,
      RegisteredAddressForEoriPage,
      ReturnSamplesPage,
      SimilarItemCommodityCodePage,
      SupportingMaterialFileListPage,
      UploadSupportingMaterialMultiplePage
    )

  def deleteAllUserAnswersExcept(userAnswers: UserAnswers, excludedPages: Seq[DataPage[?]]): UserAnswers =
    allPages
      .diff(excludedPages)
      .foldLeft(userAnswers)((userAnswers, pageToRemove) => userAnswers.remove(pageToRemove))

}
