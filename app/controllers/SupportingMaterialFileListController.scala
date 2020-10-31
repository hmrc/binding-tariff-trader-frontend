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
import forms.SupportingMaterialFileListFormProvider
import javax.inject.Inject
import models.requests.DataRequest
import models.{FileAttachment, Mode, UserAnswers}
import navigation.Navigator
import pages._
import play.api.data.Form
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Results}

import scala.concurrent.{ExecutionContext, Future}
import viewmodels.FileView
import views.html.supportingMaterialFileList
import play.twirl.api.HtmlFormat

class SupportingMaterialFileListController @Inject()(
  appConfig: FrontendAppConfig,
  val dataCacheConnector: DataCacheConnector,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  formProvider: SupportingMaterialFileListFormProvider,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext) extends AnswerCachingController[Boolean](cc) {
  lazy val form = formProvider()
  val questionPage = SupportingMaterialFileListPage

  val FormInputField = "add-file-choice"
  val MaxFilesMessage = "supportingMaterialFileList.error.numberFiles"

  private def exceedsMaxFiles(userAnswers: UserAnswers): Boolean = {
    val numberOfFiles = userAnswers
      .get(UploadSupportingMaterialMultiplePage)
      .map(_.size)
      .getOrElse(0)

    numberOfFiles > appConfig.fileUploadMaxFiles
  }

  private def hasMaxFiles(userAnswers: UserAnswers): Boolean = {
    val numberOfFiles = userAnswers
      .get(UploadSupportingMaterialMultiplePage)
      .map(_.size)
      .getOrElse(0)

    numberOfFiles >= appConfig.fileUploadMaxFiles
  }

  def removeFile(id: String, userAnswers: UserAnswers): UserAnswers = {
    val files = userAnswers.get(UploadSupportingMaterialMultiplePage).getOrElse(Seq.empty[FileAttachment])
    val remainingFiles = files.filterNot(_.id == id)
    val confidentialityStatuses = userAnswers.get(MakeFileConfidentialPage).getOrElse(Map.empty[String, Boolean])
    val remainingStatuses = confidentialityStatuses.filterKeys(_ != id)

    val updatedAnswers = userAnswers
      .set(UploadSupportingMaterialMultiplePage, remainingFiles)
      .set(MakeFileConfidentialPage, remainingStatuses)

    if (remainingFiles.isEmpty)
      updatedAnswers.remove(AddSupportingDocumentsPage)
    else
      updatedAnswers
  }

  def onRemove(fileId: String, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val updatedAnswers = removeFile(fileId, request.userAnswers)

    val onwardRoute = if (updatedAnswers.get(AddSupportingDocumentsPage).isEmpty)
      routes.AddSupportingDocumentsController.onPageLoad(mode)
    else
      routes.SupportingMaterialFileListController.onPageLoad(mode)

    dataCacheConnector
      .save(updatedAnswers.cacheMap)
      .map { _ => Redirect(onwardRoute) }
  }

  def getFileViews(userAnswers: UserAnswers): Seq[FileView] = {
    val files = userAnswers.get(UploadSupportingMaterialMultiplePage).getOrElse(Seq.empty[FileAttachment])
    val confidentialityStatuses = userAnswers.get(MakeFileConfidentialPage).getOrElse(Map.empty[String, Boolean])
    files.filter(_.uploaded).map { file => FileView(file.id, file.name, confidentialityStatuses(file.id)) }
  }

  def renderView(preparedForm: Form[Boolean], mode: Mode)(implicit request: DataRequest[_]): HtmlFormat.Appendable = {
    val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")
    // We will not use the prepared form here because we don't want to prepopulate the choice; we will only ensure existing errors are populated
    supportingMaterialFileList(appConfig, form.copy(errors = preparedForm.errors), goodsName, getFileViews(request.userAnswers), mode)
  }

  override def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request: DataRequest[_] =>
    val badRequest = (formWithErrors: Form[Boolean]) => Future.successful(BadRequest(renderView(formWithErrors, mode)))
    form.bindFromRequest().fold(badRequest, { choice =>
      if (choice && hasMaxFiles(request.userAnswers))
        badRequest(form.withError(FormInputField, MaxFilesMessage, appConfig.fileUploadMaxFiles))
      else if (exceedsMaxFiles(request.userAnswers))
        badRequest(form.withError(FormInputField, MaxFilesMessage, appConfig.fileUploadMaxFiles))
      else
        submitAnswer(choice, mode)
    })
  }
}
