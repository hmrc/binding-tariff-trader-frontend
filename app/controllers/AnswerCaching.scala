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

package controllers

import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.QuestionPage
import play.api.libs.json.{Format, Writes}
import play.api.mvc.{Result, Results}
import service.DataCacheService

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait MapAnswerCaching[K, V] extends AccumulatingAnswerCaching[Map[K, V], (K, V)] {
  val cbf: mutable.Builder[(K, V), Map[K, V]] = Map.newBuilder[K, V]
}

trait AccumulatingAnswerCaching[F <: IterableOnce[A], A] {
  def cbf: mutable.Builder[A, F]
  def dataCacheService: DataCacheService
  def navigator: Navigator
  def questionPage: QuestionPage[F]

  def submitAnswer(
    answer: A,
    mode: Mode
  )(implicit request: DataRequest[?], writes: Format[F], ec: ExecutionContext): Future[Result] = {
    val withNewAnswer = request.userAnswers
      .get(questionPage)
      .map { fa =>
        val builder = cbf
        builder.clear()
        builder.addAll(fa)
        builder.addOne(answer)
        builder.result()
      }
      .getOrElse {
        val builder = cbf
        builder.clear()
        builder.addOne(answer)
        builder += answer
        builder.result()
      }

    val updatedAnswers = request.userAnswers.set(questionPage, withNewAnswer)

    dataCacheService
      .save(updatedAnswers.cacheMap)
      .transformWith {
        case Failure(_) =>
          Future.successful(Results.BadGateway)
        case Success(_) =>
          Future.successful(Results.Redirect(navigator.nextPage(questionPage, mode)(updatedAnswers)))
      }
  }
}

trait AnswerCaching[A] {
  def dataCacheService: DataCacheService
  def navigator: Navigator
  def questionPage: QuestionPage[A]

  def submitAnswer(
    answer: A,
    mode: Mode
  )(implicit request: DataRequest[?], writes: Writes[A], ec: ExecutionContext): Future[Result] = {
    val updatedAnswers = request.userAnswers.set(questionPage, answer)

    dataCacheService
      .save(updatedAnswers.cacheMap)
      .transformWith {
        case Failure(_) =>
          Future.successful(Results.BadGateway)
        case Success(_) =>
          Future.successful(Results.Redirect(navigator.nextPage(questionPage, mode)(updatedAnswers)))
      }
  }
}
