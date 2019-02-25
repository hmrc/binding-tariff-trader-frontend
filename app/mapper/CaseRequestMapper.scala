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

  private def throwError(field: String) = {
    throw new IllegalStateException(s"Missing User Session Data: $field")
  }

  def map(userEori: String, answers: UserAnswers): NewCaseRequest = {

    val confidentialInfo: Option[ConfidentialInformation] = answers.get(ConfidentialInformationPage)
    val describeYourItem: Option[DescribeYourItem] = answers.get(DescribeYourItemPage)
    val contactDetails: Option[EnterContactDetails] = answers.get(EnterContactDetailsPage)
    val previousCommodityCode: Option[PreviousCommodityCode] = answers.get(PreviousCommodityCodePage)
    val commodityCodeRulingReference: Option[String] = answers.get(CommodityCodeRulingReferencePage)
    val legalChallengeDetails: Option[String] = answers.get(LegalChallengeDetailsPage)
    val commodityCodeDigits: Option[String] = answers.get(CommodityCodeDigitsPage)
    val supportingInformationDetails: Option[String] = answers.get(SupportingInformationDetailsPage)

    val sampleProvided: Option[WhenToSendSample] = answers.get(WhenToSendSamplePage)
    val returnSample: Option[ReturnSamples] = answers.get(ReturnSamplesPage)

    val contact = contactDetails.map(toContact).getOrElse(throwError("contact details"))

    val agentDetails: Option[AgentDetails] = buildAgentDetails(userEori)(answers)
    val holderDetails: EORIDetails = buildHolderDetails(userEori)(answers)

    val app = Application(
      holder = holderDetails,
      contact = contact,
      agent = agentDetails,
      offline = false,
      goodName = describeYourItem.map(_.field1).getOrElse("N/A"),
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

  private def buildHolderDetails(userEori: String): UserAnswers => EORIDetails = { answers: UserAnswers =>
    val maybeEoriDetails = if (isBusinessRepresentative(answers)) {
      answers.get(RegisterBusinessRepresentingPage).map(buildTraderEoriDetails(userEori)(_))
    } else {
      answers.get(RegisteredAddressForEoriPage).map(buildEoriDetailsFromCurrentUser(userEori)(_))
    }

    maybeEoriDetails.getOrElse(throwError("holder EORI details"))
  }

  private def buildAgentDetails(userEori: String): UserAnswers => Option[AgentDetails] = { answers: UserAnswers =>
    if (isBusinessRepresentative(answers)) {
      answers.get(RegisteredAddressForEoriPage).map { details: RegisteredAddressForEori =>
        AgentDetails(buildEoriDetailsFromCurrentUser(userEori)(details))
      }
    } else {
      None
    }
  }

  private def buildTraderEoriDetails(eoriNumber: String): RegisterBusinessRepresenting => EORIDetails = { details: RegisterBusinessRepresenting =>
    EORIDetails(
      details.eoriNumber,
      details.businessName,
      details.addressLine1,
      details.town,
      "", // address line 3 empty
      details.postCode,
      details.country
    )
  }

  private def buildEoriDetailsFromCurrentUser(eoriNumber: String): RegisteredAddressForEori => EORIDetails = { details: RegisteredAddressForEori =>
    EORIDetails(
      eoriNumber,
      details.field1, // business name
      details.field2, // address line 1
      details.field3, // address line 2 (town)
      "",             // address line 3 empty
      details.field4, // post code
      details.field5  // country
    )
  }

  private def toContact: EnterContactDetails => Contact = { details: EnterContactDetails =>
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
