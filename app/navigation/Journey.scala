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

package navigation

import pages.*

sealed abstract class Journey extends Product with Serializable {
  def questionPage: QuestionPage[Boolean]
  def detailPages: List[QuestionPage[?]]
}

case class YesNoJourney(questionPage: QuestionPage[Boolean], detailPages: List[QuestionPage[?]]) extends Journey

case class LoopingJourney(
  questionPage: QuestionPage[Boolean],
  detailPages: List[QuestionPage[?]],
  continuePage: QuestionPage[Boolean]
) extends Journey

object Journey {
  val confidentialInformation: YesNoJourney =
    YesNoJourney(AddConfidentialInformationPage, List(ProvideConfidentialInformationPage))

  val samples: YesNoJourney =
    YesNoJourney(AreYouSendingSamplesPage, List(IsSampleHazardousPage, ReturnSamplesPage))

  val supportingDocuments: LoopingJourney =
    LoopingJourney(
      AddSupportingDocumentsPage,
      List(UploadSupportingMaterialMultiplePage, MakeFileConfidentialPage),
      SupportingMaterialFileListPage
    )

  val commodityCode: YesNoJourney =
    YesNoJourney(CommodityCodeBestMatchPage, List(CommodityCodeDigitsPage))

  val legalProblems: YesNoJourney =
    YesNoJourney(LegalChallengePage, List(LegalChallengeDetailsPage))

  val previousBTI: YesNoJourney =
    YesNoJourney(PreviousBTIRulingPage, List(ProvideBTIReferencePage))

  val similarItem: LoopingJourney =
    LoopingJourney(SimilarItemCommodityCodePage, List(CommodityCodeRulingReferencePage), AddAnotherRulingPage)
}
