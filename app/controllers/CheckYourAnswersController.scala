/*
 * Copyright 2024 HM Revenue & Customs
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
import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import mapper.CaseRequestMapper
import models._
import models.requests.DataRequest
import navigation.Navigator
import pages._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import service.{CasesService, CountriesService, FileService, PdfService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.CheckYourAnswersHelper
import utils.JsonFormatters._
import viewmodels.{AnswerSection, FileView, PdfViewModel}
import views.html.check_your_answers

import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersController @Inject() (
  appConfig: FrontendAppConfig,
  dataCacheConnector: DataCacheConnector,
  auditService: AuditService,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  countriesService: CountriesService,
  caseService: CasesService,
  pdfService: PdfService,
  fileService: FileService,
  mapper: CaseRequestMapper,
  cc: MessagesControllerComponents,
  checkYourAnswersView: check_your_answers
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val sendingSamplesAnswer = request.userAnswers.get(AreYouSendingSamplesPage).getOrElse(false)

    val checkYourAnswersHelper = new CheckYourAnswersHelper(request.userAnswers, countriesService.getAllCountriesById)

    val sections = Seq(
      AnswerSection(
        Some("checkYourAnswers.aboutTheGoodsSection"),
        Seq(
          checkYourAnswersHelper.provideGoodsName,
          checkYourAnswersHelper.provideGoodsDescription,
          checkYourAnswersHelper.addConfidentialInformation(),
          checkYourAnswersHelper.provideConfidentialInformation,
          checkYourAnswersHelper.supportingMaterialFileListChoice,
          checkYourAnswersHelper.supportingMaterialFileList,
          checkYourAnswersHelper.areYouSendingSamples,
          checkYourAnswersHelper.isSampleHazardous,
          checkYourAnswersHelper.returnSamples,
          checkYourAnswersHelper.commodityCodeBestMatch,
          checkYourAnswersHelper.commodityCodeDigits,
          checkYourAnswersHelper.legalChallenge,
          checkYourAnswersHelper.legalChallengeDetails
        ).flatten
      ),
      AnswerSection(
        Some("checkYourAnswers.aboutOtherRulingsSection"),
        Seq(
          checkYourAnswersHelper.previousBTIRuling,
          checkYourAnswersHelper.provideBTIReference,
          checkYourAnswersHelper.similarItemCommodityCode,
          checkYourAnswersHelper.commodityCodeRulingReference
        ).flatten
      ),
      AnswerSection(
        Some("checkYourAnswers.aboutTheApplicantSection"),
        Seq(
          checkYourAnswersHelper.registeredName,
          checkYourAnswersHelper.registeredAddress,
          checkYourAnswersHelper.enterContactDetailsName,
          checkYourAnswersHelper.enterContactDetailsEmail,
          checkYourAnswersHelper.enterContactDetailsPhone
        ).flatten
      )
    )

    Ok(checkYourAnswersView(appConfig, sections, sendingSamplesAnswer))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request: DataRequest[_] =>
      val answers        = request.userAnswers
      val newCaseRequest = mapper.map(answers)

      val fileAttachments: Seq[FileAttachment] = answers
        .get(UploadSupportingMaterialMultiplePage)
        .getOrElse(Seq.empty)
        .filter(_.uploaded)

      val keepConfidential = answers
        .get(MakeFileConfidentialPage)
        .getOrElse(Map.empty)

      val fileView = fileAttachments.map(file => FileView(file.id, file.name, keepConfidential(file.id)))

      val withStatus = fileAttachments
        .map(att => Attachment(att.id, public = !keepConfidential(att.id)))

      for {
        published <- fileService.publish(fileAttachments)
        publishIds  = published.map(_.id)
        attachments = withStatus.filter(att => publishIds.contains(att.id))
        atar <- createCase(newCaseRequest, attachments)

        pdf = PdfViewModel(atar, fileView)
        pdfFile   <- pdfService.generatePdf(views.html.components.view_application_pdf(appConfig, pdf, getCountryName))
        pdfStored <- fileService.uploadApplicationPdf(atar.reference, pdfFile.content)
        pdfAttachment = Attachment(pdfStored.id, public = false)
        caseUpdate = CaseUpdate(
          Some(
            ApplicationUpdate(
              applicationPdf = SetValue(Some(pdfAttachment))
            )
          )
        )
        _ <- caseService.update(atar.reference, caseUpdate)
        _ <- caseService.addCaseCreatedEvent(atar, Operator("", Some(atar.application.contact.name)))
        _           = auditService.auditBTIApplicationSubmissionSuccessful(atar)
        userAnswers = answers.set(ConfirmationPage, Confirmation(atar)).set(PdfViewPage, pdf)
        _           <- dataCacheConnector.save(userAnswers.cacheMap)
        res: Result <- successful(Redirect(navigator.nextPage(CheckYourAnswersPage, NormalMode)(userAnswers)))
      } yield res
  }

  private def createCase(
    newCaseRequest: NewCaseRequest,
    attachments: Seq[Attachment]
  )(implicit headerCarrier: HeaderCarrier): Future[Case] =
    caseService.create(newCaseRequest.copy(attachments = attachments))

  private def getCountryName(code: String): Option[String] =
    countriesService.getAllCountriesById.get(code).map(_.countryName)
}
