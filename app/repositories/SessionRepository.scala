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

import com.mongodb.client.model.Indexes
import com.mongodb.client.model.Indexes.ascending
import models.cache.*
import org.bson.conversions.Bson
import org.mongodb.scala.SingleObservableFuture
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.{IndexModel, IndexOptions, ReplaceOptions}
import play.api.Configuration
import play.api.libs.json.OFormat
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SessionRepository @Inject() (config: Configuration, mongo: MongoComponent)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[DatedCacheMap](
      collectionName = config.get[String]("appName"),
      mongoComponent = mongo,
      domainFormat = DatedCacheMap.formats,
      indexes = Seq(
        IndexModel(
          ascending("lastUpdated"),
          IndexOptions()
            .name("userAnswersExpiry")
            .expireAfter(config.get[Int]("mongodb.timeToLiveInSeconds").toLong, TimeUnit.SECONDS)
        )
      )
    ) {

  private def byId(value: String): Bson = equal("id", value)

  def upsert(cm: CacheMap): Future[Boolean] =
    collection
      .replaceOne(byId(cm.id), DatedCacheMap(cm), ReplaceOptions().upsert(true))
      .toFuture()
      .map(_.wasAcknowledged())

  def get(id: String): Future[Option[CacheMap]] =
    collection
      .find(byId(id))
      .headOption()
      .map(cacheMapOpt => cacheMapOpt.map(cacheMap => CacheMap(cacheMap.id, cacheMap.data)))

  def extendTime(cm: CacheMap, expiry: Long): Future[Boolean] =
    collection
      .find(byId(cm.id))
      .headOption()
      .flatMap {
        case Some(cmo) =>
          val updatedExpiry = cmo.copy(lastUpdated = cmo.lastUpdated.plusSeconds(expiry))
          collection.replaceOne(byId(cm.id), updatedExpiry).toFuture().map(_.wasAcknowledged())
        case None => Future.successful(false)
      }

  def remove(cm: CacheMap): Future[Boolean] =
    collection.deleteOne(byId(cm.id)).toFuture().map(_.wasAcknowledged())

}
