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
import models.requests.IdentifierRequest
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
    getPdf(reference, token, getApplicationPDF)
  }

  def ruling(reference: String, token: Option[String]): Action[AnyContent] = identify.async { implicit request =>
    getPdf(reference, token, getRulingPDF)
  }

  private def getPdf(reference: String, token: Option[String], pdf: (String, String) => Future[Result])
                    (implicit request: IdentifierRequest[AnyContent]): Future[Result] = {
    (request.eoriNumber, token) match {
      case (Some(eori), _) => pdf(eori, reference)
      case (_, Some(tkn)) => pdfService.decodeToken(tkn) match {
        case Some(eori) => pdf(eori, reference)
        case None => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
      case _ => Future.successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad()))
    }
  }

  private def getRulingPDF(implicit request: Request[AnyContent]): (String, String) => Future[Result] = { (eori, reference) =>
    caseService.getCaseWithRulingForUser(eori, reference) flatMap { c: Case =>
      generatePdf(
        rulingPdf(appConfig, c, c.decision.getOrElse(throw new IllegalStateException("Missing decision"))), s"BTIRuling$reference.pdf"
      )
    }
  }

  private def getApplicationPDF(implicit request: Request[AnyContent]): (String, String) => Future[Result] = { (eori, reference) =>
    caseService.getCaseForUser(eori, reference) flatMap { c: Case =>
      fileService.getAttachmentMetadata(c) flatMap { attachmentData =>
        generatePdf(
          applicationPdf(appConfig, c, attachmentData), s"BTIConfirmation$reference.pdf"
        )
      }
    }
  }

  private def generatePdf(htmlContent: Html, filename: String): Future[Result] = {
    pdfService.generatePdf(htmlContent) map { pdfFile =>
      Results.Ok(pdfFile.content)
        .as(pdfFile.contentType)
        .withHeaders(CONTENT_DISPOSITION -> s"filename=$filename")
    }
  }

}
