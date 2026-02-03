/*
 * Copyright 2026 HM Revenue & Customs
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
import models.requests.DataRequest
import pages._
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.JsonFormatters._
import viewmodels.ConfirmationUrlViewModel
import views.html.confirmation

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject() (
  appConfig: FrontendAppConfig,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  dataCacheService: DataCacheService,
  countriesService: CountriesService,
  pdfService: PdfService,
  btaUserService: BTAUserService,
  userAnswerDeletionService: UserAnswerDeletionService,
  cc: MessagesControllerComponents,
  confirmationView: confirmation
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport
    with Logging {

  private def getCountryName(code: String): Option[String] =
    countriesService.getAllCountriesById
      .get(code)
      .map(_.countryName)

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    (request.userAnswers.get(ConfirmationPage), request.userAnswers.get(PdfViewPage)) match {
      case (Some(confirmation), Some(pdf)) =>
        val excludedPages: Seq[DataPage[?]] =
          Seq(
            ConfirmationPage,
            PdfViewPage
          )

        (
          for {
            isBTAUser <- btaUserService.isBTAUser(request.internalId)
            updatedUA <- Future.successful(
                           userAnswerDeletionService.deleteAllUserAnswersExcept(request.userAnswers, excludedPages)
                         )
            _ <- dataCacheService.save(updatedUA.cacheMap)
            token = pdfService.encodeToken(confirmation.eori)
          } yield Ok(
            confirmationView(
              appConfig,
              confirmation,
              token,
              pdf,
              getCountryName,
              urlViewModel = ConfirmationUrlViewModel(isBTAUser)
            )
          )
        ).recover { case e: Throwable =>
          Redirect(routes.ErrorController.onPageLoad)
        }
      case _ =>
        Future(Redirect(routes.ErrorController.onPageLoad))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request: DataRequest[?] =>
      for {
        isBTAUser <- btaUserService.isBTAUser(request.internalId)
        removed   <- dataCacheService.remove(request.userAnswers.cacheMap)
        _ = if (!removed)
              logger.warn("[ConfirmationController][onSubmit] Session entry failed to be removed from the cache")
      } yield Redirect(ConfirmationUrlViewModel(isBTAUser).call)
  }

}
