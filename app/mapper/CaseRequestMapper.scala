/*
 * Copyright 2024 HM Revenue & Customs
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

package mapper

import com.google.inject.Inject
import config.FrontendAppConfig
import models._
import pages._

import javax.inject.Singleton

@Singleton
class CaseRequestMapper @Inject() (appConfig: FrontendAppConfig) {

  private def throwError(field: String) = throw new IllegalStateException(s"Missing User Session Data: $field")

  def map(answers: UserAnswers): NewCaseRequest = {
    val goodsName                                          = answers.get(ProvideGoodsNamePage)
    val goodsDescription                                   = answers.get(ProvideGoodsDescriptionPage)
    val provideConfidentialInformation                     = confidentialInfo(answers)
    val contactDetails: Option[EnterContactDetails]        = answers.get(EnterContactDetailsPage)
    val provideBTIReference: Option[BTIReference]          = answers.get(ProvideBTIReferencePage)
    val commodityCodeRulingReference: Option[List[String]] = answers.get(CommodityCodeRulingReferencePage)
    val legalChallengeDetails: Option[String]              = answers.get(LegalChallengeDetailsPage)
    val commodityCodeDigits: Option[String]                = answers.get(CommodityCodeDigitsPage)

    val sampleProvided: Boolean = answers
      .get(AreYouSendingSamplesPage)
      .getOrElse(
        if (appConfig.samplesToggle) false else throwError("when to send a sample")
      )

    val sampleHazardous: Option[Boolean] = answers.get(IsSampleHazardousPage)
    val returnSample: Boolean            = answers.get(ReturnSamplesPage).getOrElse(false)

    val contact = contactDetails.map(toContact).getOrElse(throwError("contact details"))

    val holderDetails: EORIDetails = holderDetailsFrom(answers)

    val app = Application(
      holder = holderDetails,
      contact = contact,
      agent = None,
      offline = false,
      goodName = goodsName.getOrElse(throwError("goods name")),
      goodDescription = goodsDescription.getOrElse(throwError("goods description")),
      confidentialInformation = provideConfidentialInformation,
      otherInformation = None,
      reissuedBTIReference = provideBTIReference.map(_.reference),
      relatedBTIReferences = commodityCodeRulingReference.getOrElse(Nil),
      knownLegalProceedings = legalChallengeDetails,
      envisagedCommodityCode = commodityCodeDigits,
      sampleToBeProvided = sampleProvided,
      sampleIsHazardous = sampleHazardous,
      sampleToBeReturned = returnSample
    )

    NewCaseRequest(app)
  }

  private def confidentialInfo(answers: UserAnswers): Option[String] =
    answers.get(AddConfidentialInformationPage) match {
      case Some(true) => answers.get(ProvideConfidentialInformationPage)
      case _          => None
    }

  private def holderDetailsFrom(answers: UserAnswers): EORIDetails = {
    val maybeEoriDetails =
      answers.get(RegisteredAddressForEoriPage).map(toEoriDetails)

    maybeEoriDetails.getOrElse(throwError("holder EORI details"))
  }

  private def toEoriDetails(details: RegisteredAddressForEori): EORIDetails =
    EORIDetails(
      details.eori,
      details.businessName,
      details.addressLine1,
      details.townOrCity,
      "", // address line 3 empty
      details.postcode.getOrElse(""),
      details.country
    )

  private def toContact: EnterContactDetails => Contact = { details: EnterContactDetails =>
    Contact(
      name = details.name,
      email = details.email,
      phone = Option(details.phoneNumber)
    )
  }

}
