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
import forms.AddConfidentialInformationFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.AddConfidentialInformationPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.addConfidentialInformation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AddConfidentialInformationController @Inject()(appConfig: FrontendAppConfig,
                                                     dataCacheConnector: DataCacheConnector,
                                                     navigator: Navigator,
                                                     identify: IdentifierAction,
                                                     getData: DataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     formProvider: AddConfidentialInformationFormProvider,
                                                     cc: MessagesControllerComponents
                                                    ) extends FrontendController(cc) with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode) = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AddConfidentialInformationPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(addConfidentialInformation(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(addConfidentialInformation(appConfig, formWithErrors, mode))),
        (value) => {
          val updatedAnswers = request.userAnswers.set(AddConfidentialInformationPage, value)

          dataCacheConnector.save(updatedAnswers.cacheMap).map(
            _ =>
              Redirect(navigator.nextPage(AddConfidentialInformationPage, mode)(updatedAnswers))
          )
        }
      )
  }
}
