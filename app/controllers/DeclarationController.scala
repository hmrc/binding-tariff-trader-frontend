/*
 * Copyright 2018 HM Revenue & Customs
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
import mapper.CaseRequestMapper
import models.Confirmation.format
import models._
import navigation.Navigator
import pages.ConfirmationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import service.CasesService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.declaration

import scala.concurrent.ExecutionContext.Implicits.global

class DeclarationController @Inject()(
                                       appConfig: FrontendAppConfig,
                                       override val messagesApi: MessagesApi,
                                       dataCacheConnector: DataCacheConnector,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       service: CasesService,
                                       mapper: CaseRequestMapper
                                     ) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) { implicit request =>
    Ok(declaration(appConfig, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val answers: UserAnswers = request.userAnswers.get

    service
      .createCase(mapper.map(answers))
      .map(c => answers.set(ConfirmationPage, Confirmation(c.reference)))
      .flatMap(updatedAnswers => {
        dataCacheConnector.save(updatedAnswers.cacheMap)
          .map(_ => Redirect(navigator.nextPage(ConfirmationPage, mode)(updatedAnswers)))
      })
  }

}
