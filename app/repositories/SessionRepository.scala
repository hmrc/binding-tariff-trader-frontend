/*
 * Copyright 2021 HM Revenue & Customs
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

import com.mongodb.client.model.Indexes.ascending
import org.bson.conversions.Bson
import org.joda.time.{DateTime, DateTimeZone}
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.{IndexModel, IndexOptions, ReplaceOptions}
import play.api.Configuration
import play.api.libs.json.{Format, JsValue, Json, OFormat}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJodaFormats

import java.util.concurrent.TimeUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


case class DatedCacheMap(id: String,
                         data: Map[String, JsValue],
                         lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC))

object DatedCacheMap {
  implicit val dateFormat: Format[DateTime] = MongoJodaFormats.dateTimeFormat
  implicit val formats: OFormat[DatedCacheMap] = Json.format[DatedCacheMap]

  def apply(cacheMap: CacheMap): DatedCacheMap = DatedCacheMap(cacheMap.id, cacheMap.data)
}

class SessionRepository @Inject()(config: Configuration, mongo: MongoComponent)(implicit ec: ExecutionContext)
  extends PlayMongoRepository[DatedCacheMap](
    collectionName = config.get[String]("appName"),
    mongoComponent = mongo,
    domainFormat = DatedCacheMap.formats,
    indexes = Seq(
      IndexModel(ascending("lastUpdated"),
        IndexOptions().name("userAnswersExpiry")
          .expireAfter(config.get[Int]("mongodb.timeToLiveInSeconds"), TimeUnit.SECONDS))
    )) {

  def upsert(cm: CacheMap): Future[Boolean] = {
    collection.replaceOne(byId(cm.id), DatedCacheMap(cm), ReplaceOptions().upsert(true)).toFuture().map(_.wasAcknowledged())
  }

  def get(id: String): Future[Option[CacheMap]] = {
    collection.find(byId(id)).first().toFutureOption()
      .map(_.fold(None: Option[CacheMap]){ cacheMap => Some(CacheMap(cacheMap.id, cacheMap.data))})
  }

  def remove(cm: CacheMap): Future[Boolean] = {
    collection.deleteOne(byId(cm.id)).toFuture().map(_.wasAcknowledged())
  }

  private def byId(value: String): Bson = {
    equal("id", value)
  }
}

