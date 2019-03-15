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
import models.Case
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.twirl.api.Html
import service.{CasesService, FileService, PdfService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.pdftemplates.{applicationPdf, rulingPdf}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PdfDownloadController @Inject()(appConfig: FrontendAppConfig,
                                      override val messagesApi: MessagesApi,
                                      identify: IdentifierAction,
                                      pdfService: PdfService,
                                      caseService: CasesService,
                                      fileService: FileService
                                     ) extends FrontendController with I18nSupport {

  def application(reference: String, token: Option[String]): Action[AnyContent] = identify.async { implicit request =>
    if(request.eoriNumber.isDefined) {
      getApplicationPDF(request.eoriNumber.get, reference)
    } else if(token.isDefined) {
      pdfService.decodeToken(token.get) match {
        case Some(eori) => getApplicationPDF(eori, reference)
        case None => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
    } else {
      Future.successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad()))
    }
  }

  def ruling(reference: String, token: Option[String]): Action[AnyContent] = identify.async { implicit request =>
    if(request.eoriNumber.isDefined) {
      getRulingPDF(request.eoriNumber.get, reference)
    } else if(token.isDefined) {
      pdfService.decodeToken(token.get) match {
        case Some(eori) => getRulingPDF(eori, reference)
        case None => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
    } else {
      Future.successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad()))
    }
  }

  private def getApplicationPDF(eori: String, reference: String)(implicit r: Request[_]): Future[Result] = {
    caseService.getCaseForUser(eori, reference) flatMap { c: Case =>
      fileService.getAttachmentMetadata(c) flatMap { attachmentData =>
        generatePdf(applicationPdf(appConfig, c, attachmentData), s"BTIConfirmation$reference.pdf")
      }
    }
  }

  private def getRulingPDF(eori: String, reference: String)(implicit r: Request[_]): Future[Result] = {
    caseService.getCaseWithRulingForUser(eori, reference) flatMap { c: Case =>
      generatePdf(rulingPdf(appConfig, c, c.decision.get), s"BTIRuling$reference.pdf")
    }
  }

  private def generatePdf(htmlContent: Html, filename: String) = pdfService.generatePdf(htmlContent) map {
    pdfFile => Results.Ok(pdfFile.content).as(pdfFile.contentType).withHeaders(CONTENT_DISPOSITION -> s"filename=$filename")
  }

}
