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

package service

import com.codahale.metrics.MetricRegistry
import metrics.HasMetrics
import models.cache.CacheMap
import play.api.libs.json.Format
import repositories.SessionRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MongoCacheService @Inject() (
  val sessionRepository: SessionRepository,
  val metrics: MetricRegistry
)(implicit ec: ExecutionContext)
    extends DataCacheService
    with HasMetrics {

  def save[A](cacheMap: CacheMap): Future[CacheMap] =
    withMetricsTimerAsync("mongo-cache-save")(_ => sessionRepository.upsert(cacheMap).map(_ => cacheMap))

  def fetch(cacheId: String): Future[Option[CacheMap]] =
    withMetricsTimerAsync("mongo-cache-fetch")(_ => sessionRepository.get(cacheId))

  def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]] =
    withMetricsTimerAsync("mongo-cache-get-entry") { _ =>
      fetch(cacheId).map(optionalCacheMap => optionalCacheMap.flatMap(cacheMap => cacheMap.getEntry(key)))
    }

  def remove(cacheMap: CacheMap): Future[Boolean] =
    withMetricsTimerAsync("mongo-cache-remove")(_ => sessionRepository.remove(cacheMap))
}

trait DataCacheService {

  def save[A](cacheMap: CacheMap): Future[CacheMap]

  def fetch(cacheId: String): Future[Option[CacheMap]]

  def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]]

  def remove(cacheMap: CacheMap): Future[Boolean]

}
