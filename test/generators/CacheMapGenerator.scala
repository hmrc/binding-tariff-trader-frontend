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

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import pages._
import play.api.libs.json.JsValue
import models.cache.CacheMap

trait CacheMapGenerator {
  self: Generators =>

  val generators: Seq[Gen[(Page, JsValue)]] =
    arbitrary[(LegalChallengeDetailsPage.type, JsValue)] ::
      arbitrary[(LegalChallengePage.type, JsValue)] ::
      arbitrary[(CommodityCodeRulingReferencePage.type, JsValue)] ::
      arbitrary[(SimilarItemCommodityCodePage.type, JsValue)] ::
      arbitrary[(ReturnSamplesPage.type, JsValue)] ::
      arbitrary[(AreYouSendingSamplesPage.type, JsValue)] ::
      arbitrary[(CommodityCodeDigitsPage.type, JsValue)] ::
      arbitrary[(CommodityCodeBestMatchPage.type, JsValue)] ::
      arbitrary[(UploadSupportingMaterialMultiplePage.type, JsValue)] ::
      arbitrary[(ProvideBTIReferencePage.type, JsValue)] ::
      arbitrary[(EnterContactDetailsPage.type, JsValue)] ::
      arbitrary[(RegisteredAddressForEoriPage.type, JsValue)] ::
      Nil

  implicit lazy val arbitraryCacheMap: Arbitrary[CacheMap] =
    Arbitrary {
      for {
        cacheId <- nonEmptyString
        data <- generators match {
                  case Nil => Gen.const(Map[Page, JsValue]())
                  case _   => Gen.mapOf(oneOf(generators))
                }
      } yield CacheMap(
        cacheId,
        data.map { case (k, v) =>
          (k.toString, v)
        }
      )
    }
}
