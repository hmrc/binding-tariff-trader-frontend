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

package generators

import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {


  implicit lazy val arbitrarySupportInformation: Arbitrary[SupportingInformation] =
    Arbitrary {
      Gen.oneOf(SupportingInformation.values.toSeq)
    }

  implicit lazy val arbitraryLegalChallenge: Arbitrary[Boolean] =
    Arbitrary {
      Gen.oneOf(Seq(true,false))
    }

  implicit lazy val arbitrarySimilarItemCommodityCode: Arbitrary[SimilarItemCommodityCode] =
    Arbitrary {
      Gen.oneOf(SimilarItemCommodityCode.values.toSeq)
    }

  implicit lazy val arbitraryReturnSamples: Arbitrary[ReturnSamples] =
    Arbitrary {
      Gen.oneOf(ReturnSamples.values.toSeq)
    }

  implicit lazy val arbitraryWhenToSendSample: Arbitrary[WhenToSendSample] =
    Arbitrary {
      Gen.oneOf(WhenToSendSample.values.toSeq)
    }

  implicit lazy val arbitraryCommodityCodeBestMatch: Arbitrary[CommodityCodeBestMatch] =
    Arbitrary {
      Gen.oneOf(CommodityCodeBestMatch.values.toSeq)
    }

  implicit lazy val arbitraryConfidentialInformation: Arbitrary[ConfidentialInformation] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
      } yield ConfidentialInformation(field1)
    }

  implicit lazy val arbitraryUploadSupportingMaterialMultiple: Arbitrary[Seq[FileAttachment]] =
    Arbitrary {
      for {
        id <- arbitrary[String]
        name <- arbitrary[String]
        mimeType <- arbitrary[String]
        size <- arbitrary[Long]
      } yield Seq(FileAttachment(id, name, mimeType, size))
    }

  implicit lazy val arbitraryUploadWrittenAuthorisation: Arbitrary[FileAttachment] =
    Arbitrary {
      for {
        id <- arbitrary[String]
        name <- arbitrary[String]
        mimeType <- arbitrary[String]
        size <- arbitrary[Long]
      } yield FileAttachment(id, name, mimeType, size)
    }

  implicit lazy val arbitraryDescribeYourItem: Arbitrary[DescribeYourItem] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
        field2 <- arbitrary[String]
      } yield DescribeYourItem(field1, field2)
    }

  implicit lazy val arbitraryPreviousCommodityCode: Arbitrary[PreviousCommodityCode] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
      } yield PreviousCommodityCode(field1)
    }

  implicit lazy val arbitraryInformationAboutYourItem: Arbitrary[InformationAboutYourItem] =
    Arbitrary {
      Gen.oneOf(InformationAboutYourItem.values.toSeq)
    }

  implicit lazy val arbitraryEnterContactDetails: Arbitrary[EnterContactDetails] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
        field2 <- arbitrary[String]
        field3 <- arbitrary[Option[String]]
      } yield EnterContactDetails(field1, field2, field3)
    }

  implicit lazy val arbitraryRegisterBusinessRepresenting: Arbitrary[RegisterBusinessRepresenting] =
    Arbitrary {
      for {
        eoriNumber <- arbitrary[String]
        businessName <- arbitrary[String]
        addressLine1 <- arbitrary[String]
        town <- arbitrary[String]
        postCode <- arbitrary[String]
        country <- arbitrary[String]
      } yield RegisterBusinessRepresenting(eoriNumber, businessName, addressLine1, town, postCode, country)
    }
 
  implicit lazy val arbitrarySelectApplicationType: Arbitrary[SelectApplicationType] =
    Arbitrary {
      Gen.oneOf(SelectApplicationType.values.toSeq)
    }

  implicit lazy val arbitraryWhichBestDescribesYou: Arbitrary[WhichBestDescribesYou] =
    Arbitrary {
      Gen.oneOf(WhichBestDescribesYou.values.toSeq)
    }

  implicit lazy val arbitraryRegisteredAddressForEori: Arbitrary[RegisteredAddressForEori] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
        field2 <- arbitrary[String]
        field3 <- arbitrary[String]
        field4 <- arbitrary[String]
        field5 <- arbitrary[String]
      } yield RegisteredAddressForEori(field1, field2, field3, field4, field5)
    }

}
