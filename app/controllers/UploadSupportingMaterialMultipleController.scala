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
import models.{FileAttachment, Mode, UploadError, UserAnswers}
import navigation.Navigator
import pages._
import play.api.i18n.I18nSupport
import play.api.mvc._
import service.FileService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.uploadSupportingMaterialMultiple

import scala.concurrent.{ExecutionContext, Future}
import models.requests.DataRequest

import scala.util.Failure
import scala.util.Success
import scala.util.control.NonFatal
import models.requests.FileStoreInitiateRequest
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

  val FormInputField = "file"

  private def upsertFile(file: FileAttachment, userAnswers: UserAnswers): UserAnswers = {
    val updatedFiles = userAnswers
      .get(UploadSupportingMaterialMultiplePage)
      .map { files =>
        val uploadedFiles = files.filter(_.uploaded)
        val index = uploadedFiles.indexWhere(_.id == file.id)
        if (index >= 0) {
          uploadedFiles.updated(index, file)
        } else {
          uploadedFiles :+ file
        }
      }.getOrElse(Seq(file))

    userAnswers.set(UploadSupportingMaterialMultiplePage, updatedFiles)
  }

  def onFileUploadSuccess(id: String, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request: DataRequest[AnyContent] =>
    val updatedAnswers = for {
      files <- request.userAnswers.get(UploadSupportingMaterialMultiplePage)
      file <- files.find(_.id == id)
      updated = upsertFile(file.copy(uploaded = true), request.userAnswers)
    } yield updated

    val userAnswers = updatedAnswers.getOrElse(request.userAnswers)

    dataCacheConnector
      .save(userAnswers.cacheMap)
      .map(_ => Redirect(navigator.nextPage(UploadSupportingMaterialMultiplePage, mode)(userAnswers)))
  }

  def onFileUploadError(id: String, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request: DataRequest[AnyContent] =>
    val errorCode = request.getQueryString("errorCode").getOrElse("")
    val errorMessage = request.getQueryString("errorMessage").getOrElse("")
    logger.error(s"File upload for file with id ${id} failed with error code ${errorCode}: ${errorMessage}")

    val updatedAnswers = for {
      files <- request.userAnswers.get(UploadSupportingMaterialMultiplePage)
      updatedFiles = files.filterNot(_.id == id)
    } yield request.userAnswers.set(UploadSupportingMaterialMultiplePage, updatedFiles)

    val uploadError = UploadError.fromErrorCode(errorCode)
    val userAnswers = updatedAnswers.getOrElse(request.userAnswers)
    val formWithErrors = form.withError(FormInputField,  uploadError.errorMessageKey)

    for {
      _ <- dataCacheConnector.save(userAnswers.cacheMap)
      result <- renderView(mode, formWithErrors)
        .transformWith {
          case Failure(NonFatal(_)) =>
            Future.successful(BadGateway)
          case Success(html) =>
            Future.successful(BadRequest(html))
        }
    } yield result
  }

  def onFileSelected(): Action[FileAttachment] =
    (identify andThen getData andThen requireData).async[FileAttachment](parse.json[FileAttachment]) { implicit request: DataRequest[FileAttachment] =>
      val updatedAnswers = upsertFile(request.body, request.userAnswers)

      dataCacheConnector
        .save(updatedAnswers.cacheMap)
        .map(_ => Ok)
    }

  def renderView(mode: Mode, form: Form[String])(implicit request: DataRequest[AnyContent]) = {
    val fileId = ju.UUID.randomUUID().toString

    fileService.initiate(FileStoreInitiateRequest(
      id = Some(fileId),
      successRedirect = Some(routes.UploadSupportingMaterialMultipleController.onFileUploadSuccess(fileId, mode).absoluteURL),
      errorRedirect = Some(routes.UploadSupportingMaterialMultipleController.onFileUploadError(fileId, mode).absoluteURL)
    )).map { response =>
        val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")
        uploadSupportingMaterialMultiple(appConfig, response, form, goodsName, mode)
    }
  }

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    renderView(mode, form)
      .transformWith {
              case Failure(NonFatal(_)) =>
                Future.successful(BadGateway)
              case Success(html) =>
                Future.successful(Ok(html))
      }
  }
}
