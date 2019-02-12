/*
 * Copyright 2019 HM Revenue & Customs
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

import com.google.inject.Inject
import play.api.libs.json.Format
import repositories.SessionRepository
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoCacheConnector @Inject()(val sessionRepository: SessionRepository) extends DataCacheConnector {

  def save[A](cacheMap: CacheMap): Future[CacheMap] = {
    sessionRepository().upsert(cacheMap).map {_ => cacheMap}
  }

  def fetch(cacheId: String): Future[Option[CacheMap]] =
    sessionRepository().get(cacheId)

  def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]] = {
    fetch(cacheId).map { optionalCacheMap =>
      optionalCacheMap.flatMap { cacheMap => cacheMap.getEntry(key)}
    }
  }

  def remove(cacheMap: CacheMap): Future[Boolean] = {
    sessionRepository().remove(cacheMap)
  }

}

trait DataCacheConnector {

  def save[A](cacheMap: CacheMap): Future[CacheMap]

  def fetch(cacheId: String): Future[Option[CacheMap]]

  def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]]

  def remove(cacheMap: CacheMap): Future[Boolean]

}
