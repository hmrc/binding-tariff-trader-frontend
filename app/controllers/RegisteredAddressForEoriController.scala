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
import forms.RegisteredAddressForEoriFormProvider
import javax.inject.Inject
import models.{Mode, RegisteredAddressForEori, UserAnswers}
import navigation.Navigator
import pages.{EnterContactDetailsPage, RegisteredAddressForEoriPage}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.CountriesService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.registeredAddressForEori

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegisteredAddressForEoriController @Inject()(appConfig: FrontendAppConfig,
                                                   dataCacheConnector: DataCacheConnector,
                                                   navigator: Navigator,
                                                   identify: IdentifierAction,
                                                   getData: DataRetrievalAction,
                                                   formProvider: RegisteredAddressForEoriFormProvider,
                                                   countriesService: CountriesService,
                                                   cc: MessagesControllerComponents
                                                  ) extends FrontendController(cc) with I18nSupport {

  private lazy val form: Form[RegisteredAddressForEori] = formProvider()


  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) { implicit request =>

    val preparedForm = request.userAnswers.flatMap(_.get(RegisteredAddressForEoriPage)) match {
      case Some(value) if request.userEoriNumber.isDefined => form.fill(value.copy(eori = request.userEoriNumber.get))
      case None if request.userEoriNumber.isDefined => form.fill(RegisteredAddressForEori(request.userEoriNumber.get))
      case Some(value) => form.fill(value)
      case _ => form
    }

    Ok(registeredAddressForEori(appConfig, preparedForm, mode, countriesService.getAllCountries))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async { implicit request =>

    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        Future.successful(
          BadRequest(registeredAddressForEori(appConfig, formWithErrors, mode, countriesService.getAllCountries))),
      value => {
        val updatedAnswers = request.userAnswers.getOrElse(UserAnswers(request.internalId)).set(RegisteredAddressForEoriPage, value)

        dataCacheConnector.save(updatedAnswers.cacheMap).map(
          _ => Redirect(navigator.nextPage(RegisteredAddressForEoriPage, mode)(updatedAnswers))
        )
      }
    )
  }

}
