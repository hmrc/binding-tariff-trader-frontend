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

package generators

import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {


  implicit lazy val arbitraryReturnSamples: Arbitrary[ReturnSamples] =
    Arbitrary {
      Gen.oneOf(ReturnSamples.values.toSeq)
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
        field3 <- arbitrary[Option[String]]
      } yield DescribeYourItem(field1, field2, field3)
    }

  implicit lazy val arbitraryPreviousCommodityCode: Arbitrary[PreviousCommodityCode] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
      } yield PreviousCommodityCode(field1)
    }

  implicit lazy val arbitraryInformationAboutYourItem: Arbitrary[Boolean] =
    Arbitrary {
      Gen.oneOf(Seq(true, false))
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

  implicit lazy val arbitraryImportOrExport: Arbitrary[ImportOrExport] =
    Arbitrary {
      Gen.oneOf(ImportOrExport.values.toSeq)
    }

  implicit lazy val arbitraryRegisteredAddressForEori: Arbitrary[RegisteredAddressForEori] =
    Arbitrary {
      for {
        eori <- arbitrary[String]
        businessName <- arbitrary[String]
        addressLine1 <- arbitrary[String]
        town <- arbitrary[String]
        postcode <- arbitrary[String]
        country <- arbitrary[String]
      } yield RegisteredAddressForEori(eori, businessName, addressLine1, town, postcode, country)
    }

}
