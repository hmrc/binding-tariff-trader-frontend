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
import models.{Pagination, SearchPagination}
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

  def loadApplications(page : Int): Action[AnyContent] = identify.async { implicit request =>

    service.findApplicationsBy(request.eoriNumber, SearchPagination(page)) flatMap { pagedResult =>
      successful(Ok(index(appConfig, CaseDetailTab.APPLICATION, table_applications(pagedResult))))
    }
  }

  def loadRulings(page : Int): Action[AnyContent] = identify.async { implicit request =>

    service.findRulingsBy(request.eoriNumber, SearchPagination(page)) flatMap { pagedResult =>
      successful(Ok(index(appConfig, CaseDetailTab.RULING, table_rulings(pagedResult) )))
    }
  }



}
