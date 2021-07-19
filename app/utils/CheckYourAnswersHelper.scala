/*
 * Copyright 2021 HM Revenue & Customs
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
                            )(implicit messages: Messages) {

  def provideGoodsName: Option[AnswerRow] = userAnswers.get(ProvideGoodsNamePage) map {
    x => AnswerRow(
      label = "provideGoodsName.checkYourAnswersLabel",
      answer = s"$x",
      answerIsMessageKey = false,
      changeUrl = routes.ProvideGoodsNameController.onPageLoad(CheckMode).url
    )
  }

  def provideGoodsDescription: Option[AnswerRow] = userAnswers.get(ProvideGoodsDescriptionPage) map {
    x => AnswerRow(
      label = "provideGoodsDescription.checkYourAnswersLabel",
      answer = s"$x",
      answerIsMessageKey = false,
      changeUrl = routes.ProvideGoodsDescriptionController.onPageLoad(CheckMode).url
    )
  }

  def addConfidentialInformation: Option[AnswerRow] = userAnswers.get(AddConfidentialInformationPage) map {
    x => AnswerRow(
      label = "addConfidentialInformation.checkYourAnswersLabel",
      answer = yesNoAnswer(x),
      answerIsMessageKey = true,
      changeUrl = routes.AddConfidentialInformationController.onPageLoad(CheckMode).url
    )
  }

  def provideConfidentialInformation: Option[AnswerRow] = userAnswers.get(ProvideConfidentialInformationPage) map {
    x => AnswerRow(
      label = "provideConfidentialInformation.checkYourAnswersLabel",
      answer = s"$x",
      answerIsMessageKey = false,
      changeUrl = routes.ProvideConfidentialInformationController.onPageLoad(CheckMode).url
    )
  }

  def legalChallengeDetails: Option[AnswerRow] = userAnswers.get(LegalChallengeDetailsPage) map {
    x => AnswerRow(
      label = "legalChallengeDetails.checkYourAnswersLabel",
      answer = s"$x",
      answerIsMessageKey = false,
      changeUrl = routes.LegalChallengeDetailsController.onPageLoad(CheckMode).url
    )
  }

  def legalChallenge: Option[AnswerRow] = userAnswers.get(LegalChallengePage) map {
    x => AnswerRow(
      label = "legalChallenge.checkYourAnswersLabel",
      answer = yesNoAnswer(x),
      answerIsMessageKey = true,
      changeUrl = routes.LegalChallengeController.onPageLoad(CheckMode).url
    )
  }

  def commodityCodeRulingReference: Option[AnswerRow] = userAnswers.get(CommodityCodeRulingReferencePage) map {
    x => AnswerRow(
      label = "commodityCodeRulingReference.checkYourAnswersLabel",
      answer = x.mkString("\n"),
      answerIsMessageKey = false,
      changeUrl = routes.AddAnotherRulingController.onPageLoad(CheckMode).url
    )
  }

  def similarItemCommodityCode: Option[AnswerRow] = userAnswers.get(SimilarItemCommodityCodePage) map {
    x => AnswerRow(
      label = "similarItemCommodityCode.checkYourAnswersLabel",
      answer = yesNoAnswer(x),
      answerIsMessageKey = true,
      changeUrl = routes.SimilarItemCommodityCodeController.onPageLoad(CheckMode).url
    )
  }

  def returnSamples: Option[AnswerRow] = userAnswers.get(ReturnSamplesPage) map {
    x => AnswerRow(
      label = "returnSamples.checkYourAnswersLabel",
      answer = yesNoAnswer(x),
      answerIsMessageKey = true,
      changeUrl = routes.ReturnSamplesController.onPageLoad(CheckMode).url
    )
  }

  def isSampleHazardous: Option[AnswerRow] = userAnswers.get(IsSampleHazardousPage) map {
    x => AnswerRow(
      label = "isSampleHazardous.checkYourAnswersLabel",
      answer = yesNoAnswer(x),
      answerIsMessageKey = true,
      changeUrl = routes.IsSampleHazardousController.onPageLoad(CheckMode).url
    )
  }

  def areYouSendingSamples: Option[AnswerRow] = userAnswers.get(AreYouSendingSamplesPage) map {
    x => AnswerRow(
      label = "areYouSendingSamples.checkYourAnswersLabel",
      answer = yesNoAnswer(x),
      answerIsMessageKey = true,
      changeUrl = routes.AreYouSendingSamplesController.onPageLoad(CheckMode).url
    )
  }

  def commodityCodeDigits: Option[AnswerRow] = userAnswers.get(CommodityCodeDigitsPage) map {
    x => AnswerRow(
      label = "commodityCodeDigits.checkYourAnswersLabel",
      answer = s"$x",
      answerIsMessageKey = false,
      changeUrl = routes.CommodityCodeDigitsController.onPageLoad(CheckMode).url
    )
  }

  def commodityCodeBestMatch: Option[AnswerRow] = userAnswers.get(CommodityCodeBestMatchPage) map {
    x => AnswerRow(
      label = "commodityCodeBestMatch.checkYourAnswersLabel",
      answer = yesNoAnswer(x),
      answerIsMessageKey = true,
      changeUrl = routes.CommodityCodeBestMatchController.onPageLoad(CheckMode).url
    )
  }

  def supportingMaterialFileListChoice: Option[AnswerRow] = {
    userAnswers.get(AddSupportingDocumentsPage).map { addDocuments =>
      AnswerRow(
        label = "supportingMaterialFileList.choice.checkYourAnswersLabel",
        answer = yesNoAnswer(addDocuments),
        answerIsMessageKey = true,
        changeUrl = routes.AddSupportingDocumentsController.onPageLoad(CheckMode).url
      )
    }
  }

  def supportingMaterialFileList: Option[AnswerRow] = {
    def filesRow(files: Seq[String]): AnswerRow = AnswerRow(
      label = "supportingMaterialFileList.checkYourAnswersLabel",
      answer = files,
      answerIsMessageKey = false,
      changeUrl = routes.SupportingMaterialFileListController.onPageLoad(CheckMode).url
    )

    val keepConfidential = userAnswers
      .get(MakeFileConfidentialPage)
      .getOrElse(Map.empty)

    def confidentialLabel(attachment: FileAttachment)(implicit messages: Messages): String =
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
    x => AnswerRow(
      label = "provideBTIReference.checkYourAnswersLabel",
      answer = s"${x.reference}",
      answerIsMessageKey = false,
      changeUrl = routes.ProvideBTIReferenceController.onPageLoad(CheckMode).url
    )
  }

  def registeredName: Option[AnswerRow] = userAnswers.get(RegisteredAddressForEoriPage).map { regAddress =>
    AnswerRow(
      label = "registeredAddressForEori.registeredName.checkYourAnswersLabel",
      answer = s"${regAddress.businessName}",
      answerIsMessageKey = false,
      changeUrl = routes.RegisteredAddressForEoriController.onPageLoad(CheckMode).url
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
      label = "registeredAddressForEori.registeredAddress.checkYourAnswersLabel",
      answer = formattedAddress,
      answerIsMessageKey = false,
      changeUrl = routes.RegisteredAddressForEoriController.onPageLoad(CheckMode).url
    )
  }

  def enterContactDetailsName: Option[AnswerRow] = userAnswers.get(EnterContactDetailsPage) map {
      x => AnswerRow(
        label = "enterContactDetails.checkYourAnswersLabel.name",
        answer = s"${x.name}",
        answerIsMessageKey = false,
        changeUrl = routes.EnterContactDetailsController.onPageLoad(CheckMode).url
      )
  }

  def enterContactDetailsEmail: Option[AnswerRow] = userAnswers.get(EnterContactDetailsPage) map {
    x => AnswerRow(
      label = "enterContactDetails.checkYourAnswersLabel.email",
      answer = s"${x.email}",
      answerIsMessageKey = false,
      changeUrl = routes.EnterContactDetailsController.onPageLoad(CheckMode).url
    )
  }

  def enterContactDetailsPhone: Option[AnswerRow] = userAnswers.get(EnterContactDetailsPage) map {
    x => AnswerRow(
      label = "enterContactDetails.checkYourAnswersLabel.phone",
      answer = s"${x.phoneNumber}",
      answerIsMessageKey = false,
      changeUrl = routes.EnterContactDetailsController.onPageLoad(CheckMode).url
    )
  }

  def previousBTIRuling: Option[AnswerRow] = userAnswers.get(PreviousBTIRulingPage) map { x =>
    AnswerRow(
      label = "previousBTIRuling.checkYourAnswersLabel",
      answer = yesNoAnswer(x),
      answerIsMessageKey = true,
      changeUrl = routes.PreviousBTIRulingController.onPageLoad(CheckMode).url
    )
  }

  def whichBestDescribesYou: Option[AnswerRow] = None

  def registeredAddressForEori(implicit request: DataRequest[_]): Option[AnswerRow] = userAnswers.get(RegisteredAddressForEoriPage) map { x =>

    val fields = if (request.eoriNumber.isDefined) {
      Seq(x.businessName, x.addressLine1, x.townOrCity, x.postcode.getOrElse(""), messages(getCountryName(x.country).mkString))
    } else {
      Seq(x.eori, x.businessName, x.addressLine1, x.townOrCity, x.postcode.getOrElse(""), messages(getCountryName(x.country).mkString))
    }
    AnswerRow(
      label = "registeredAddressForEori.checkYourAnswersLabel",
      answer = fields,
      answerIsMessageKey = false,
      changeUrl = routes.RegisteredAddressForEoriController.onPageLoad(CheckMode).url
    )
  }

  private def yesNoAnswer(x: Boolean): String = if (x) "site.yes" else "site.no"

  def getCountryName(code: String): Option[String] = countries.get(code).map(_.countryName)
}
