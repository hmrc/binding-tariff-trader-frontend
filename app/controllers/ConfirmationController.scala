/*
 * Copyright 2022 HM Revenue & Customs
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
import models.Confirmation
import pages.{ConfirmationPage, PdfViewPage}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import service.{CountriesService, PdfService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.JsonFormatters._
import viewmodels.PdfViewModel
import views.html.confirmation

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        dataCacheConnector: DataCacheConnector,
                                        countriesService: CountriesService,
                                        pdfService: PdfService,
                                        cc: MessagesControllerComponents,
                                        confirmationView: confirmation
                                      )(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport with Logging {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    def show(c: Confirmation, pdf: PdfViewModel): Future[Result] = for {
      removed <- dataCacheConnector.remove(request.userAnswers.cacheMap)
      _ = if (!removed) logger.warn("Session entry failed to be removed from the cache")
      token: String = pdfService.encodeToken(c.eori)
    } yield Ok(confirmationView(appConfig, c, token, pdf, getCountryName))

    (request.userAnswers.get(ConfirmationPage), request.userAnswers.get(PdfViewPage)) match {
      case (Some(c: Confirmation), Some(pdf: PdfViewModel)) => show(c, pdf)
      case _ => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
  }

  def getCountryName(code: String): Option[String] =
    countriesService
      .getAllCountriesById
      .get(code)
      .map(_.countryName)
}
