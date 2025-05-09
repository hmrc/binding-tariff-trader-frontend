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

package generators

import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryUploadSupportingMaterialMultiple: Arbitrary[Seq[FileAttachment]] =
    Arbitrary {
      for {
        id       <- arbitrary[String]
        name     <- arbitrary[String]
        mimeType <- arbitrary[String]
        size     <- arbitrary[Long]
      } yield Seq(FileAttachment(id, name, mimeType, size))
    }

  implicit lazy val arbitraryUploadWrittenAuthorisation: Arbitrary[FileAttachment] =
    Arbitrary {
      for {
        id       <- arbitrary[String]
        name     <- arbitrary[String]
        mimeType <- arbitrary[String]
        size     <- arbitrary[Long]
      } yield FileAttachment(id, name, mimeType, size)
    }

  implicit lazy val arbitraryBTIReference: Arbitrary[BTIReference] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
      } yield BTIReference(field1)
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
        field3 <- arbitrary[String]
      } yield EnterContactDetails(field1, field2, field3)
    }

  implicit lazy val arbitraryRegisteredAddressForEori: Arbitrary[RegisteredAddressForEori] =
    Arbitrary {
      for {
        eori         <- arbitrary[String]
        businessName <- arbitrary[String]
        addressLine1 <- arbitrary[String]
        town         <- arbitrary[String]
        postcode     <- arbitrary[Option[String]]
        country      <- arbitrary[String]
      } yield RegisteredAddressForEori(eori, businessName, addressLine1, town, postcode, country)
    }

}
