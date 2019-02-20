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
import forms.UploadSupportingMaterialMultipleFormProvider
import javax.inject.Inject
import models.FileAttachment.format
import models.{FileAttachment, Mode}
import navigation.Navigator
import pages._
import play.api.data.FormError
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.Files
import play.api.libs.Files.TemporaryFile
import play.api.mvc.{Action, AnyContent, MultipartFormData, Result}
import service.FileService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.uploadSupportingMaterialMultiple

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.{sequence, successful}

class UploadSupportingMaterialMultipleController @Inject()(
                                                            appConfig: FrontendAppConfig,
                                                            override val messagesApi: MessagesApi,
                                                            dataCacheConnector: DataCacheConnector,
                                                            navigator: Navigator,
                                                            identify: IdentifierAction,
                                                            getData: DataRetrievalAction,
                                                            requireData: DataRequiredAction,
                                                            formProvider: UploadSupportingMaterialMultipleFormProvider,
                                                            fileService: FileService
                                                          ) extends FrontendController with I18nSupport {

  private lazy val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val existingFiles = request.userAnswers.get(UploadSupportingMaterialMultiplePage).getOrElse(Seq.empty)
    Ok(uploadSupportingMaterialMultiple(appConfig, form, existingFiles, mode))
  }

  def onSubmit(mode: Mode): Action[MultipartFormData[TemporaryFile]] = (identify andThen getData andThen requireData)
    .async(parse.multipartFormData) { implicit request =>

      def badRequest(errorKey: String, errorMessage: String): Future[Result] = {
        val storedLetter = request.userAnswers.get(UploadWrittenAuthorisationPage)
        successful(
          BadRequest(
            uploadSupportingMaterialMultiple(appConfig, form.copy(errors = Seq(FormError(errorKey, errorMessage))), Seq.empty, mode)
          )
        )
      }

      def uploadFile(validFile: MultipartFormData.FilePart[TemporaryFile]) = {
        fileService.upload(validFile) flatMap {
          case fileAttachment: FileAttachment =>
            val updatedAnswers = request.userAnswers.set(UploadWrittenAuthorisationPage, fileAttachment)
            dataCacheConnector.save(updatedAnswers.cacheMap)
              .map(_ => Redirect(navigator.nextPage(SelectApplicationTypePage, mode)(request.userAnswers)))
          case _ => badRequest("upload-error", "File upload has failed. Try again")
        }
      }


      val files: Seq[MultipartFormData.FilePart[Files.TemporaryFile]] = request.body.files.filter(_.filename.nonEmpty)

      val file = request.body.file("upload-file")

      sequence(
        files.map(
          fileService.upload(_)
        )
      ).flatMap {

        case savedFiles: Seq[FileAttachment] if savedFiles.nonEmpty =>
          val existingFiles = request.userAnswers.get(UploadSupportingMaterialMultiplePage).getOrElse(Seq.empty)
          val updatedFiles = existingFiles ++ savedFiles
          val updatedAnswers = request.userAnswers.set(UploadSupportingMaterialMultiplePage, updatedFiles)
          dataCacheConnector.save(updatedAnswers.cacheMap)
            .map(_ => Redirect(navigator.nextPage(SupportingMaterialFileListPage, mode)(updatedAnswers)))
        case _ =>
          successful(Redirect(routes.SupportingMaterialFileListController.onPageLoad(mode)))
      }

      request.body.file("upload-file") match {
        case Some(file) =>
          validFile(file) match {
            case Right(rightFile) => uploadFile(rightFile)
            case Left(errorMessage) => badRequest("validation-error", errorMessage)
          }
        case _ =>
          request.userAnswers.get(UploadWrittenAuthorisationPage) match {
            case Some(_) => successful(Redirect(navigator.nextPage(SelectApplicationTypePage, mode)(request.userAnswers)))
            case _ => badRequest("letter-of-authority", messagesApi("uploadWrittenAuthorisation.error.selectFile"))
          }
      }

    }


  private def validFile(file: MultipartFormData.FilePart[TemporaryFile]): Either[String, MultipartFormData.FilePart[TemporaryFile]] = {
    file match {
      case f if f.filename.isEmpty => Left(messagesApi("uploadSupporting.error.empty"))
      case f if hasInvalidContentType(f) => Left(messagesApi("uploadSupporting.error.content"))
      case f if hasValidSize(f) => Left(messagesApi("uploadSupporting.error.size"))
      case _ => Right(file)
    }
  }


  private val validContentType = Seq("application/pdf, application/msword, application/vnd.ms-excel, " +
    "image/png, application/vnd.openxmlformats-officedocument.wordprocessingml.document," +
    " application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, image/jpeg, text/plain")

  private def hasInvalidContentType: MultipartFormData.FilePart[TemporaryFile] => Boolean = { f =>
    f.contentType match {
      case Some(c: String) if validContentType.contains(c) => false
      case _ => true
    }
  }

  private val maxFileSize = 10 * 1024 * 1024

  private def hasValidSize: MultipartFormData.FilePart[TemporaryFile] => Boolean = { f =>
    f.ref.file.length() > maxFileSize
  }


}
