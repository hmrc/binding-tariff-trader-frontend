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
import models.Case
import models.requests.OptionalDataRequest
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import service.{CasesService, PdfService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.pdftemplates.applicationPdf
import scala.concurrent.ExecutionContext.Implicits.global

class PdfDownloadController @Inject()(appConfig: FrontendAppConfig,
                                      override val messagesApi: MessagesApi,
                                      identify: IdentifierAction,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction,
                                      dataCacheConnector: DataCacheConnector,
                                      pdfService: PdfService,
                                      caseService: CasesService
                                      ) extends FrontendController with I18nSupport {


  def application(reference: String): Action[AnyContent] = (identify andThen getData).async { implicit request: OptionalDataRequest[_] =>

    val userEori = request.userEoriNumber

    caseService.getCaseForUser(userEori, reference).flatMap {
      case Some(c: Case) => pdfService.generatePdf(s"confirmation_$reference.pdf", applicationPdf(appConfig, c).toString())

      // TODO - what if case not found?
      case _ => throw new Exception("Problem !!!")
    }
  }


  // TODO delete this handler - for testing only!!!
  def applicationPreview(reference: String): Action[AnyContent] = (identify andThen getData).async { implicit request: OptionalDataRequest[_] =>

    val userEori = request.userEoriNumber

    caseService.getCaseForUser(userEori, reference).map {
      case Some(c: Case) => Ok(applicationPdf(appConfig, c))

      // TODO - what if case not found?
      case _ => throw new Exception("Problem !!!")
    }
  }
}
