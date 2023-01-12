/*
 * Copyright 2023 HM Revenue & Customs
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
import models.Confirmation
import pages.{ConfirmationPage, PdfViewPage}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import service.{BTAUserService, CountriesService, PdfService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.JsonFormatters._
import viewmodels.{ConfirmationUrlViewModel, PdfViewModel}
import views.html.confirmation

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class ConfirmationController @Inject() (
  appConfig: FrontendAppConfig,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  dataCacheConnector: DataCacheConnector,
  countriesService: CountriesService,
  pdfService: PdfService,
  btaUserService: BTAUserService,
  cc: MessagesControllerComponents,
  confirmationView: confirmation
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport
    with Logging {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    def show(c: Confirmation, pdf: PdfViewModel): Future[Result] =
      (for {
        isBTAUser <- btaUserService.isBTAUser(request.internalId)
        removed   <- dataCacheConnector.remove(request.userAnswers.cacheMap)
        _             = if (!removed) logger.warn("Session entry failed to be removed from the cache")
        token: String = pdfService.encodeToken(c.eori)
      } yield {
        Ok(
          confirmationView(appConfig, c, token, pdf, getCountryName, urlViewModel = ConfirmationUrlViewModel(isBTAUser))
        )
      }) recover {
        case NonFatal(error) =>
          logger.error("An error occurred whilst processing data for the confirmation view", error)
          Redirect(routes.ErrorController.onPageLoad)
      }

    (request.userAnswers.get(ConfirmationPage), request.userAnswers.get(PdfViewPage)) match {
      case (Some(c: Confirmation), Some(pdf: PdfViewModel)) => show(c, pdf)
      case _                                                => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad))
    }
  }

  def getCountryName(code: String): Option[String] =
    countriesService.getAllCountriesById
      .get(code)
      .map(_.countryName)
}
