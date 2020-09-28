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
import pages._
import play.api.data.FormError
import play.api.i18n.{I18nSupport, Lang}
import play.api.libs.Files.TemporaryFile
import play.api.mvc._
import service.FileService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.uploadSupportingMaterialMultiple

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.successful

class UploadSupportingMaterialMultipleController @Inject()(
                                                            appConfig: FrontendAppConfig,
                                                            dataCacheConnector: DataCacheConnector,
                                                            identify: IdentifierAction,
                                                            getData: DataRetrievalAction,
                                                            requireData: DataRequiredAction,
                                                            formProvider: UploadSupportingMaterialMultipleFormProvider,
                                                            fileService: FileService,
                                                            cc: MessagesControllerComponents
                                                          ) extends FrontendController(cc) with I18nSupport {

  private lazy val form = formProvider()
  private implicit val lang: Lang = appConfig.defaultLang

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")

    Ok(uploadSupportingMaterialMultiple(appConfig, form, goodsName, mode))
  }

  def onSubmit(mode: Mode): Action[MultipartFormData[TemporaryFile]] = (identify andThen getData andThen requireData)
    .async(parse.multipartFormData) { implicit request =>
      def badRequest(errorKey: String, errorMessage: String): Future[Result] = {
        val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")
        successful(BadRequest(
          uploadSupportingMaterialMultiple(appConfig, form.copy(errors = Seq(FormError(errorKey, errorMessage))), goodsName, mode)
        ))
      }

      def saveAndRedirect(file: FileAttachment): Future[Result] = {
        val updatedFiles = request.userAnswers.get(SupportingMaterialFileListPage)
          .map(_ ++ Seq(file)) getOrElse Seq(file)
        val updatedAnswers = request.userAnswers.set(SupportingMaterialFileListPage, updatedFiles)
        dataCacheConnector.save(updatedAnswers.cacheMap)
          .map(_ => Redirect(routes.MakeFileConfidentialController.onPageLoad(file.id, mode)))
      }

      def uploadFile(validFile: MultipartFormData.FilePart[TemporaryFile]): Future[Result] = {
        fileService.upload(validFile) flatMap {
          case file: FileAttachment => saveAndRedirect(file)
          case _ => badRequest("upload-error", "File upload has failed. Try again")
        }
      }

      def hasMaxFiles: Boolean = {
        request.userAnswers.get(SupportingMaterialFileListPage).map(_.size).getOrElse(0) >= 10
      }

      request.body.file("file-input").filter(_.filename.nonEmpty) match {
        case Some(_) if hasMaxFiles => badRequest("validation-error", messagesApi("uploadSupportingMaterialMultiple.error.numberFiles"))
        case Some(file) => fileService.validate(file) match {
          case Right(rightFile) => uploadFile(rightFile)
          case Left(errorMessage) => badRequest("file-input", errorMessage)
        }
        case _ =>
          badRequest("file-input", messagesApi("uploadSupportingMaterialMultiple.upload.selectFile"))
      }

    }

}
