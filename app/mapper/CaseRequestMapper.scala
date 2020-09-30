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

package mapper

import javax.inject.Singleton
import models.WhichBestDescribesYou.theUserIsAnAgent
import models._
import pages._

@Singleton
class CaseRequestMapper {

  private def throwError(field: String) = throw new IllegalStateException(s"Missing User Session Data: $field")

  def map(answers: UserAnswers): NewCaseRequest = {
    val goodsName = answers.get(ProvideGoodsNamePage)
    val goodsDescription = answers.get(ProvideGoodsDescriptionPage)
    val provideConfidentialInformation = confidentialInfo(answers)
    val contactDetails: Option[EnterContactDetails] = answers.get(EnterContactDetailsPage)
    val previousCommodityCode: Option[PreviousCommodityCode] = answers.get(PreviousCommodityCodePage)
    val commodityCodeRulingReference: Option[String] = answers.get(CommodityCodeRulingReferencePage)
    val legalChallengeDetails: Option[String] = answers.get(LegalChallengeDetailsPage)
    val commodityCodeDigits: Option[String] = answers.get(CommodityCodeDigitsPage)
    val supportingInformationDetails: Option[String] = answers.get(SupportingInformationDetailsPage)

    val sampleProvided: Boolean = answers.get(WhenToSendSamplePage).getOrElse(throwError("when to send a sample"))
    val sampleHazardous: Option[Boolean] = answers.get(IsSampleHazardousPage)
    val returnSample: Boolean = answers.get(ReturnSamplesPage).getOrElse(false)

    val contact = contactDetails.map(toContact).getOrElse(throwError("contact details"))

    val agentDetails: Option[AgentDetails] = agentDetailsFrom(answers)
    val holderDetails: EORIDetails = holderDetailsFrom(answers)


    val app = Application(
      holder = holderDetails,
      contact = contact,
      agent = agentDetails,
      offline = false,
      goodName = goodsName.getOrElse(throwError("goods name")),
      goodDescription = goodsDescription.getOrElse(throwError("goods description")),
      confidentialInformation = provideConfidentialInformation,
      otherInformation = supportingInformationDetails,
      reissuedBTIReference = previousCommodityCode.map(_.previousCommodityCode),
      relatedBTIReference = commodityCodeRulingReference,
      knownLegalProceedings = legalChallengeDetails,
      envisagedCommodityCode = commodityCodeDigits,
      sampleToBeProvided = sampleProvided,
      sampleIsHazardous = sampleHazardous,
      sampleToBeReturned = returnSample
    )

    NewCaseRequest(app)
  }

  private def confidentialInfo(answers: UserAnswers): Option[String] = {
    answers.get(AddConfidentialInformationPage) match {
      case Some(true) => answers.get(ProvideConfidentialInformationPage)
      case _ => None
    }
  }

  private def holderDetailsFrom(answers: UserAnswers): EORIDetails = {
    val maybeEoriDetails = if (theUserIsAnAgent(answers)) {
      answers.get(RegisterBusinessRepresentingPage).map(toEoriDetails)
    } else {
      answers.get(RegisteredAddressForEoriPage).map(toEoriDetails)
    }

    maybeEoriDetails.getOrElse(throwError("holder EORI details"))
  }

  private def agentDetailsFrom(answers: UserAnswers): Option[AgentDetails] = {
    if (theUserIsAnAgent(answers)) {
      answers.get(RegisteredAddressForEoriPage).map { details: RegisteredAddressForEori =>
        AgentDetails(toEoriDetails(details))
      }
    } else {
      None
    }
  }

  private def toEoriDetails(details: RegisterBusinessRepresenting): EORIDetails = {
    EORIDetails(
      details.eoriNumber,
      details.businessName,
      details.addressLine1,
      details.town,
      "", // address line 3 empty
      details.postCode.getOrElse(""),
      details.country
    )
  }

  private def toEoriDetails(details: RegisteredAddressForEori): EORIDetails = {
    EORIDetails(
      details.eori,
      details.businessName,
      details.addressLine1,
      details.townOrCity,
      "", // address line 3 empty
      details.postcode.getOrElse(""),
      details.country
    )
  }

  private def toContact: EnterContactDetails => Contact = { details: EnterContactDetails =>
    Contact(
      name = details.name,
      email = details.email,
      phone = details.phoneNumber
    )
  }

}
