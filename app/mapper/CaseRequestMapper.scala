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

package mapper

import javax.inject.Singleton
import models.WhichBestDescribesYou.isBusinessRepresentative
import models._
import pages._

@Singleton
class CaseRequestMapper {

  def buildAgentDetails(answers: UserAnswers): Option[AgentDetails] = {
    if (isBusinessRepresentative(answers)){
      val details: RegisterBusinessRepresenting = answers.get(RegisterBusinessRepresentingPage).get
      Some(AgentDetails(
        EORIDetails(
          details.eoriNumber,
          details.businessName,
          details.addressLine1,
          details.town, // Line 2 empty
          "", // Line 3 empty
          details.postCode,
          details.country
        )
      ))
    }else{
      None
    }
  }

  def map(answers: UserAnswers): NewCaseRequest = {

    val confidentialInfo: Option[ConfidentialInformation] = answers.get(ConfidentialInformationPage)
    val describeYourItem: Option[DescribeYourItem] = answers.get(DescribeYourItemPage)
    val contactDetails: Option[EnterContactDetails] = answers.get(EnterContactDetailsPage)
    val previousCommodityCode: Option[PreviousCommodityCode] = answers.get(PreviousCommodityCodePage)
    val registeredAddressForEori: Option[RegisteredAddressForEori] = answers.get(RegisteredAddressForEoriPage)
    val commodityCodeRulingReference: Option[String] = answers.get(CommodityCodeRulingReferencePage)
    val legalChallengeDetails: Option[String] = answers.get(LegalChallengeDetailsPage)
    val commodityCodeDigits: Option[String] = answers.get(CommodityCodeDigitsPage)
    val supportingInformationDetails: Option[String] = answers.get(SupportingInformationDetailsPage)

    val contact = contactDetails.map(toContact).get
    val holder: EORIDetails = registeredAddressForEori.map(toHolder).get

    val agentDetails = buildAgentDetails(answers)

    val app = Application(
      holder = holder,
      contact = contact,
      agent = agentDetails,
      offline = false,
      goodName = describeYourItem.map(_.field1).getOrElse("CaseRequestMapperTestN/A"),
      goodDescription = describeYourItem.map(_.field2).getOrElse("N/A"),
      confidentialInformation = confidentialInfo.map(_.field1),
      otherInformation = supportingInformationDetails,
      reissuedBTIReference = previousCommodityCode.map(_.field1),
      relatedBTIReference = commodityCodeRulingReference,
      knownLegalProceedings = legalChallengeDetails,
      envisagedCommodityCode = commodityCodeDigits,
      sampleToBeProvided = false, // TODO Unimplemented
      sampleToBeReturned = false // TODO Unimplemented
    )

    NewCaseRequest(app)
  }

  def toHolder: RegisteredAddressForEori => EORIDetails = { details =>
    EORIDetails(
      "", // TODO: Hard Coded
      details.field1,
      details.field2,
      details.field3,
      "", // TODO: Missing From model
      details.field4,
      details.field5
    )
  }

  def toContact: EnterContactDetails => Contact = { details =>
    Contact(
      name = details.field1,
      email = details.field2,
      phone = details.field3
    )
  }

}
