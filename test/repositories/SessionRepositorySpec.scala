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

package repositories

import base.SpecBase
import models.cache.CacheMap
import org.mongodb.scala.SingleObservableFuture
import org.mongodb.scala.model.Filters
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.Span
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.test.MongoSupport

class SessionRepositorySpec extends SpecBase with ScalaFutures with BeforeAndAfterEach with MongoSupport {

  private val repository        = app.injector.instanceOf[SessionRepository]
  implicit val timeout: Timeout = Timeout(Span(5, org.scalatest.time.Seconds))

  override def beforeEach(): Unit = {
    super.beforeEach()

    repository.collection.deleteMany(Filters.empty()).toFuture().futureValue
  }

  "SessionRepository" should {

    "correctly handle upsert operations" in {
      val id       = "test-id"
      val data     = Map("map-test" -> Json.obj("key" -> "value"))
      val cacheMap = CacheMap(id, data)

      repository.upsert(cacheMap).futureValue shouldBe true

      val result = repository.get(id).futureValue
      result             shouldBe defined
      result.map(_.id)   shouldBe Some(id)
      result.map(_.data) shouldBe Some(data)
    }

    "update existing data when upsert is called with an existing id" in {
      val id          = "test-id"
      val initialData = Map("map-test" -> Json.obj("key" -> "value"))
      val updatedData = Map("map-test" -> Json.obj("key" -> "updated-value", "newKey" -> "new-value"))

      val initialCacheMap = CacheMap(id, initialData)
      val updatedCacheMap = CacheMap(id, updatedData)

      repository.upsert(initialCacheMap).futureValue
      repository.upsert(updatedCacheMap).futureValue shouldBe true

      val getResult = repository.get(id).futureValue
      getResult             shouldBe defined
      getResult.map(_.id)   shouldBe Some(id)
      getResult.map(_.data) shouldBe Some(updatedData)
    }

    "return None when getting a non-existent cache map" in {
      val id = "non-existent-id"

      repository.get(id).futureValue shouldBe None
    }

    "update the index for the expiry time of cache map if exists" in {
      val id         = "test-id"
      val data       = Map("map-test" -> Json.obj("key" -> "value"))
      val cacheMap   = CacheMap(id, data)
      val expiryTime = 10L

      repository.upsert(cacheMap)
      repository.extendTime(cacheMap, expiryTime).futureValue shouldBe true

      val getResult = repository.get(id).futureValue
      getResult           shouldBe defined
      getResult.map(_.id) shouldBe Some(id)
    }

    ".expiryTime should return None when getting a non-existent cache map" in {
      val id       = "test-id"
      val data     = Map("map-test" -> Json.obj("key" -> "value"))
      val cacheMap = CacheMap("id", data)

      val expiryTime = 10L

      repository.extendTime(cacheMap, expiryTime).futureValue shouldBe false

      repository.get(id).futureValue shouldBe None
    }

    "remove a cache map" in {
      val id       = "test-id"
      val data     = Map("map-test" -> Json.obj("key" -> "value"))
      val cacheMap = CacheMap(id, data)

      repository.upsert(cacheMap).futureValue
      repository.remove(cacheMap).futureValue shouldBe true
      repository.get(id).futureValue          shouldBe None
    }

    "handle removing a non-existent cache map" in {
      val id       = "non-existent-id"
      val cacheMap = CacheMap(id, Map("map-test" -> Json.obj()))

      repository.remove(cacheMap).futureValue shouldBe true // Always returns true even if nothing was removed
    }

    "handle multiple cache maps independently" in {
      val id1   = "test-id-1"
      val id2   = "test-id-2"
      val data1 = Map("map-test" -> Json.obj("key1" -> "value1"))
      val data2 = Map("map-test" -> Json.obj("key2" -> "value2"))

      val cacheMap1 = CacheMap(id1, data1)
      val cacheMap2 = CacheMap(id2, data2)

      repository.upsert(cacheMap1).futureValue
      repository.upsert(cacheMap2).futureValue

      val result1 = repository.get(id1).futureValue
      result1             shouldBe defined
      result1.map(_.data) shouldBe Some(data1)

      val result2 = repository.get(id2).futureValue
      result2             shouldBe defined
      result2.map(_.data) shouldBe Some(data2)

      repository.remove(cacheMap1).futureValue
      repository.get(id1).futureValue shouldBe None

      val stillExistsResult = repository.get(id2).futureValue
      stillExistsResult             shouldBe defined
      stillExistsResult.map(_.data) shouldBe Some(data2)
    }
  }
}
