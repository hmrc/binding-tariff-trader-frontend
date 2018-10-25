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
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryRegisteredAddressForEoriUserAnswersEntry: Arbitrary[(RegisteredAddressForEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RegisteredAddressForEoriPage.type]
        value <- arbitrary[RegisteredAddressForEori].map(Json.toJson(_))
      } yield (page, value)
    }

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
