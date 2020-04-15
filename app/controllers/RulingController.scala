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
import models.Case
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.CasesService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.ruling_information

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful

class RulingController @Inject()(val appConfig: FrontendAppConfig,
                                 identify: IdentifierAction,
                                 service: CasesService,
                                 cc: MessagesControllerComponents) extends FrontendController(cc) with I18nSupport {

  def viewRuling(reference: String): Action[AnyContent] = identify.async { implicit request =>

    request.eoriNumber match {
      case Some(eori: String) =>
        service.getCaseForUser(eori, reference) flatMap {
          c: Case => successful(Ok(ruling_information(appConfig, c)))
        }

      case None => successful(Redirect(routes.BeforeYouStartController.onPageLoad()))
    }

  }
}
