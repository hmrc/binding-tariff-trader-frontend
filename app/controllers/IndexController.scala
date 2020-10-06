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

import config.FrontendAppConfig
import controllers.actions.IdentifierAction
import javax.inject.Inject
import models._
import navigation.Navigator
import pages.IndexPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.CasesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.CaseDetailTab
import views.html.components.{ table_applications, table_rulings }
import views.html.{ account_dashboard_statuses, index }

import scala.concurrent.{ ExecutionContext, Future }

class IndexController @Inject()(
  val appConfig: FrontendAppConfig,
  identify: IdentifierAction,
  navigator: Navigator,
  service: CasesService,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport {

  private val applicationStatuses = Set(
    CaseStatus.DRAFT, CaseStatus.NEW, CaseStatus.OPEN,
    CaseStatus.SUPPRESSED, CaseStatus.REFERRED, CaseStatus.REJECTED,
    CaseStatus.CANCELLED, CaseStatus.SUSPENDED, CaseStatus.COMPLETED
  )
  private val rulingStatuses = Set(
    CaseStatus.REJECTED, CaseStatus.SUSPENDED, CaseStatus.CANCELLED,
    CaseStatus.COMPLETED, CaseStatus.NEW, CaseStatus.OPEN, CaseStatus.REFERRED
  )

  def getApplications(page: Int): Action[AnyContent] = identify.async { implicit request =>
    request.eoriNumber match {
      case Some(eori: String) =>
        service.getCases(eori, applicationStatuses, SearchPagination(page), Sort()).map { pagedResult =>
          Ok(index(appConfig, CaseDetailTab.APPLICATION, table_applications(pagedResult)))
        }

      case None =>
        val initialAnswers = UserAnswers(request.identifier)
        Future.successful(Redirect(navigator.nextPage(IndexPage, NormalMode)(initialAnswers)))
    }
  }

  def getRulings(page: Int): Action[AnyContent] = identify.async { implicit request =>
    request.eoriNumber match {
      case Some(eori: String) =>
        service.getCases(eori, rulingStatuses, SearchPagination(page), Sort(SortField.DECISION_START_DATE)).map { pagedResult =>
          Ok(index(appConfig, CaseDetailTab.RULING, table_rulings(pagedResult)))
        }

      case None =>
        val initialAnswers = UserAnswers(request.identifier)
        Future.successful(Redirect(navigator.nextPage(IndexPage, NormalMode)(initialAnswers)))
    }
  }

  def getApplicationsAndRulings(page: Int): Action[AnyContent] = identify.async { implicit request =>
    request.eoriNumber match {
      case Some(eori: String) =>
        service.getCases(eori, applicationStatuses, SearchPagination(page), Sort()) flatMap { pagedResult =>
          Future.successful(Ok(account_dashboard_statuses(appConfig, pagedResult)))
        }

      case None =>
        val initialAnswers = UserAnswers(request.identifier)
        Future.successful(Redirect(navigator.nextPage(IndexPage, NormalMode)(initialAnswers)))
    }

  }
}
