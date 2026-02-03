/*
 * Copyright 2026 HM Revenue & Customs
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

package service

import connectors.ConnectorTest
import generators.Generators
import models.cache.CacheMap
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.Mockito.{mock, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.libs.json.JsString
import repositories.SessionRepository
import uk.gov.hmrc.mongo.test.MongoSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoCacheServiceSpec
    extends ConnectorTest
    with ScalaCheckDrivenPropertyChecks
    with Generators
    with ScalaFutures
    with MongoSupport {

  val repository: SessionRepository = mock(classOf[SessionRepository])

  ".save" must {

    "save the cache map to the Mongo repository" in {

      when(repository.upsert(any[CacheMap])) `thenReturn` Future.successful(true)

      val mongoCacheService: MongoCacheService = new MongoCacheService(repository, metrics)

      forAll(arbitrary[CacheMap]) { cacheMap =>
        val result = mongoCacheService.save(cacheMap)

        whenReady(result) { savedCacheMap =>
          savedCacheMap shouldEqual cacheMap
          verify(repository).upsert(cacheMap)
        }

      }

    }

  }

  ".keepAlive" must {

    "keep the cache map for the length of 2 hours in the Mongo repository" in {

      when(repository.extendTime(any[CacheMap], ArgumentMatchers.eq(7200L))).thenReturn(Future.successful(true))

      val mongoCacheService: MongoCacheService = new MongoCacheService(repository, metrics)

      forAll(arbitrary[CacheMap]) { cacheMap =>
        val result = mongoCacheService.keepAlive(cacheMap, 7200L)

        whenReady(result) { savedCacheMap =>
          savedCacheMap shouldEqual cacheMap
          verify(repository).extendTime(cacheMap, 7200L)
        }
      }
    }
  }

  ".remove" must {

    "remove the cache map to the Mongo repository" in {

      when(repository.remove(any[CacheMap])) `thenReturn` Future.successful(true)

      val mongoCacheService: MongoCacheService = new MongoCacheService(repository, metrics)

      forAll(arbitrary[CacheMap]) { cacheMap =>
        val result = mongoCacheService.remove(cacheMap)

        whenReady(result) { savedCacheMap =>
          savedCacheMap shouldEqual true
          verify(repository).remove(cacheMap)
        }
      }

    }

  }

  ".fetch" when {

    "there isn't a record for this key in Mongo" must {

      "return None" in {

        when(repository.get(any[String])) `thenReturn` Future.successful(None)

        val mongoCacheService: MongoCacheService = new MongoCacheService(repository, metrics)

        forAll(nonEmptyString) { cacheId =>
          val result = mongoCacheService.fetch(cacheId)

          whenReady(result)(optionalCacheMap => optionalCacheMap should be(empty))
        }

      }

    }

    "a record exists for this key" must {

      "return the record" in {

        val mongoCacheService: MongoCacheService = new MongoCacheService(repository, metrics)

        forAll(arbitrary[CacheMap]) { cacheMap =>
          when(repository.get(refEq(cacheMap.id))) `thenReturn` Future.successful(Some(cacheMap))

          val result = mongoCacheService.fetch(cacheMap.id)

          whenReady(result)(optionalCacheMap => optionalCacheMap shouldBe Some(cacheMap))
        }

      }

    }

  }

  ".getEntry" when {

    "there isn't a record for this key in Mongo" must {

      "return None" in {

        when(repository.upsert(any[CacheMap])) `thenReturn` Future.successful(false)

        val mongoCacheService: MongoCacheService = new MongoCacheService(repository, metrics)

        forAll(nonEmptyString, nonEmptyString) { (cacheId, key) =>
          val result = mongoCacheService.getEntry[String](cacheId, key)

          whenReady(result)(optionalValue => optionalValue should be(empty))
        }

      }

    }

    "a record exists in Mongo but this key is not present" must {

      "return None" in {

        when(repository.upsert(any[CacheMap])) `thenReturn` Future.successful(false)

        val mongoCacheService: MongoCacheService = new MongoCacheService(repository, metrics)

        val gen = for {
          key      <- nonEmptyString
          cacheMap <- arbitrary[CacheMap]
        } yield (key, cacheMap `copy` (data = cacheMap.data - key))

        forAll(gen) { case (k, cacheMap) =>
          when(repository.get(refEq(cacheMap.id))) `thenReturn` Future.successful(Some(cacheMap))

          val result = mongoCacheService.getEntry[String](cacheMap.id, k)

          whenReady(result)(optionalValue => optionalValue should be(empty))
        }

      }

    }

    "a record exists in Mongo with this key" must {

      "return the key's value" in {

        val mongoCacheService: MongoCacheService = new MongoCacheService(repository, metrics)

        val gen = for {
          key      <- nonEmptyString
          value    <- nonEmptyString
          cacheMap <- arbitrary[CacheMap]
        } yield (key, value, cacheMap `copy` (data = cacheMap.data + (key -> JsString(value))))

        forAll(gen) { case (k, v, cacheMap) =>
          when(repository.get(refEq(cacheMap.id))) `thenReturn` Future.successful(Some(cacheMap))

          val result = mongoCacheService.getEntry[String](cacheMap.id, k)

          whenReady(result)(optionalValue => optionalValue shouldBe Some(v))
        }

      }

    }

  }

}
