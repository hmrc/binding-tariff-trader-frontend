/*
 * Copyright 2019 HM Revenue & Customs
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

  def buildAgentDetails: UserAnswers => Option[AgentDetails] = { answers: UserAnswers =>
    if (isBusinessRepresentative(answers)) {
      answers.get(RegisterBusinessRepresentingPage).map { details: RegisterBusinessRepresenting =>
        AgentDetails(
          EORIDetails(
            details.eoriNumber,
            details.businessName,
            details.addressLine1,
            details.town, // Line 2 empty
            "", // Line 3 empty
            details.postCode,
            details.country
          )
        )
      }
    } else {
      None
    }
  }

  def map(eoriNumber: String, answers: UserAnswers): NewCaseRequest = {

    val confidentialInfo: Option[ConfidentialInformation] = answers.get(ConfidentialInformationPage)
    val describeYourItem: Option[DescribeYourItem] = answers.get(DescribeYourItemPage)
    val contactDetails: Option[EnterContactDetails] = answers.get(EnterContactDetailsPage)
    val previousCommodityCode: Option[PreviousCommodityCode] = answers.get(PreviousCommodityCodePage)
    val registeredAddressForEori: Option[RegisteredAddressForEori] = answers.get(RegisteredAddressForEoriPage)
    val commodityCodeRulingReference: Option[String] = answers.get(CommodityCodeRulingReferencePage)
    val legalChallengeDetails: Option[String] = answers.get(LegalChallengeDetailsPage)
    val commodityCodeDigits: Option[String] = answers.get(CommodityCodeDigitsPage)
    val supportingInformationDetails: Option[String] = answers.get(SupportingInformationDetailsPage)

    val sampleProvided: Option[WhenToSendSample] = answers.get(WhenToSendSamplePage)
    val returnSample: Option[ReturnSamples] = answers.get(ReturnSamplesPage)

    val contact = contactDetails.map(toContact).get
    val holder: EORIDetails = registeredAddressForEori.map(toHolder(eoriNumber)).get

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
      sampleToBeProvided = toSampleProvided(sampleProvided),
      sampleToBeReturned = toReturnSample(returnSample)
    )

    NewCaseRequest(app)
  }

  def toHolder(eoriNumber: String): RegisteredAddressForEori => EORIDetails = { details: RegisteredAddressForEori =>
    EORIDetails(
      eoriNumber,
      details.field1,
      details.field2,
      details.field3,
      "", // TODO: Missing From model
      details.field4,
      details.field5
    )
  }

  def toContact: EnterContactDetails => Contact = { details: EnterContactDetails =>
    Contact(
      name = details.field1,
      email = details.field2,
      phone = details.field3
    )
  }

  private def toSampleProvided: Option[WhenToSendSample] => Boolean = { op: Option[WhenToSendSample] =>
    op.getOrElse(WhenToSendSample.No) match {
      case WhenToSendSample.Yes => true
      case _ => false
    }
  }

  private def toReturnSample: Option[ReturnSamples] => Boolean = { op: Option[ReturnSamples] =>
    op.getOrElse(ReturnSamples.No) match {
      case ReturnSamples.Yes => true
      case _ => false
    }
  }

}
