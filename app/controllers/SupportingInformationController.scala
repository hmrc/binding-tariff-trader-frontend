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
import connectors.DataCacheConnector
import controllers.actions._
import forms.SupportingInformationFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.supportingInformation

import scala.concurrent.Future

class SupportingInformationController @Inject()(
                                                 appConfig: FrontendAppConfig,
                                                 override val messagesApi: MessagesApi,
                                                 override val dataCacheConnector: DataCacheConnector,
                                                 override val navigator: Navigator,
                                                 identify: IdentifierAction,
                                                 getData: DataRetrievalAction,
                                                 requireData: DataRequiredAction,
                                                 formProvider: SupportingInformationFormProvider
                                               ) extends FrontendController with I18nSupport with YesNoBehaviour[String] {

  private lazy val form = formProvider()

  override val page: QuestionPage[Boolean] = SupportingInformationPage
  override val pageDetails: QuestionPage[String] = SupportingInformationDetailsPage
  override val nextPage: Page = CheckYourAnswersPage

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(SupportingInformationPage) match {
      case Some(value) => form.fill(value)
      case _ => form
    }

    Ok(supportingInformation(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    def badRequest = { formWithErrors: Form[_] =>
      Future.successful(BadRequest(supportingInformation(appConfig, formWithErrors, mode)))
    }

    form.bindFromRequest().fold(badRequest, submitAnswer(_, mode))
  }

}
