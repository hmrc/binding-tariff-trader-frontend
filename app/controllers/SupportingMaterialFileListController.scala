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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import scala.concurrent.ExecutionContext
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

  def removeFile(id: String, userAnswers: UserAnswers): UserAnswers = {
    val files = userAnswers.get(UploadSupportingMaterialMultiplePage).getOrElse(Seq.empty[FileAttachment])
    val confidentialityStatuses = userAnswers.get(MakeFileConfidentialPage).getOrElse(Map.empty[String, Boolean])
    userAnswers
      .set(UploadSupportingMaterialMultiplePage, files.filterNot(_.id == id))
      .set(MakeFileConfidentialPage, confidentialityStatuses.filterKeys(_ != id))
  }

  def onRemove(fileId: String, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val updatedAnswers = removeFile(fileId, request.userAnswers)
    dataCacheConnector.save(updatedAnswers.cacheMap)
      .map(_ => Redirect(routes.SupportingMaterialFileListController.onPageLoad(mode)))
  }

  def getFileViews(userAnswers: UserAnswers): Seq[FileView] = {
    val files = userAnswers.get(UploadSupportingMaterialMultiplePage).getOrElse(Seq.empty[FileAttachment])
    val confidentialityStatuses = userAnswers.get(MakeFileConfidentialPage).getOrElse(Map.empty[String, Boolean])
    files.map { file => FileView(file.id, file.name, confidentialityStatuses(file.id)) }
  }

  def renderView(preparedForm: Form[Boolean], mode: Mode)(implicit request: DataRequest[_]): HtmlFormat.Appendable = {
    val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")
    // We will not use the prepared form here because we don't want to prepopulate the choice
    supportingMaterialFileList(appConfig, form, goodsName, getFileViews(request.userAnswers), mode)
  }
}
