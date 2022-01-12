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

package controllers

import connectors.DataCacheConnector
import models.requests.DataRequest
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.QuestionPage
import play.api.libs.json.Writes
import play.api.mvc.{Result, Results}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scala.util.control.NonFatal

trait YesNoCaching extends AnswerCaching[Boolean] {
  def dataCacheConnector: DataCacheConnector
  def navigator: Navigator
  def detailPages: List[QuestionPage[_]]

  override def submitAnswer(answer: Boolean, mode: Mode)(implicit request: DataRequest[_], writes: Writes[Boolean], ec: ExecutionContext): Future[Result] = {
    // Set the yes/no question into the user answers
    val questionPageAnswers: UserAnswers = request.userAnswers.set(questionPage, answer)

    // If they have selected 'no' clear the subsequent details pages
    val updatedAnswers: UserAnswers = if (answer) {
      questionPageAnswers
    } else {
      detailPages.foldLeft(questionPageAnswers) {
        case (userAnswers, detailPage) => userAnswers.remove(detailPage)
      }
    }

    dataCacheConnector.save(updatedAnswers.cacheMap).transformWith {
      case Failure(NonFatal(_)) =>
        Future.successful(Results.BadGateway)
      case Success(_) =>
        Future.successful(Results.Redirect(navigator.nextPage(questionPage, mode)(updatedAnswers)))
    }
  }

}
