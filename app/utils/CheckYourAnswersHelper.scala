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

package utils

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages._
import viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def askForUploadSupportingMaterial: Option[AnswerRow] = userAnswers.get(AskForUploadSupportingMaterialPage) map {
    x => AnswerRow("askForUploadSupportingMaterial.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.AskForUploadSupportingMaterialController.onPageLoad(CheckMode).url)
  }

  def uploadWrittenAuthorisation: Option[AnswerRow] = userAnswers.get(UploadWrittenAuthorisationPage) map {
    x => AnswerRow("uploadWrittenAuthorisation.checkYourAnswersLabel", x.name , false, routes.UploadWrittenAuthorisationController.onPageLoad(CheckMode).url)
  }

  def supportingInformationDetails: Option[AnswerRow] = userAnswers.get(SupportingInformationDetailsPage) map {
    x => AnswerRow("supportingInformationDetails.checkYourAnswersLabel", s"$x", false, routes.SupportingInformationDetailsController.onPageLoad(CheckMode).url)
  }

  def supportingInformation: Option[AnswerRow] = userAnswers.get(SupportingInformationPage) map {
    x => AnswerRow("supportingInformation.checkYourAnswersLabel", s"supportingInformation.$x", true, routes.SupportingInformationController.onPageLoad(CheckMode).url)
  }

  def legalChallengeDetails: Option[AnswerRow] = userAnswers.get(LegalChallengeDetailsPage) map {
    x => AnswerRow("legalChallengeDetails.checkYourAnswersLabel", s"$x", false, routes.LegalChallengeDetailsController.onPageLoad(CheckMode).url)
  }

  def legalChallenge: Option[AnswerRow] = userAnswers.get(LegalChallengePage) map {
    x => AnswerRow("legalChallenge.checkYourAnswersLabel", s"legalChallenge.$x", true, routes.LegalChallengeController.onPageLoad(CheckMode).url)
  }

  def commodityCodeRulingReference: Option[AnswerRow] = userAnswers.get(CommodityCodeRulingReferencePage) map {
    x => AnswerRow("commodityCodeRulingReference.checkYourAnswersLabel", s"$x", false, routes.CommodityCodeRulingReferenceController.onPageLoad(CheckMode).url)
  }

  def similarItemCommodityCode: Option[AnswerRow] = userAnswers.get(SimilarItemCommodityCodePage) map {
    x => AnswerRow("similarItemCommodityCode.checkYourAnswersLabel", s"similarItemCommodityCode.$x", true, routes.SimilarItemCommodityCodeController.onPageLoad(CheckMode).url)
  }

  def returnSamples: Option[AnswerRow] = userAnswers.get(ReturnSamplesPage) map {
    x => AnswerRow("returnSamples.checkYourAnswersLabel", s"returnSamples.$x", true, routes.ReturnSamplesController.onPageLoad(CheckMode).url)
  }

  def whenToSendSample: Option[AnswerRow] = userAnswers.get(WhenToSendSamplePage) map {
    x => AnswerRow("whenToSendSample.checkYourAnswersLabel", s"whenToSendSample.$x", true, routes.WhenToSendSampleController.onPageLoad(CheckMode).url)
  }

  def commodityCodeDigits: Option[AnswerRow] = userAnswers.get(CommodityCodeDigitsPage) map {
    x => AnswerRow("commodityCodeDigits.checkYourAnswersLabel", s"$x", false, routes.CommodityCodeDigitsController.onPageLoad(CheckMode).url)
  }

  def commodityCodeBestMatch: Option[AnswerRow] = userAnswers.get(CommodityCodeBestMatchPage) map {
    x => AnswerRow("commodityCodeBestMatch.checkYourAnswersLabel", s"commodityCodeBestMatch.$x", true, routes.CommodityCodeBestMatchController.onPageLoad(CheckMode).url)
  }

  def confidentialInformation: Option[AnswerRow] = userAnswers.get(ConfidentialInformationPage) map {
    x => AnswerRow("confidentialInformation.checkYourAnswersLabel", s"${x.field1}", false, routes.ConfidentialInformationController.onPageLoad(CheckMode).url)
  }

  def uploadSupportingMaterialMultiple: Option[AnswerRow] = userAnswers.get(UploadSupportingMaterialMultiplePage) map {
    x => AnswerRow("uploadSupportingMaterialMultiple.checkYourAnswersLabel", x.map(_.name), false, routes.UploadSupportingMaterialMultipleController.onPageLoad(CheckMode).url)
  }

  def describeYourItem: Option[AnswerRow] = userAnswers.get(DescribeYourItemPage) map {
    x => AnswerRow("describeYourItem.checkYourAnswersLabel", Seq(x.field1, x.field2), false, routes.DescribeYourItemController.onPageLoad(CheckMode).url)
  }

  def previousCommodityCode: Option[AnswerRow] = userAnswers.get(PreviousCommodityCodePage) map {
    x => AnswerRow("previousCommodityCode.checkYourAnswersLabel", s"${x.field1}", false, routes.PreviousCommodityCodeController.onPageLoad(CheckMode).url)
  }

  def informationAboutYourItem: Option[AnswerRow] = userAnswers.get(InformationAboutYourItemPage) map {
    x => AnswerRow("informationAboutYourItem.checkYourAnswersLabel", s"informationAboutYourItem.$x", true, routes.InformationAboutYourItemController.onPageLoad(CheckMode).url)
  }

  def enterContactDetails: Option[AnswerRow] = userAnswers.get(EnterContactDetailsPage) map {
    x => AnswerRow("enterContactDetails.checkYourAnswersLabel", Seq(x.field1, x.field2, x.field3.orNull), false, routes.EnterContactDetailsController.onPageLoad(CheckMode).url)
  }

  def registerBusinessRepresenting: Option[AnswerRow] = userAnswers.get(RegisterBusinessRepresentingPage) map {
    x =>
      AnswerRow("registerBusinessRepresenting.checkYourAnswersLabel",
        Seq(x.eoriNumber, x.businessName, x.addressLine1, x.town, x.postCode, x.country),
        false,
        routes.RegisterBusinessRepresentingController.onPageLoad(CheckMode).url)
  }

  def selectApplicationType: Option[AnswerRow] = userAnswers.get(SelectApplicationTypePage) map {
    x => AnswerRow("selectApplicationType.checkYourAnswersLabel", s"selectApplicationType.$x", true, routes.SelectApplicationTypeController.onPageLoad(CheckMode).url)
  }

  def whichBestDescribesYou: Option[AnswerRow] = userAnswers.get(WhichBestDescribesYouPage) map {
    x => AnswerRow("whichBestDescribesYou.checkYourAnswersLabel", s"whichBestDescribesYou.$x", true, routes.WhichBestDescribesYouController.onPageLoad(CheckMode).url)
  }

  def registeredAddressForEori: Option[AnswerRow] = userAnswers.get(RegisteredAddressForEoriPage) map {
    x => AnswerRow("registeredAddressForEori.checkYourAnswersLabel", Seq(x.field1, x.field2, x.field3, x.field4, x.field5), false, routes.RegisteredAddressForEoriController.onPageLoad(CheckMode).url)
  }

}
