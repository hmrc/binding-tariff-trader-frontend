package generators

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryRegisteredAddressForEoriPage: Arbitrary[RegisteredAddressForEoriPage.type] =
    Arbitrary(RegisteredAddressForEoriPage)

  implicit lazy val arbitraryregistered_address_for_eoriPage: Arbitrary[registered_address_for_eoriPage.type] =
    Arbitrary(registered_address_for_eoriPage)

  implicit lazy val arbitraryregistered-address-for-eoriPage: Arbitrary[registered-address-for-eoriPage.type] =
    Arbitrary(registered-address-for-eoriPage)
}
