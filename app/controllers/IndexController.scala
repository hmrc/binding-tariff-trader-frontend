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

import config.FrontendAppConfig
import controllers.actions.IdentifierAction
import models.SortDirection.SortDirection
import models.SortField.SortField
import models._
import navigation.Navigator
import pages.IndexPage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.{BTAUserService, CasesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.Dashboard
import views.CaseDetailTab
import views.html.components.{table_applications, table_rulings}
import views.html.{account_dashboard_statuses, index}

import javax.inject.Inject
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class IndexController @Inject()(
                                 val appConfig: FrontendAppConfig,
                                 identify: IdentifierAction,
                                 navigator: Navigator,
                                 service: CasesService,
                                 cc: MessagesControllerComponents,
                                 btaUserService: BTAUserService,
                                 accountDashboardStatusesView: account_dashboard_statuses,
                                 indexView: index
)(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport with Logging {

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
          Ok(indexView(appConfig, CaseDetailTab.APPLICATION, table_applications(pagedResult)))
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
          Ok(indexView(appConfig, CaseDetailTab.RULING, table_rulings(pagedResult)))
        }

      case None =>
        val initialAnswers = UserAnswers(request.identifier)
        Future.successful(Redirect(navigator.nextPage(IndexPage, NormalMode)(initialAnswers)))
    }
  }

  def getApplicationsAndRulings(page: Int, sortBy: Option[SortField], order: Option[SortDirection]):
  Action[AnyContent] = identify.async { implicit request =>
    request.eoriNumber match {
      case Some(eori: String) =>
        val sort = Sort(sortBy.getOrElse(Dashboard.defaultSortField), order)
        (for {
          pagedResult <- service.getCases(eori, applicationStatuses, SearchPagination(page), sort)
          isBTAUser <-  btaUserService.isBTAUser(request.identifier)
        } yield {
            Ok(accountDashboardStatusesView(appConfig, Dashboard(pagedResult, sort), isBTAUser))
        }) recover {
          case NonFatal(error) =>
            logger.error("An error occurred whilst fetching data for dashboard view", error)
            Redirect(routes.ErrorController.onPageLoad)
        }
      case None =>
        val initialAnswers = UserAnswers(request.identifier)
        successful(Redirect(navigator.nextPage(IndexPage, NormalMode)(initialAnswers)))
    }
  }
}
