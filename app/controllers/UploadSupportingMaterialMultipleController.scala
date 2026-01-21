/*
 * Copyright 2026 HM Revenue & Customs
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
import controllers.actions.*
import forms.UploadSupportingMaterialMultipleFormProvider
import models.requests.{DataRequest, FileStoreInitiateRequest}
import models.{FileAttachment, Mode, UploadError, UserAnswers}
import navigation.Navigator
import pages.*
import play.api.Logging
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.*
import play.twirl.api.Html
import service.{DataCacheService, FileService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.uploadSupportingMaterialMultiple

import java.util as ju
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class UploadSupportingMaterialMultipleController @Inject() (
  appConfig: FrontendAppConfig,
  dataCacheService: DataCacheService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  navigator: Navigator,
  requireData: DataRequiredAction,
  formProvider: UploadSupportingMaterialMultipleFormProvider,
  fileService: FileService,
  cc: MessagesControllerComponents,
  uploadSupportingMaterialMultipleView: uploadSupportingMaterialMultiple
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport
    with Logging {
  private lazy val form = formProvider()

  val FormInputField = "file"

  private def upsertFile(file: FileAttachment, userAnswers: UserAnswers): UserAnswers = {
    val confidentialityStatuses = userAnswers
      .get(MakeFileConfidentialPage)
      .getOrElse(Map.empty[String, Boolean])

    val updatedFiles = userAnswers
      .get(UploadSupportingMaterialMultiplePage)
      .map { files =>
        val uploadedFiles = files.filter(file => file.uploaded && confidentialityStatuses.keySet.contains(file.id))
        val index         = uploadedFiles.indexWhere(_.id == file.id)

        if (index >= 0) {
          uploadedFiles.updated(index, file)
        } else {
          uploadedFiles :+ file
        }
      }
      .getOrElse(Seq(file))

    userAnswers.set(UploadSupportingMaterialMultiplePage, updatedFiles)
  }

  private def removeFile(id: String, userAnswers: UserAnswers): UserAnswers = {
    val updatedAnswers = for {
      files <- userAnswers.get(UploadSupportingMaterialMultiplePage)
      updatedFiles = files.filterNot(_.id == id)
    } yield userAnswers.set(UploadSupportingMaterialMultiplePage, updatedFiles)

    updatedAnswers.getOrElse(userAnswers)
  }

  def onFileUploadSuccess(id: String, mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request: DataRequest[AnyContent] =>
      val updatedAnswers = for {
        files <- request.userAnswers.get(UploadSupportingMaterialMultiplePage)
        file  <- files.find(_.id == id)
        updated = upsertFile(file.copy(uploaded = true), request.userAnswers)
      } yield updated

      val userAnswers = updatedAnswers.getOrElse {
        // There is no metadata entry for this file
        // The metadata entry is usually created by a JS event handler on the file picker
        // We can end up here if Javascript is disabled
        upsertFile(FileAttachment(id, "", "", 0L, uploaded = true), request.userAnswers)
      }

      dataCacheService
        .save(userAnswers.cacheMap)
        .map(_ => Redirect(navigator.nextPage(UploadSupportingMaterialMultiplePage, mode)(userAnswers)))
    }

  def onFileSelected(): Action[FileAttachment] =
    (identify andThen getData andThen requireData).async[FileAttachment](parse.json[FileAttachment]) {
      implicit request: DataRequest[FileAttachment] =>
        val updatedAnswers = upsertFile(request.body, request.userAnswers)
        dataCacheService
          .save(updatedAnswers.cacheMap)
          .map(_ => Ok)
    }

  def renderView(fileId: String, mode: Mode, form: Form[String])(implicit
    request: DataRequest[AnyContent]
  ): Future[Html] =
    fileService
      .initiate(
        FileStoreInitiateRequest(
          id = Some(fileId),
          successRedirect = Some(
            appConfig.host + routes.UploadSupportingMaterialMultipleController.onFileUploadSuccess(fileId, mode).url
          ),
          errorRedirect =
            Some(appConfig.host + routes.UploadSupportingMaterialMultipleController.onPageLoad(Some(fileId), mode).url),
          maxFileSize = appConfig.fileUploadMaxSize
        )
      )
      .map { response =>
        val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")
        uploadSupportingMaterialMultipleView(appConfig, response, form, goodsName, mode)
      }

  def onPageLoad(id: Option[String], mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      val fileId = id.getOrElse(ju.UUID.randomUUID().toString)

      request
        .getQueryString("errorCode")
        .map { errorCode =>
          // Received an error from Upscan
          val errorMessage   = request.getQueryString("errorMessage").getOrElse("")
          val uploadError    = UploadError.fromErrorCode(errorCode)
          val userAnswers    = removeFile(fileId, request.userAnswers)
          val formWithErrors = form.withError(FormInputField, uploadError.errorMessageKey)
          logger.error(
            s"[UploadSupportingMaterialMultipleController][onPageLoad] File upload for file with id $fileId failed with error code $errorCode: $errorMessage"
          )

          for {
            _    <- dataCacheService.save(userAnswers.cacheMap)
            html <- renderView(fileId, mode, formWithErrors)
          } yield BadRequest(html)
        }
        .getOrElse {
          // Normal page render
          renderView(fileId, mode, form)
            .map(Ok(_))
        }
        .recoverWith { case NonFatal(_) =>
          Future.successful(BadGateway)
        }
    }
}
