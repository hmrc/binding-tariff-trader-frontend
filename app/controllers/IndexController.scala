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

import config.FrontendAppConfig
import controllers.actions.IdentifierAction
import javax.inject.Inject
import models.{CaseStatus, SearchPagination, Sort, SortField}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import service.CasesService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.CaseDetailTab
import views.html.components.{table_applications, table_rulings}
import views.html.index

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful

class IndexController @Inject()(val appConfig: FrontendAppConfig,
                                identify: IdentifierAction,
                                service: CasesService,
                                val messagesApi: MessagesApi) extends FrontendController with I18nSupport {


  private val applicationStatuses = Set(CaseStatus.DRAFT, CaseStatus.NEW, CaseStatus.OPEN,
    CaseStatus.SUPPRESSED, CaseStatus.REFERRED, CaseStatus.REJECTED,
    CaseStatus.CANCELLED, CaseStatus.SUSPENDED, CaseStatus.COMPLETED)

  def getApplications(page: Int): Action[AnyContent] = identify.async { implicit request =>

    service.findCases(request.eoriNumber, applicationStatuses, SearchPagination(page), Sort()) flatMap { pagedResult =>
      successful(Ok(index(appConfig, CaseDetailTab.APPLICATION, table_applications(pagedResult))))
    }
  }


  private val rulingStatuses = Set(CaseStatus.CANCELLED, CaseStatus.COMPLETED)

  def getRulings(page: Int): Action[AnyContent] = identify.async { implicit request =>
    service.findCases(request.eoriNumber, rulingStatuses, SearchPagination(page), Sort(SortField.DECISION_START_DATE)) flatMap { pagedResult =>
      successful(Ok(index(appConfig, CaseDetailTab.RULING, table_rulings(pagedResult))))
    }
  }


}
