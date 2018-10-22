package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryregistered_address_for_eoriUserAnswersEntry: Arbitrary[(registered_address_for_eoriPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[registered_address_for_eoriPage.type]
        value <- arbitrary[registered_address_for_eori].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryregistered-address-for-eoriUserAnswersEntry: Arbitrary[(registered-address-for-eoriPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[registered-address-for-eoriPage.type]
        value <- arbitrary[registered-address-for-eori].map(Json.toJson(_))
      } yield (page, value)
    }
}
