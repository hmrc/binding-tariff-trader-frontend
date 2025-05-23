/*
 * Copyright 2025 HM Revenue & Customs
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
import forms.MakeFileConfidentialFormProvider
import models.requests.DataRequest
import models.{FileAttachment, Mode}
import navigation.Navigator
import pages.*
import play.api.data.Form
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import service.DataCacheService
import views.html.makeFileConfidential

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class MakeFileConfidentialController @Inject() (
  appConfig: FrontendAppConfig,
  val dataCacheService: DataCacheService,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  formProvider: MakeFileConfidentialFormProvider,
  cc: MessagesControllerComponents,
  makeFileConfidentialView: makeFileConfidential
)(implicit ec: ExecutionContext)
    extends MapCachingController[Boolean](cc) {

  lazy val form: Form[(String, Boolean)]          = formProvider()
  val questionPage: MakeFileConfidentialPage.type = MakeFileConfidentialPage

  override def submitAction(mode: Mode): Call = routes.MakeFileConfidentialController.onSubmit(mode)

  def renderView(preparedForm: Form[(String, Boolean)], submitAction: Call, mode: Mode)(implicit
    request: DataRequest[?]
  ): HtmlFormat.Appendable = {
    val fileId: String = request.userAnswers.get(UploadSupportingMaterialMultiplePage).map(_.last.id).get
      makeFileConfidentialView(appConfig, preparedForm, submitAction, mode, fileId)
  }

  override def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request:DataRequest[?] =>
      val files = request.userAnswers.get(UploadSupportingMaterialMultiplePage)
      if(files.get.isEmpty){
        val fileId = request.id.toString
        Redirect(routes.UploadSupportingMaterialMultipleController.onPageLoad(Some(fileId), mode))
      } else {
        Ok(renderView(form, submitAction(mode), mode))
      }
  }

}
