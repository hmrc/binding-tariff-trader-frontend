/*
 * Copyright 2018 HM Revenue & Customs
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

import audit.AuditService
import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import javax.inject.Inject
import mapper.CaseRequestMapper
import models.Confirmation.format
import models.WhichBestDescribesYou.isBusinessRepresentative
import models._
import navigation.Navigator
import pages.{ConfirmationPage, UploadSupportingMaterialMultiplePage, UploadWrittenAuthorisationPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import service.{CasesService, FileService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.declaration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.successful

class DeclarationController @Inject()(
                                       appConfig: FrontendAppConfig,
                                       override val messagesApi: MessagesApi,
                                       dataCacheConnector: DataCacheConnector,
                                       auditService: AuditService,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       caseService: CasesService,
                                       fileService: FileService,
                                       mapper: CaseRequestMapper
                                     ) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) { implicit request =>
    Ok(declaration(appConfig, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async { implicit request =>

    val answers = request.userAnswers.get // TODO: we should not call `get` on an Option
    val newCaseRequest = mapper.map(answers)

    val attachments: Seq[FileAttachment] = answers
      .get(UploadSupportingMaterialMultiplePage)
      .getOrElse(Seq.empty)

    for {
      att: Seq[SubmittedFileAttachment] <- fileService.publish(attachments)
      letter <- getPublishedLetter(answers)
      c: Case <- createCase(newCaseRequest, att, letter, answers)
      _ = auditService.auditBTIApplicationSubmissionSuccessful(c)
      userAnswers = answers.set(ConfirmationPage, Confirmation(c.reference))
      _ <- dataCacheConnector.save(userAnswers.cacheMap)
      res: Result <- successful(Redirect(navigator.nextPage(ConfirmationPage, mode)(userAnswers)))
    } yield res

  }

  private def getPublishedLetter(answers: UserAnswers)
                                (implicit headerCarrier: HeaderCarrier): Future[Option[SubmittedFileAttachment]] = {

    if (isBusinessRepresentative(answers)) {
      answers.get(UploadWrittenAuthorisationPage)
        .map( fileService.publish(_).map(Some(_)) )
        .getOrElse(successful(None))
    } else {
      successful(None)
    }
  }

  private def createCase(newCaseRequest: NewCaseRequest, attachments: Seq[SubmittedFileAttachment],
                         letter: Option[SubmittedFileAttachment], answers: UserAnswers)
                        (implicit headerCarrier: HeaderCarrier): Future[Case] = {
    caseService.create(appendAttachments(newCaseRequest, attachments, letter, answers))
  }

  private def appendAttachments(caseRequest: NewCaseRequest, attachments: Seq[SubmittedFileAttachment],
                                letter: Option[SubmittedFileAttachment], answers: UserAnswers): NewCaseRequest = {

    val published: Seq[Attachment] = attachments
      .filter(_.isInstanceOf[PublishedFileAttachment])
      .map(_.asInstanceOf[PublishedFileAttachment])
      .map(f => Attachment(url = f.url, mimeType = f.mimeType))

    if (isBusinessRepresentative(answers)) {
      val letterOfAuth: Option[Attachment] = letter
        .filter(_.isInstanceOf[PublishedFileAttachment])
        .map(_.asInstanceOf[PublishedFileAttachment])
        .map(f => Attachment(url = f.url, mimeType = f.mimeType))

      val agentDetails = caseRequest.application.agent.map(_.copy(letterOfAuthorisation = letterOfAuth))
      val application = caseRequest.application.copy(agent = agentDetails)
      caseRequest.copy(application = application, attachments = published)
    } else {
      caseRequest.copy(attachments = published)
    }
  }

}
