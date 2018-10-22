package generators

import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

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
