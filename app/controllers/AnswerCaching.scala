/*
 * Copyright 2020 HM Revenue & Customs
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

import connectors.DataCacheConnector
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.QuestionPage
import play.api.mvc.{ Result, Results }
import play.api.libs.json.Writes

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NonFatal
import scala.util.{ Success, Failure }

trait AnswerCaching[A] {
  def dataCacheConnector: DataCacheConnector
  def navigator: Navigator
  def questionPage: QuestionPage[A]

  def submitAnswer(answer: A, mode: Mode)(implicit request: DataRequest[_], writes: Writes[A], ec: ExecutionContext): Future[Result] = {
    val updatedAnswers = request.userAnswers.set(questionPage, answer)

    dataCacheConnector.save(updatedAnswers.cacheMap)
      .transformWith {
        case Failure(NonFatal(_)) =>
          Future.successful(Results.BadGateway)
        case Success(_) =>
          Future.successful(Results.Redirect(navigator.nextPage(questionPage, mode)(updatedAnswers)))
      }
  }
}