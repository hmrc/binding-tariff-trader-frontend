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

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.ConfidentialInformationFormProvider
import models.Mode
import pages.{ConfidentialInformationPage, DescribeYourItemPage}
import navigation.Navigator
import play.api.mvc.{Action, AnyContent}
import views.html.confidentialInformation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfidentialInformationController @Inject()(appConfig: FrontendAppConfig,
                                      override val messagesApi: MessagesApi,
                                      dataCacheConnector: DataCacheConnector,
                                      navigator: Navigator,
                                      identify: IdentifierAction,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction,
                                      formProvider: ConfidentialInformationFormProvider
                                      ) extends FrontendController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(ConfidentialInformationPage) match {
      case None => form
      case Some(value) => form.fill(value)
    }

    Ok(confidentialInformation(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        Future.successful(BadRequest(confidentialInformation(appConfig, formWithErrors, mode))),
      value => {
        val updatedAnswers = request.userAnswers.set(ConfidentialInformationPage, value)

        dataCacheConnector.save(updatedAnswers.cacheMap).map(
          _ =>
            Redirect(navigator.nextPage(DescribeYourItemPage, mode)(updatedAnswers))
        )
      }
    )
  }

}
