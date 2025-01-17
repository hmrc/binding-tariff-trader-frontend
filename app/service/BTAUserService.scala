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

import models.cache.CacheMap
import play.api.libs.json.Json

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BTAUserService @Inject() (dataCacheService: DataCacheService)(implicit ec: ExecutionContext) {
  private val isBTAUser = "isBTAUser"
  private val keyPrefix = "btaUser-"

  def save(requestId: String): Future[CacheMap] = {
    val cachedId = cacheMapId(requestId)
    dataCacheService.fetch(cachedId).flatMap {
      case Some(cacheMap) => Future.successful(cacheMap)
      case None           => dataCacheService.save(new CacheMap(cachedId, Map(isBTAUser -> Json.toJson(true))))
    }
  }

  def isBTAUser(requestId: String): Future[Boolean] =
    dataCacheService.fetch(cacheMapId(requestId)).map {
      case Some(cacheMap) =>
        cacheMap.getEntry[Boolean](isBTAUser).getOrElse(false)
      case None =>
        false
    }

  def remove(requestId: String): Future[Boolean] =
    for {
      maybeCacheMap   <- dataCacheService.fetch(cacheMapId(requestId))
      cacheMapRemoved <- maybeCacheMap.fold(Future.successful(false))(dataCacheService.remove)
    } yield cacheMapRemoved

  private def cacheMapId(requestId: String): String = s"$keyPrefix$requestId"
}
