/*
 * Copyright 2019 HM Revenue & Customs
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
import pages.{Page, QuestionPage}
import play.api.i18n.I18nSupport
import play.api.mvc.{Result, Results}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class YesNoController[A](dataCacheConnector: DataCacheConnector, navigator: Navigator)
  extends FrontendController with I18nSupport {

  protected val page: QuestionPage[Boolean]
  protected val pageDetails: QuestionPage[A]
  protected val nextPage: Page

  def applyAnswer(value: Boolean, mode: Mode)
                 (implicit request: DataRequest[_]): Future[Result] =
    value match {
      case true =>

        val updatedAnswers = request.userAnswers.set[Boolean](page, value)
        dataCacheConnector.save(updatedAnswers.cacheMap).map(
          _ => Results.Redirect(navigator.nextPage(pageDetails, mode)(updatedAnswers))
        )

      case false =>

        val updatedAnswers = request.userAnswers.set(page, value).remove(pageDetails)
        dataCacheConnector.save(updatedAnswers.cacheMap).map(
          _ => Results.Redirect(navigator.nextPage(nextPage, mode)(updatedAnswers))
        )
    }
}
