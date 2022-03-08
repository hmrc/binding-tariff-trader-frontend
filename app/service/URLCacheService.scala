/*
 * Copyright 2022 HM Revenue & Customs
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

import connectors.DataCacheConnector
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.CacheMap
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class URLCacheService @Inject()(dataCacheConnector: DataCacheConnector)(implicit ec: ExecutionContext) {
  private val btaCallBackURLKey = "btaCallBackURLKey"
  private val keyPrefix = "url-"

  def saveBTACallbackURL(requestId: String, btaUrl: String): Future[CacheMap] = {
    val cachedId = cacheMapId(requestId)
    dataCacheConnector.fetch(cachedId).flatMap {
      case Some(cacheMap) => Future.successful(cacheMap)
      case None => dataCacheConnector.save(new CacheMap(cachedId, Map(btaCallBackURLKey -> Json.toJson(btaUrl))))
    }
  }

  def fetchBTACallbackURL(requestId: String): Future[Option[String]] = {
    dataCacheConnector.fetch(cacheMapId(requestId)).map(_.flatMap(_.getEntry[String](btaCallBackURLKey)))
  }

  def fetchBTACallbackURLWithDelete(requestId: String): Future[Option[String]] = {
    for{
      maybeCacheMap <- dataCacheConnector.fetch(cacheMapId(requestId))
      maybeBTAUrl = maybeCacheMap.flatMap(_.getEntry[String](btaCallBackURLKey))
      _ = maybeCacheMap.map(dataCacheConnector.remove)
    } yield maybeBTAUrl
  }

  def removeBTACallbackURL(requestId: String): Future[Boolean] = {
    for{
      maybeCacheMap <- dataCacheConnector.fetch(cacheMapId(requestId))
      cacheMapRemoved <- maybeCacheMap.fold(Future.successful(false))(dataCacheConnector.remove)
    } yield cacheMapRemoved
  }

  private def cacheMapId(requestId: String): String = s"$keyPrefix$requestId"
}
