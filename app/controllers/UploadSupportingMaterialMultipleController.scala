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
import connectors.DataCacheConnector
import controllers.actions._
import forms.UploadSupportingMaterialMultipleFormProvider
import javax.inject.Inject
import models.{FileAttachment, Mode}
import navigation.Navigator
import pages._
import play.api.i18n.{I18nSupport, Lang}
import play.api.libs.Files.TemporaryFile
import play.api.mvc._
import service.FileService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.uploadSupportingMaterialMultiple

import scala.concurrent.{ ExecutionContext, Future }
import models.UserAnswers
import uk.gov.hmrc.http.HeaderCarrier
import models.requests.DataRequest
import scala.util.Failure
import scala.util.Success
import scala.util.control.NonFatal
import models.requests.FileStoreInitiateRequest
import models.response.ScanResult
import java.{util => ju}
import play.api.data.Form
import play.api.Logging

class UploadSupportingMaterialMultipleController @Inject()(
  appConfig: FrontendAppConfig,
  dataCacheConnector: DataCacheConnector,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  navigator: Navigator,
  requireData: DataRequiredAction,
  formProvider: UploadSupportingMaterialMultipleFormProvider,
  fileService: FileService,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport with Logging {
  private lazy val form = formProvider()
  private implicit val lang: Lang = appConfig.defaultLang

  def hasMaxFiles(userAnswers: UserAnswers): Boolean = {
    val numberOfFiles = userAnswers
      .get(UploadSupportingMaterialMultiplePage)
      .map(_.size)
      .getOrElse(0)

    numberOfFiles >= 10
  }

  def upsertFile(file: FileAttachment, userAnswers: UserAnswers): UserAnswers = {
    val updatedFiles = userAnswers
      .get(UploadSupportingMaterialMultiplePage)
      .map { files =>
        val index = files.indexWhere(_.id == file.id)
        if (index >= 0) {
          files.take(index) ++ Seq(file) ++ files.drop(index + 1)
        } else {
          files :+ file
        }
      }.getOrElse(Seq(file))

    userAnswers.set(UploadSupportingMaterialMultiplePage, updatedFiles)
  }

  def onFileScanned(id: String): Action[ScanResult] = Action(parse.json[ScanResult]).async { implicit request =>
    fileService
      .notify(id, request.body)
      .map(_ => Accepted)
  }

  def onFileUploadSuccess(id: String, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request: DataRequest[AnyContent] =>
    logger.info(s"File upload succeeded for id: ${id}, user answers: ${request.userAnswers}")

    val updatedAnswers = for {
      files <- request.userAnswers.get(UploadSupportingMaterialMultiplePage)
      file <- files.find(_.id == id)
      updated = upsertFile(file.copy(uploaded = true), request.userAnswers)
    } yield updated

    val userAnswers = updatedAnswers.getOrElse(request.userAnswers)

    logger.info(s"Updated user answers: ${userAnswers}")

    dataCacheConnector
      .save(userAnswers.cacheMap)
      .map(_ => Redirect(navigator.nextPage(UploadSupportingMaterialMultiplePage, mode)(userAnswers)))
  }

  def onFileUploadError(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request: DataRequest[AnyContent] =>
    val errorCode = request.getQueryString("errorCode")
    val errorMessage = request.getQueryString("errorMessage")
    Ok
  }

  def onFileSelected(): Action[FileAttachment] =
    (identify andThen getData andThen requireData).async[FileAttachment](parse.json[FileAttachment]) { implicit request: DataRequest[FileAttachment] =>
      logger.info(s"File selected for id: ${request.body.id}, user answers: ${request.userAnswers}")

      val updatedAnswers = upsertFile(request.body, request.userAnswers)

      logger.info(s"Updated user answers: ${updatedAnswers}")

      dataCacheConnector
        .save(updatedAnswers.cacheMap)
        .map(_ => Ok)
    }

  def renderView(mode: Mode, form: Form[String])(implicit request: DataRequest[AnyContent]) = {
    val fileId = ju.UUID.randomUUID().toString

    fileService.initiate(FileStoreInitiateRequest(
      id = Some(fileId),
      callbackUrl = routes.UploadSupportingMaterialMultipleController.onFileScanned(fileId).absoluteURL,
      successRedirect = Some(routes.UploadSupportingMaterialMultipleController.onFileUploadSuccess(fileId, mode).absoluteURL),
      errorRedirect = Some(routes.UploadSupportingMaterialMultipleController.onFileUploadError(mode).absoluteURL)
    )).transformWith {
      case Failure(NonFatal(_)) =>
        Future.successful(BadGateway)
      case Success(response) =>
        val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")
        Future.successful(Ok(uploadSupportingMaterialMultiple(appConfig, response, form, goodsName, mode)))
    }
  }

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    renderView(mode, form)
  }
}
