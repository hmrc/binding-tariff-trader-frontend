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

package viewmodels

import models.{Case, Contact, EORIDetails}
import play.api.i18n.Messages

import java.time.Instant

case class PdfViewModel(
  eori: String,
  reference: String,
  accountDetails: EORIDetails,
  contact: Contact,
  dateSubmitted: Instant,
  goodsName: String,
  goodsDetails: String,
  confidentialInformation: Option[String],
  sendingSample: Boolean,
  hazardousSample: Boolean,
  returnSample: Boolean,
  attachments: Seq[FileView],
  foundCommodityCode: Option[String],
  legalProblems: Option[String],
  similarAtarReferences: List[String],
  reissuedBTIReference: Option[String]
) {

  def supportingMaterialFileList(implicit messages: Messages): Seq[String] = {
    def confidentialLabel(isConfidential: Boolean): String =
      if (isConfidential) "- " + messages("site.keep_confidential") else ""

    attachments.map(att => s"${att.name} ${confidentialLabel(att.confidential)}")
  }

  def similarAtarCodes: String = similarAtarReferences.mkString("\n")

}

object PdfViewModel {

  def apply(c: Case, fileView: Seq[FileView]): PdfViewModel = new PdfViewModel(
    eori = c.application.holder.eori,
    reference = c.reference,
    accountDetails = c.application.holder,
    contact = c.application.contact,
    dateSubmitted = c.createdDate,
    goodsName = c.application.goodName,
    goodsDetails = c.application.goodDescription,
    confidentialInformation = c.application.confidentialInformation,
    sendingSample = c.application.sampleToBeProvided,
    hazardousSample = c.application.sampleIsHazardous.getOrElse(false),
    returnSample = c.application.sampleToBeReturned,
    attachments = fileView,
    foundCommodityCode = c.application.envisagedCommodityCode,
    legalProblems = c.application.knownLegalProceedings,
    similarAtarReferences = c.application.relatedBTIReferences,
    reissuedBTIReference = c.application.reissuedBTIReference
  )
}
