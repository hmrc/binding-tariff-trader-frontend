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
import play.api.mvc.{Action, AnyContent, Results}
import service.{CasesService, FileService, PdfService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.pdftemplates.{applicationPdf, rulingPdf}

import scala.concurrent.ExecutionContext.Implicits.global

class PdfDownloadController @Inject()(appConfig: FrontendAppConfig,
                                      override val messagesApi: MessagesApi,
                                      identify: IdentifierAction,
                                      pdfService: PdfService,
                                      caseService: CasesService,
                                      fileService: FileService
                                     ) extends FrontendController with I18nSupport {

  def application(reference: String): Action[AnyContent] = identify.async { implicit request =>
    caseService.getCaseForUser(request.eoriNumber, reference) flatMap { c: Case =>
      fileService.getAttachmentMetadata(c) flatMap { attachmentData =>
        pdfService.generatePdf(applicationPdf(appConfig, c, attachmentData)).map { binaryFile =>
          Results.Ok(binaryFile.content)
            .as(binaryFile.contentType)
            .withHeaders(CONTENT_DISPOSITION -> s"filename=BTIConfirmation$reference.pdf")
        }
      }
    }
  }

  def ruling(reference: String): Action[AnyContent] = identify.async { implicit request =>
    caseService.getCaseWithRulingForUser(request.eoriNumber, reference) flatMap { c: Case =>
      pdfService.generatePdf(rulingPdf(appConfig, c, c.decision.get)).map { binaryFile =>
        Results.Ok(binaryFile.content)
          .as(binaryFile.contentType)
          .withHeaders(CONTENT_DISPOSITION -> s"filename=BTIRuling$reference.pdf")
      }
    }
  }

}
