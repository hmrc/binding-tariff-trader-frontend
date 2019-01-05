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
import controllers.actions._
import javax.inject.Inject
import models._
import navigation.Navigator
import pages.InformationAboutYourItemPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.acceptItemInformationList

import scala.concurrent.Future.successful

class AcceptItemInformationListController @Inject()(appConfig: FrontendAppConfig,
                                                    override val messagesApi: MessagesApi,
                                                    navigator: Navigator,
                                                    identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction
                                                   ) extends FrontendController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    Ok(acceptItemInformationList(appConfig))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    successful(Redirect(navigator.nextPage(InformationAboutYourItemPage, NormalMode)(request.userAnswers.get)))
  }

}
