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

package generators

import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

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
      } yield RegisteredAddressForEori(field1, field2)
    }

  implicit lazy val arbitraryregistered_address_for_eori: Arbitrary[registered_address_for_eori] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
        field2 <- arbitrary[String]
      } yield registered_address_for_eori(field1, field2)
    }

  implicit lazy val arbitraryregistered-address-for-eori: Arbitrary[registered-address-for-eori] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
        field2 <- arbitrary[String]
      } yield registered-address-for-eori(field1, field2)
    }
}
