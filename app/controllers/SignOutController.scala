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
import controllers.actions.{DataRetrievalAction, IdentifierAction}
import models.requests.OptionalDataRequest
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Results}
import service.BTAUserService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.Future.successful

class SignOutController @Inject() (
  val appConfig: FrontendAppConfig,
  dataCacheConnector: DataCacheConnector,
  urlCacheService: BTAUserService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport {

  def startFeedbackSurvey: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    clearDataCache(request)
    successful(SeeOther(appConfig.feedbackSurvey).withNewSession)
  }

  def forceSignOut: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    clearDataCache(request)
    urlCacheService.remove(request.internalId).flatMap { _ =>
      successful(Results.Redirect(routes.SessionExpiredController.onPageLoad).withNewSession)
    }
  }

  def unauthorisedSignOut: Action[AnyContent] = Action {
    Redirect(routes.IndexController.getApplications()).withNewSession
  }

  def keepAlive(): Action[AnyContent] = Action.async {
    successful(Ok("OK"))
  }

  def cancelApplication: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    clearDataCache(request)
    successful(Results.Redirect(routes.IndexController.getApplicationsAndRulings(sortBy = None, order = None)))
  }

  private def clearDataCache(request: OptionalDataRequest[AnyContent]): Option[Future[Boolean]] =
    request.userAnswers map { answer => dataCacheConnector.remove(answer.cacheMap) }
}
