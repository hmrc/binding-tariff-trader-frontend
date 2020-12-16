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

package utils

import controllers.routes
import models.requests.DataRequest
import models.{CheckMode, Country, UserAnswers}
import pages._
import play.api.i18n.Messages
import viewmodels.AnswerRow
import models.FileAttachment

class CheckYourAnswersHelper(
                              userAnswers: UserAnswers,
                              countries: Map[String, Country]
                            )
                            (
                              implicit messages: Messages
                            ) {

  def provideGoodsName: Option[AnswerRow] = userAnswers.get(ProvideGoodsNamePage) map {
    x => AnswerRow("provideGoodsName.checkYourAnswersLabel", s"$x", false, routes.ProvideGoodsNameController.onPageLoad(CheckMode).url)
  }

  def provideGoodsDescription: Option[AnswerRow] = userAnswers.get(ProvideGoodsDescriptionPage) map {
    x => AnswerRow("provideGoodsDescription.checkYourAnswersLabel", s"$x", false, routes.ProvideGoodsDescriptionController.onPageLoad(CheckMode).url)
  }

  def addConfidentialInformation: Option[AnswerRow] = userAnswers.get(AddConfidentialInformationPage) map {
    x => AnswerRow("addConfidentialInformation.checkYourAnswersLabel", yesNoAnswer(x), true, routes.AddConfidentialInformationController.onPageLoad(CheckMode).url)
  }

  def provideConfidentialInformation: Option[AnswerRow] = userAnswers.get(ProvideConfidentialInformationPage) map {
    x => AnswerRow("provideConfidentialInformation.checkYourAnswersLabel", s"$x", false, routes.ProvideConfidentialInformationController.onPageLoad(CheckMode).url)
  }

  def legalChallengeDetails: Option[AnswerRow] = userAnswers.get(LegalChallengeDetailsPage) map {
    x => AnswerRow("legalChallengeDetails.checkYourAnswersLabel", s"$x", false, routes.LegalChallengeDetailsController.onPageLoad(CheckMode).url)
  }

  def legalChallenge: Option[AnswerRow] = userAnswers.get(LegalChallengePage) map {
    x => AnswerRow("legalChallenge.checkYourAnswersLabel", yesNoAnswer(x), true, routes.LegalChallengeController.onPageLoad(CheckMode).url)
  }

  def commodityCodeRulingReference: Option[AnswerRow] = userAnswers.get(CommodityCodeRulingReferencePage) map {
    x => AnswerRow("commodityCodeRulingReference.checkYourAnswersLabel", x.mkString("\n"), false, routes.AddAnotherRulingController.onPageLoad(CheckMode).url)
  }

  def similarItemCommodityCode: Option[AnswerRow] = userAnswers.get(SimilarItemCommodityCodePage) map {
    x => AnswerRow("similarItemCommodityCode.checkYourAnswersLabel", yesNoAnswer(x), true, routes.SimilarItemCommodityCodeController.onPageLoad(CheckMode).url)
  }

  def returnSamples: Option[AnswerRow] = userAnswers.get(ReturnSamplesPage) map {
    x => AnswerRow("returnSamples.checkYourAnswersLabel", yesNoAnswer(x), true, routes.ReturnSamplesController.onPageLoad(CheckMode).url)
  }

  def isSampleHazardous: Option[AnswerRow] = userAnswers.get(IsSampleHazardousPage) map {
    x => AnswerRow("isSampleHazardous.checkYourAnswersLabel", yesNoAnswer(x), true, routes.IsSampleHazardousController.onPageLoad(CheckMode).url)
  }

  def areYouSendingSamples: Option[AnswerRow] = userAnswers.get(AreYouSendingSamplesPage) map {
    x => AnswerRow("areYouSendingSamples.checkYourAnswersLabel", yesNoAnswer(x), true, routes.AreYouSendingSamplesController.onPageLoad(CheckMode).url)
  }

  def commodityCodeDigits: Option[AnswerRow] = userAnswers.get(CommodityCodeDigitsPage) map {
    x => AnswerRow("commodityCodeDigits.checkYourAnswersLabel", s"$x", false, routes.CommodityCodeDigitsController.onPageLoad(CheckMode).url)
  }

  def commodityCodeBestMatch: Option[AnswerRow] = userAnswers.get(CommodityCodeBestMatchPage) map {
    x => AnswerRow("commodityCodeBestMatch.checkYourAnswersLabel", yesNoAnswer(x), true, routes.CommodityCodeBestMatchController.onPageLoad(CheckMode).url)
  }

  def supportingMaterialFileListChoice: Option[AnswerRow] = {
    userAnswers.get(AddSupportingDocumentsPage).map { addDocuments =>
      AnswerRow(
        "supportingMaterialFileList.choice.checkYourAnswersLabel",
        yesNoAnswer(addDocuments), true,
        routes.AddSupportingDocumentsController.onPageLoad(CheckMode).url
      )
    }
  }

  def supportingMaterialFileList: Option[AnswerRow] = {
    def filesRow(files: Seq[String]): AnswerRow = AnswerRow(
      "supportingMaterialFileList.checkYourAnswersLabel",
      files, false,
      routes.SupportingMaterialFileListController.onPageLoad(CheckMode).url
    )

    val keepConfidential = userAnswers
      .get(MakeFileConfidentialPage)
      .getOrElse(Map.empty)

    def confidentialLabel(attachment: FileAttachment)(implicit messages: Messages) =
      if (keepConfidential(attachment.id)) "- " + messages("site.keep_confidential") else ""

    userAnswers.get(UploadSupportingMaterialMultiplePage).collect {
      case attachments if attachments.nonEmpty =>
        val attachmentLabels = attachments.map { att =>
          att.name + confidentialLabel(att)
        }
        filesRow(attachmentLabels)
    }
  }

  def provideBTIReference: Option[AnswerRow] = userAnswers.get(ProvideBTIReferencePage) map {
    x => AnswerRow("provideBTIReference.checkYourAnswersLabel", s"${x.reference}", false, routes.ProvideBTIReferenceController.onPageLoad(CheckMode).url)
  }

  def registeredName: Option[AnswerRow] = userAnswers.get(RegisteredAddressForEoriPage).map { regAddress =>
    AnswerRow(
      "registeredAddressForEori.registeredName.checkYourAnswersLabel", s"${regAddress.businessName}", false,
      routes.RegisteredAddressForEoriController.onPageLoad(CheckMode).url
    )
  }

  def registeredAddress: Option[AnswerRow] = userAnswers.get(RegisteredAddressForEoriPage).map { regAddress =>
    val formattedAddress = List(
      regAddress.addressLine1,
      regAddress.townOrCity,
      regAddress.postcode.getOrElse(""),
      messages(getCountryName(regAddress.country).mkString)
    ).filterNot(_.isEmpty).mkString("\n")

    AnswerRow(
      "registeredAddressForEori.registeredAddress.checkYourAnswersLabel", formattedAddress, false,
      routes.RegisteredAddressForEoriController.onPageLoad(CheckMode).url
    )
  }

  def enterContactDetailsName: Option[AnswerRow] = userAnswers.get(EnterContactDetailsPage) map {
      x => AnswerRow("enterContactDetails.checkYourAnswersLabel.name", s"${x.name}", false,
        routes.EnterContactDetailsController.onPageLoad(CheckMode).url)
  }

  def enterContactDetailsEmail: Option[AnswerRow] = userAnswers.get(EnterContactDetailsPage) map {
    x => AnswerRow("enterContactDetails.checkYourAnswersLabel.email", s"${x.email}", false,
      routes.EnterContactDetailsController.onPageLoad(CheckMode).url)
  }

  def enterContactDetailsPhone: Option[AnswerRow] = userAnswers.get(EnterContactDetailsPage) map {
    x => AnswerRow("enterContactDetails.checkYourAnswersLabel.phone", s"${x.phoneNumber}", false,
      routes.EnterContactDetailsController.onPageLoad(CheckMode).url)
  }

  def previousBTIRuling: Option[AnswerRow] = userAnswers.get(PreviousBTIRulingPage) map { x =>
    AnswerRow("previousBTIRuling.checkYourAnswersLabel", yesNoAnswer(x), true, routes.PreviousBTIRulingController.onPageLoad(CheckMode).url)
  }

  def whichBestDescribesYou: Option[AnswerRow] = None

  //    userAnswers.get(WhichBestDescribesYouPage) map {
  //    x => AnswerRow("whichBestDescribesYou.checkYourAnswersLabel", s"whichBestDescribesYou.$x", true, routes.WhichBestDescribesYouController.onPageLoad(CheckMode).url)
  //  }

  def registeredAddressForEori(implicit request: DataRequest[_]): Option[AnswerRow] = userAnswers.get(RegisteredAddressForEoriPage) map { x =>

    val fields = if (request.eoriNumber.isDefined) {
      Seq(x.businessName, x.addressLine1, x.townOrCity, x.postcode.getOrElse(""), messages(getCountryName(x.country).mkString))
    } else {
      Seq(x.eori, x.businessName, x.addressLine1, x.townOrCity, x.postcode.getOrElse(""), messages(getCountryName(x.country).mkString))
    }
    AnswerRow("registeredAddressForEori.checkYourAnswersLabel", fields, false, routes.RegisteredAddressForEoriController.onPageLoad(CheckMode).url)
  }

  private def yesNoAnswer(x: Boolean) = if (x) "site.yes" else "site.no"

  def getCountryName(code: String): Option[String] =
    countries.get(code).map(_.countryName)
}
