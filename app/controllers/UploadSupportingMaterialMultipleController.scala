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
import java.{util => ju}

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
)(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport {
  private lazy val form = formProvider()
  private implicit val lang: Lang = appConfig.defaultLang

  def hasMaxFiles(userAnswers: UserAnswers): Boolean = {
    val numberOfFiles = userAnswers
      .get(UploadSupportingMaterialMultiplePage)
      .map(_.size)
      .getOrElse(0)

    numberOfFiles >= 10
  }

  def addFile(file: FileAttachment, userAnswers: UserAnswers): UserAnswers = {
    val updatedFiles = userAnswers
      .get(UploadSupportingMaterialMultiplePage)
      .map(files => files :+ file)
      .getOrElse(Seq(file))

    userAnswers.set(UploadSupportingMaterialMultiplePage, updatedFiles)
  }

  def uploadFile(validFile: MultipartFormData.FilePart[TemporaryFile])(implicit hc: HeaderCarrier, request: DataRequest[_]): Future[UserAnswers] = {
    for {
      attachment <- fileService.upload(validFile)
      updatedAnswers = addFile(attachment, request.userAnswers)
      _ <- dataCacheConnector.save(updatedAnswers.cacheMap)
    } yield updatedAnswers
  }

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    fileService.initiate(FileStoreInitiateRequest(
      successRedirect = Some(routes.MakeFileConfidentialController.onPageLoad(mode).absoluteURL),
      errorRedirect = Some(routes.UploadSupportingMaterialMultipleController.onPageLoad(mode).absoluteURL)
    )).transformWith {
      case Failure(NonFatal(_)) =>
        Future.successful(BadGateway)
      case Success(response) =>
        val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")
        Future.successful(Ok(uploadSupportingMaterialMultiple(appConfig, response, form, goodsName, mode)))
    }
  }

  def onSubmit(mode: Mode): Action[MultipartFormData[TemporaryFile]] = (identify andThen getData andThen requireData)
    .async(parse.multipartFormData) { implicit request =>

      def badRequest(mode: Mode, errorKey: String, errorMessage: String): Future[Result] = {
        fileService.initiate(FileStoreInitiateRequest()).map { response =>
          val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")
          val formWithError = form.withError(errorKey, errorMessage)
          BadRequest(uploadSupportingMaterialMultiple(appConfig, response, formWithError, goodsName, mode))
        }
      }

      request.body.file("file-input").filter(_.filename.nonEmpty) match {
        case _ if hasMaxFiles(request.userAnswers) =>
          badRequest(mode, "validation-error", messagesApi("uploadSupportingMaterialMultiple.error.numberFiles"))
        case Some(file) =>
          fileService.validate(file).fold(
            errorMessage =>
              badRequest(mode, "file-input", errorMessage),
            validFile => {
              uploadFile(validFile).transformWith {
                case Failure(NonFatal(_)) =>
                  Future.successful(Results.BadGateway)
                case Success(updatedAnswers) =>
                  Future.successful(Results.Redirect(navigator.nextPage(UploadSupportingMaterialMultiplePage, mode)(updatedAnswers)))
              }
            }
          )
        case _ =>
          badRequest(mode, "file-input", messagesApi("uploadSupportingMaterialMultiple.upload.selectFile"))
      }
    }
}
