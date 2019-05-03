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
import views.html.templates.{applicationTemplate, applicationView, rulingCertificateTemplate}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.successful

class PdfDownloadController @Inject()(appConfig: FrontendAppConfig,
                                      override val messagesApi: MessagesApi,
                                      identify: IdentifierAction,
                                      pdfService: PdfService,
                                      caseService: CasesService,
                                      fileService: FileService
                                     ) extends FrontendController with I18nSupport {

  private type Eori = String
  private type CaseReference = String

  def applicationPdf(reference: String, token: Option[String]): Action[AnyContent] = identify.async { implicit request =>
    getPdf(request.eoriNumber, reference, token, getApplicationPDF)
  }

  private def getPdf(maybeEoriNumber: Option[Eori],
                     reference: CaseReference,
                     token: Option[String],
                     toPdf: (Eori, CaseReference) => Future[Result]): Future[Result] = {
    (maybeEoriNumber, token) match {
      case (Some(eori), _) => toPdf(eori, reference)
      case (_, Some(tkn)) => pdfService.decodeToken(tkn) match {
        case Some(eori) => toPdf(eori, reference)
        case None => successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
      case _ => successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad()))
    }
  }



  private def getApplicationPDF(eori: Eori, reference: CaseReference)
                               (implicit request: Request[AnyContent]): Future[Result] = {

    //TODO: make function to not repeat this for comprehension and pass the view as a Function Parameter

    for {
      c <- caseService.getCaseForUser(eori, reference)
      attachments <- fileService.getAttachmentMetadata(c)
      letter <- fileService.getLetterOfAuthority(c)
      view <- generatePdf(applicationTemplate(appConfig, c, attachments, letter, true), s"BTIConfirmation$reference.pdf")
    } yield view
  }

  private def generatePdf(htmlContent: Html, filename: String): Future[Result] = {
    pdfService.generatePdf(htmlContent) map { pdfFile =>
      Results.Ok(pdfFile.content)
        .as(pdfFile.contentType)
        .withHeaders(CONTENT_DISPOSITION -> s"filename=$filename")
    }
  }

  def rulingCertificatePdf(reference: String, token: Option[String]): Action[AnyContent] = identify.async { implicit request =>
    getPdf(request.eoriNumber, reference, token, getRulingPDF)
  }

  private def getRulingPDF(eori: Eori, reference: CaseReference)
                          (implicit request: Request[AnyContent]): Future[Result] = {
    caseService.getCaseWithRulingForUser(eori, reference) flatMap { c: Case =>
      lazy val decision = c.decision.getOrElse(throw new IllegalStateException("Missing decision"))
      generatePdf(views.html.templates.rulingCertificate(appConfig, c, decision), s"BTIRuling$reference.pdf")
    }
  }

  def viewRulingCertificate(reference: String, token: Option[String]): Action[AnyContent] = identify.async { implicit request =>
    request.eoriNumber match {
      case Some(eori: String) =>
        caseService.getCaseForUser(eori, reference) flatMap {
          c: Case => successful(Ok(rulingCertificateTemplate(appConfig, c)))
        }
      case None => successful(Redirect(routes.BeforeYouStartController.onPageLoad()))
    }
  }

  def viewApplication(reference: String, token: Option[String]): Action[AnyContent] = identify.async { implicit request =>
    request.eoriNumber match {
      case Some(eori: String) => {
        for {
          c <- caseService.getCaseForUser(eori, reference)
          attachments <- fileService.getAttachmentMetadata(c)
          letter <- fileService.getLetterOfAuthority(c)
          view <- successful(Ok(applicationView(appConfig, c, attachments, letter)))
        } yield view
      }
      case None => successful(Redirect(routes.BeforeYouStartController.onPageLoad()))
    }
  }
}
