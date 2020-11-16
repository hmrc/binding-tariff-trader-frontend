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
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.Html
import service.{CasesService, CountriesService, FileService, PdfService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.AssetLoader
import viewmodels.{FileView, PdfViewModel}
import views.html.components.view_application
import views.html.templates._

import scala.concurrent.{ExecutionContext, Future}

class ApplicationController @Inject()(appConfig: FrontendAppConfig,
                                      identify: IdentifierAction,
                                      pdfService: PdfService,
                                      caseService: CasesService,
                                      fileService: FileService,
                                      countriesService: CountriesService,
                                      assetLoader: AssetLoader,
                                      cc: MessagesControllerComponents
                                     )(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport {

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
        case Some(eori) =>
          toView(eori, reference)
        case None =>
          Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
      case _ =>
        Future.successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad()))
    }
  }

  private def getApplicationPDF(eori: Eori, reference: CaseReference)
                               (implicit request: Request[AnyContent]): Future[Result] = {
    getApplicationPDForHtml(eori,reference, pdf = true)
  }

  private def getApplicationView(eori: Eori, reference: CaseReference)
                               (implicit request: Request[AnyContent]): Future[Result] = {
    getApplicationPDForHtml(eori,reference, pdf = false)
  }


  private def getApplicationPDForHtml(eori: Eori, reference: CaseReference, pdf : Boolean)
                               (implicit request: Request[AnyContent]): Future[Result] = {

    for {
      c <- caseService.getCaseForUser(eori, reference)
      attachments <- fileService.getAttachmentMetadata(c)
      attachmentFileView = (attachments, c.attachments).zipped map {
        (fileStoreRespAtt, caseAttachment) =>
          FileView(fileStoreRespAtt.id, fileStoreRespAtt.fileName, caseAttachment.public)
      }
      out <- pdf match {
        case true =>
          generatePdf(view_application(appConfig, PdfViewModel(c, attachmentFileView), getCountryName), s"BTIConfirmation$reference.pdf")
        case false =>
          Future.successful(Ok(applicationView(appConfig, PdfViewModel(c, attachmentFileView), getCountryName)))
      }
    } yield out
  }

  private def generatePdf(htmlContent: Html, filename: String)
                         (implicit request: Request[AnyContent]): Future[Result] = {
    //val styledHtml = addPdfStyles(htmlContent)
    Logger.logger.info("html content is: " + htmlContent)
    pdfService.generatePdf(htmlContent) map { pdfFile =>
      Results.Ok(pdfFile.content)
        .as(pdfFile.contentType)
        .withHeaders(CONTENT_DISPOSITION -> s"attachment; filename=$filename")
    }
  }

  private def addPdfStyles(htmlContent: Html)
                          (implicit request: Request[AnyContent]): Html = {

    val cssSource = assetLoader.fromURL(controllers.routes.Assets.versioned("stylesheets/print_pdf.css")
      .absoluteURL()).mkString
    Html(htmlContent.toString
      .replace("<head>", s"<head><style>$cssSource</style>")
    )
  }

  private def getRulingPDF(eori: Eori, reference: CaseReference)
                          (implicit request: Request[AnyContent]): Future[Result] = {
    caseService.getCaseWithRulingForUser(eori, reference) flatMap { c: Case =>
      lazy val decision = c.decision.getOrElse(throw new IllegalStateException("Missing decision"))
      generatePdf(rulingCertificateTemplate(appConfig, c, decision, getCountryName), s"BTIRuling$reference.pdf")
    }
  }

  def rulingCertificateHtmlView(eori: Eori, reference: CaseReference)
                           (implicit request: Request[AnyContent]): Future[Result] = {
    caseService.getCaseWithRulingForUser(eori, reference) flatMap { c: Case =>
      Future.successful(Ok(rulingCertificateView(appConfig, c, getCountryName)))
    }
  }

  def getCountryName(code: String) =
    countriesService
      .getAllCountries
      .find(_.code == code)
      .map(_.countryName)
}
