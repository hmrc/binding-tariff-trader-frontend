/*
 * Copyright 2023 HM Revenue & Customs
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

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import javax.inject.Inject
import models.UserAnswers
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Results}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.beforeYouStart

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class BeforeYouStartController @Inject() (
  appConfig: FrontendAppConfig,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  dataCacheConnector: DataCacheConnector,
  cc: MessagesControllerComponents,
  beforeYouStartPageView: beforeYouStart
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val initialAnswers = request.userAnswers.getOrElse(UserAnswers(request.internalId))
    dataCacheConnector.save(initialAnswers.cacheMap).transformWith {
      case Success(_) =>
        Future.successful(Ok(beforeYouStartPageView(appConfig)))
      case Failure(_) =>
        Future.successful(Results.BadGateway)
    }
  }
}
