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

package controllers

import models.Mode
import models.requests.DataRequest
import play.api.libs.json.Format
import play.api.mvc.{Result, Results}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait ListAnswerEditing[A] extends AccumulatingAnswerEditing[List[A], A, Int] {
  val cbf: mutable.Builder[A, List[A]] = List.newBuilder[A]
  def editIndex(list: List[A], index: Int, elem: A): List[A] =
    list.take(index) ++ List(elem) ++ list.drop(index + 1)
}

trait AccumulatingAnswerEditing[F <: IterableOnce[A], A, I] extends AccumulatingAnswerCaching[F, A] {

  def editIndex(f: F, index: I, elem: A): F

  def editAnswer(
    index: I,
    answer: A,
    mode: Mode
  )(implicit request: DataRequest[?], writes: Format[F], ec: ExecutionContext): Future[Result] =
    request.userAnswers
      .get(questionPage)
      .map { fa =>
        val updatedAnswers = request.userAnswers.set(questionPage, editIndex(fa, index, answer))

        dataCacheService
          .save(updatedAnswers.cacheMap)
          .transformWith {
            case Failure(_) =>
              Future.successful(Results.BadGateway)
            case Success(_) =>
              Future.successful(Results.Redirect(navigator.nextPage(questionPage, mode)(updatedAnswers)))
          }
      }
      .getOrElse {
        Future.successful(Results.BadRequest)
      }
}
