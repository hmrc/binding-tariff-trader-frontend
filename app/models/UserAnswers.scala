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

package models

import pages._
import play.api.libs.json._
import uk.gov.hmrc.http.cache.client.CacheMap

case class UserAnswers(cacheMap: CacheMap) extends Enumerable.Implicits {

  def get[A](page: DataPage[A])(implicit rds: Reads[A]): Option[A] =
    cacheMap.getEntry[A](page.toString)

  def set[A](page: QuestionPage[A], value: A)(implicit writes: Writes[A]): UserAnswers =
    UserAnswers(cacheMap copy (data = cacheMap.data + (page.toString -> Json.toJson(value))))

  def set[A](page: DataPage[A], value: A)(implicit writes: Writes[A]): UserAnswers =
    UserAnswers(cacheMap copy (data = cacheMap.data + (page.toString -> Json.toJson(value))))

  def remove[A](page: QuestionPage[A]): UserAnswers =
    UserAnswers(cacheMap copy (data = cacheMap.data - page.toString))
}

object UserAnswers {

  def apply(cacheId: String): UserAnswers =
    UserAnswers(new CacheMap(cacheId, Map()))
}
