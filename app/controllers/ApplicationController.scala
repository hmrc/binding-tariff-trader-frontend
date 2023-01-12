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

import cats.data.OptionT
import config.FrontendAppConfig
import connectors.InjectAuthHeader
import controllers.actions._

import javax.inject.Inject
import models.Case
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc._
import service.{CasesService, CountriesService, FileService, PdfService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.{FileView, PdfViewModel}
import views.html.documentNotFound
import views.html.templates._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class ApplicationController @Inject() (
  appConfig: FrontendAppConfig,
  identify: IdentifierAction,
  pdfService: PdfService,
  caseService: CasesService,
  fileService: FileService,
  countriesService: CountriesService,
  cc: MessagesControllerComponents,
  applicationView: applicationView,
  rulingCertificateView: rulingCertificateView,
  documentNotFoundView: documentNotFound
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport
    with Logging
    with InjectAuthHeader {

  private type Eori          = String
  private type CaseReference = String

  def applicationPdf(reference: String, token: Option[String]): Action[AnyContent] = identify.async {
    implicit request => getPdfOrHtml(request.eoriNumber, reference, token, getApplicationPDF)
  }

  def rulingCertificatePdf(reference: String, token: Option[String]): Action[AnyContent] = identify.async {
    implicit request => getPdfOrHtml(request.eoriNumber, reference, token, getRulingPDF)
  }

  def coverLetterPdf(reference: String, token: Option[String]): Action[AnyContent] = identify.async {
    implicit request => getPdfOrHtml(request.eoriNumber, reference, token, getLetterPDF)
  }

  def viewRulingCertificate(reference: String, token: Option[String]): Action[AnyContent] = identify.async {
    implicit request => getPdfOrHtml(request.eoriNumber, reference, token, rulingCertificateHtmlView)
  }

  def viewApplication(reference: String, token: Option[String]): Action[AnyContent] = identify.async {
    implicit request => getPdfOrHtml(request.eoriNumber, reference, token, getApplicationView)
  }

  private def getPdfOrHtml(
    maybeEoriNumber: Option[Eori],
    reference: CaseReference,
    token: Option[String],
    toView: (Eori, CaseReference) => Future[Result]
  ): Future[Result] =
    (maybeEoriNumber, token) match {
      case (Some(eori), _) => toView(eori, reference)
      case (_, Some(tkn)) =>
        pdfService.decodeToken(tkn) match {
          case Some(eori) =>
            toView(eori, reference)
          case None =>
            Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad))
        }
      case _ =>
        Future.successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad))
    }

  private def getApplicationPDF(eori: Eori, reference: CaseReference)(
    implicit request: Request[AnyContent]
  ): Future[Result] =
    getApplicationPDForHtml(eori, reference, pdf = true)

  private def getApplicationView(eori: Eori, reference: CaseReference)(
    implicit request: Request[AnyContent]
  ): Future[Result] =
    getApplicationPDForHtml(eori, reference, pdf = false)

  def fetchApplicationPdf(cse: Case)(implicit request: Request[AnyContent]): Future[Result] = {
    val applicationResponse = for {
      pdf    <- OptionT.fromOption[Future](cse.application.applicationPdf)
      meta   <- OptionT(fileService.getAttachmentMetadata(pdf))
      url    <- OptionT.fromOption[Future](meta.url)
      result <- OptionT(fileService.downloadFile(url))
    } yield Ok
      .streamed(result, None, Some(meta.mimeType))
      .withHeaders("Content-Disposition" -> s"attachment; filename=${meta.fileName}")

    val messages     = request.messages
    val documentType = messages("documentNotFound.application")

    applicationResponse
      .getOrElse(NotFound(documentNotFoundView(appConfig, documentType, cse.reference)))
      .recover {
        case NonFatal(_) => BadGateway
      }
  }

  def renderApplicationHtml(cse: Case)(implicit request: Request[AnyContent]): Future[Result] =
    for {
      attachments <- fileService.getAttachmentMetadata(cse)
      attachmentFileView = attachments.map { attachment =>
        FileView(
          id           = attachment.id,
          name         = attachment.fileName,
          confidential = cse.attachments.find(_.id == attachment.id).exists(!_.public)
        )
      }
    } yield Ok(applicationView(appConfig, PdfViewModel(cse, attachmentFileView), getCountryName))

  private def getApplicationPDForHtml(eori: Eori, reference: CaseReference, pdf: Boolean)(
    implicit request: Request[AnyContent]
  ): Future[Result] =
    caseService.getCaseForUser(eori, reference).flatMap { c =>
      val result = if (pdf) {
        fetchApplicationPdf(c)
      } else {
        renderApplicationHtml(c)
      }

      result.recover {
        case NonFatal(_) => BadGateway
      }
    }

  private def getRulingPDF(eori: Eori, reference: CaseReference)(
    implicit request: Request[AnyContent]
  ): Future[Result] =
    caseService.getCaseWithRulingForUser(eori, reference).flatMap { c: Case =>
      val rulingResponse = for {
        decision <- OptionT.fromOption[Future].apply(c.decision)
        pdf      <- OptionT.fromOption[Future](decision.decisionPdf)
        meta     <- OptionT(fileService.getAttachmentMetadata(pdf))
        url      <- OptionT.fromOption[Future](meta.url)
        result   <- OptionT(fileService.downloadFile(url))
      } yield Ok
        .streamed(result, None, Some(meta.mimeType))
        .withHeaders("Content-Disposition" -> s"attachment; filename=${meta.fileName}")

      val messages     = request.messages
      val documentType = messages("documentNotFound.rulingCertificate")

      rulingResponse.getOrElse(NotFound(documentNotFoundView(appConfig, documentType, reference))).recover {
        case NonFatal(_) => BadGateway
      }
    }

  private def getLetterPDF(eori: Eori, reference: CaseReference)(
    implicit request: Request[AnyContent]
  ): Future[Result] =
    caseService.getCaseWithRulingForUser(eori, reference).flatMap { c: Case =>
      val rulingResponse = for {
        decision <- OptionT.fromOption[Future].apply(c.decision)
        pdf      <- OptionT.fromOption[Future](decision.letterPdf)
        meta     <- OptionT(fileService.getAttachmentMetadata(pdf))
        url      <- OptionT.fromOption[Future](meta.url)
        result   <- OptionT(fileService.downloadFile(url))
      } yield Ok
        .streamed(result, None, Some(meta.mimeType))
        .withHeaders("Content-Disposition" -> s"attachment; filename=${meta.fileName}")

      val messages     = request.messages
      val documentType = messages("documentNotFound.rulingCertificate")

      rulingResponse.getOrElse(NotFound(documentNotFoundView(appConfig, documentType, reference))).recover {
        case NonFatal(_) => BadGateway
      }
    }

  def rulingCertificateHtmlView(eori: Eori, reference: CaseReference)(
    implicit request: Request[AnyContent]
  ): Future[Result] =
    caseService.getCaseWithRulingForUser(eori, reference) flatMap { c: Case =>
      Future.successful(Ok(rulingCertificateView(appConfig, c, getCountryName)))
    }

  def getCountryName(code: String): Option[String] =
    countriesService.getAllCountries
      .find(_.code == code)
      .map(_.countryName)
}
