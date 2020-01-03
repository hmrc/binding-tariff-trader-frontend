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
import controllers.actions._
import javax.inject.Inject
import models.Case
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.twirl.api.Html
import service.{CasesService, FileService, PdfService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.templates.{applicationTemplate, applicationView, rulingCertificateTemplate, rulingCertificateView}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.successful

class ApplicationController @Inject()(appConfig: FrontendAppConfig,
                                      override val messagesApi: MessagesApi,
                                      identify: IdentifierAction,
                                      pdfService: PdfService,
                                      caseService: CasesService,
                                      fileService: FileService
                                     ) extends FrontendController with I18nSupport {

  private type Eori = String
  private type CaseReference = String

  def applicationPdf(reference: String, token: Option[String]): Action[AnyContent] = identify.async { implicit request =>
    getPdfOrHtml(request.eoriNumber, reference, token, getApplicationPDF)
  }

  def rulingCertificatePdf(reference: String, token: Option[String]): Action[AnyContent] = identify.async { implicit request =>
    getPdfOrHtml(request.eoriNumber, reference, token, getRulingPDF)
  }

  def viewRulingCertificate(reference: String, token: Option[String]): Action[AnyContent] = identify.async { implicit request =>
    getPdfOrHtml(request.eoriNumber, reference, token, rulingCertificateHtmlView)
  }

  def viewApplication(reference: String, token: Option[String]): Action[AnyContent] = identify.async { implicit request =>
    getPdfOrHtml(request.eoriNumber, reference, token, getApplicationView)
  }

  private def getPdfOrHtml(maybeEoriNumber: Option[Eori],
                     reference: CaseReference,
                     token: Option[String],
                     toView: (Eori, CaseReference) => Future[Result]): Future[Result] = {
    (maybeEoriNumber, token) match {
      case (Some(eori), _) => toView(eori, reference)
      case (_, Some(tkn)) => pdfService.decodeToken(tkn) match {
        case Some(eori) => toView(eori, reference)
        case None => successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
      case _ => successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad()))
    }
  }

  private def getApplicationPDF(eori: Eori, reference: CaseReference)
                               (implicit request: Request[AnyContent]): Future[Result] = {
    getApplicationPDForHtml(eori,reference,true)
  }

  private def getApplicationView(eori: Eori, reference: CaseReference)
                               (implicit request: Request[AnyContent]): Future[Result] = {
    getApplicationPDForHtml(eori,reference,false)
  }

  private def getApplicationPDForHtml(eori: Eori, reference: CaseReference, pdf : Boolean)
                               (implicit request: Request[AnyContent]): Future[Result] = {

    for {
      c <- caseService.getCaseForUser(eori, reference)
      attachments <- fileService.getAttachmentMetadata(c)
      letter <- fileService.getLetterOfAuthority(c)
      pdf <- pdf match {
        case true => generatePdf(applicationTemplate(appConfig, c, attachments, letter), s"BTIConfirmation$reference.pdf")
        case false => successful(Ok(applicationView(appConfig, c, attachments, letter)))
      }
    } yield pdf
  }

  private def generatePdf(htmlContent: Html, filename: String): Future[Result] = {
    pdfService.generatePdf(htmlContent) map { pdfFile =>
      Results.Ok(pdfFile.content)
        .as(pdfFile.contentType)
        .withHeaders(CONTENT_DISPOSITION -> s"filename=$filename")
    }
  }

  private def getRulingPDF(eori: Eori, reference: CaseReference)
                          (implicit request: Request[AnyContent]): Future[Result] = {
    caseService.getCaseWithRulingForUser(eori, reference) flatMap { c: Case =>
      lazy val decision = c.decision.getOrElse(throw new IllegalStateException("Missing decision"))
      generatePdf(rulingCertificateTemplate(appConfig, c, decision), s"BTIRuling$reference.pdf")
    }
  }

  def rulingCertificateHtmlView(eori: Eori, reference: CaseReference)
                           (implicit request: Request[AnyContent]): Future[Result] = {
    caseService.getCaseWithRulingForUser(eori, reference) flatMap {
      c: Case => successful(Ok(rulingCertificateView(appConfig, c)))
    }
  }

}
