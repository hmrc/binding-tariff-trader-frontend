/*
 * Copyright 2023 HM Revenue & Customs
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

package connectors

import java.util.concurrent.atomic.AtomicReference
import play.api.libs.json.Format
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.Future

object FakeDataCacheConnector extends FakeDataCacheConnector(Map.empty[String, CacheMap])

class FakeDataCacheConnector(initialData: Map[String, CacheMap]) extends DataCacheConnector {
  val cache = new AtomicReference(initialData)

  override def save[A](cacheMap: CacheMap): Future[CacheMap] = Future.successful {
    cache.updateAndGet(current => current.updated(cacheMap.id, cacheMap))

    cacheMap
  }

  override def fetch(cacheId: String): Future[Option[CacheMap]] =
    Future.successful(cache.get().get(cacheId))

  override def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]] =
    Future.successful(cache.get().get(cacheId).flatMap(_.getEntry(key)))

  override def remove(cacheMap: CacheMap): Future[Boolean] = Future.successful {
    cache.updateAndGet(current => current - cacheMap.id)

    true
  }
}
