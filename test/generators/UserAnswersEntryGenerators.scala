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
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryLegalChallengeDetailsUserAnswersEntry
    : Arbitrary[(LegalChallengeDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LegalChallengeDetailsPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLegalChallengeUserAnswersEntry: Arbitrary[(LegalChallengePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LegalChallengePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCommodityCodeBestMatchUserAnswersEntry
    : Arbitrary[(CommodityCodeBestMatchPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CommodityCodeBestMatchPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCommodityCodeRulingReferenceUserAnswersEntry
    : Arbitrary[(CommodityCodeRulingReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CommodityCodeRulingReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySimilarItemCommodityCodeUserAnswersEntry
    : Arbitrary[(SimilarItemCommodityCodePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SimilarItemCommodityCodePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryReturnSamplesUserAnswersEntry: Arbitrary[(ReturnSamplesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ReturnSamplesPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouSendingSamplesUserAnswersEntry: Arbitrary[(AreYouSendingSamplesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouSendingSamplesPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCommodityCodeDigitsUserAnswersEntry: Arbitrary[(CommodityCodeDigitsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CommodityCodeDigitsPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryUploadSupportingMaterialMultipleUserAnswersEntry
    : Arbitrary[(UploadSupportingMaterialMultiplePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[UploadSupportingMaterialMultiplePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryProvideBTIReferenceUserAnswersEntry: Arbitrary[(ProvideBTIReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ProvideBTIReferencePage.type]
        value <- arbitrary[BTIReference].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEnterContactDetailsUserAnswersEntry: Arbitrary[(EnterContactDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EnterContactDetailsPage.type]
        value <- arbitrary[EnterContactDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRegisteredAddressForEoriUserAnswersEntry
    : Arbitrary[(RegisteredAddressForEoriPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RegisteredAddressForEoriPage.type]
        value <- arbitrary[RegisteredAddressForEori].map(Json.toJson(_))
      } yield (page, value)
    }

}
