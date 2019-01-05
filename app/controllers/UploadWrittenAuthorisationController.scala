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
import forms.UploadWrittenAuthorisationFormProvider
import javax.inject.Inject
import models.{FileAttachment, Mode}
import navigation.Navigator
import pages._
import play.api.data.FormError
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.Files.TemporaryFile
import play.api.mvc.{Action, AnyContent, MultipartFormData, Result}
import service.FileService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.uploadWrittenAuthorisation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.successful

class UploadWrittenAuthorisationController @Inject()(
                                                      appConfig: FrontendAppConfig,
                                                      override val messagesApi: MessagesApi,
                                                      dataCacheConnector: DataCacheConnector,
                                                      navigator: Navigator,
                                                      identify: IdentifierAction,
                                                      getData: DataRetrievalAction,
                                                      requireData: DataRequiredAction,
                                                      formProvider: UploadWrittenAuthorisationFormProvider,
                                                      fileService: FileService
                                                    ) extends FrontendController with I18nSupport {

  private lazy val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val uploadedFile = request.userAnswers.get(UploadWrittenAuthorisationPage)
    Ok(uploadWrittenAuthorisation(appConfig, form, uploadedFile, mode))
  }

  def onSubmit(mode: Mode): Action[MultipartFormData[TemporaryFile]] = (identify andThen getData andThen requireData)
    .async(parse.multipartFormData) { implicit request =>

      def badRequest(errorKey: String, errorMessage: String): Future[Result] = {
        val storedLetter = request.userAnswers.get(UploadWrittenAuthorisationPage)
        successful(
          BadRequest(
            uploadWrittenAuthorisation(appConfig, form.copy(errors = Seq(FormError(errorKey, errorMessage))), storedLetter, mode)
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

      val letterOfAuthority = request.body.file("letter-of-authority").filter(_.filename.nonEmpty)

      letterOfAuthority match {
        case Some(file) =>
          valid(file) match {
            case Right(validFile) => uploadFile(validFile)
            case Left(errorMessage) => badRequest("validation-error", errorMessage)
          }
        case _ =>
          request.userAnswers.get(UploadWrittenAuthorisationPage) match {
            case Some(_) => successful(Redirect(navigator.nextPage(SelectApplicationTypePage, mode)(request.userAnswers)))
            case _ => badRequest("letter-of-authority", messagesApi("uploadWrittenAuthorisation.error.selectFile"))
          }
      }
    }

  private val maxFileSize = 10485760
  private def valid(file: MultipartFormData.FilePart[TemporaryFile]): Either[String, MultipartFormData.FilePart[TemporaryFile]] = {
    if (file.ref.file.length() > maxFileSize) {
      Left(messagesApi("uploadWrittenAuthorisation.error.size"))
    } else {
      Right(file)
    }
  }

}
