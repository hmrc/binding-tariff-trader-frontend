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
import connectors.DataCacheConnector
import controllers.actions._
import javax.inject.Inject
import models.Confirmation
import pages.ConfirmationPage
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import service.PdfService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.confirmation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.successful

class ConfirmationController @Inject()(appConfig: FrontendAppConfig,
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       dataCacheConnector: DataCacheConnector,
                                       pdfService: PdfService
                                      ) extends FrontendController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    def show(c: Confirmation): Future[Result] = {
      dataCacheConnector.remove(request.userAnswers.cacheMap).map { b: Boolean =>
        if (!b) {
          Logger.warn("Session entry failed to be removed from the cache")
        }
        Ok(confirmation(appConfig, c))
      }
    }

    request.userAnswers.get(ConfirmationPage) match {
      case Some(c: Confirmation) => show(c)
      case _ => successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }

  }

  def pdf: Action[AnyContent] = (identify).async { implicit request =>

    def generatePdf(): Future[Result] = {
      pdfService.getPdf().map {
        response =>
          response.status match {
            case OK => Ok(response.bodyAsBytes.toArray).as("application/pdf")
              .withHeaders("Content-Disposition" -> s"attachment; filename=result.pdf")
            case _ => BadRequest(response.body)

          }
      }
    }


    generatePdf()

//    request.userAnswers.get(ConfirmationPage) match {
//      case Some(c: Confirmation) => generatePdf(c)
//      case _ => successful(Redirect(routes.SessionExpiredController.onPageLoad()))
//    }

  }

  //  def pdf = {
  //
  //    pdfService.getPdf()
  //      .map { response =>
  //        response.status match {
  //          case OK => Ok(response.bodyAsBytes.toArray).as("application/pdf")
  //            .withHeaders("Content-Disposition" -> s"attachment; filename=result.pdf")
  //          case _ => BadRequest(response.body)
  //        }
  //      }
  //  }

}
