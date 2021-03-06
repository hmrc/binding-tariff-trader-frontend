/*
 * Copyright 2021 HM Revenue & Customs
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
import models.{ Mode, RegisteredAddressForEori }
import models.requests.DataRequest
import navigation.Navigator
import pages.RegisteredAddressForEoriPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{ Action, AnyContent, MessagesControllerComponents }
import play.twirl.api.HtmlFormat
import service.CountriesService
import views.html.registeredAddressForEori

import scala.concurrent.ExecutionContext

class RegisteredAddressForEoriController @Inject()(appConfig: FrontendAppConfig,
  val dataCacheConnector: DataCacheConnector,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  formProvider: RegisteredAddressForEoriFormProvider,
  countriesService: CountriesService,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext) extends AnswerCachingController[RegisteredAddressForEori](cc) with I18nSupport {

  lazy val form: Form[RegisteredAddressForEori] = formProvider()
  val questionPage = RegisteredAddressForEoriPage

  def renderView(preparedForm: Form[RegisteredAddressForEori], mode: Mode)(implicit request: DataRequest[_]): HtmlFormat.Appendable =
    registeredAddressForEori(appConfig, preparedForm, mode, countriesService.getAllCountries)

  override def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = (request.userAnswers.get(RegisteredAddressForEoriPage), request.eoriNumber) match {
      case (Some(value), Some(eoriNumber)) => form.fill(value.copy(eori = eoriNumber))
      case (None,        Some(eoriNumber)) => form.fill(RegisteredAddressForEori(eoriNumber))
      case (Some(value), _               ) => form.fill(value)
      case _ => form
    }

    Ok(renderView(preparedForm, mode))
  }
}
