/*
 * Copyright 2021 HM Revenue & Customs
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
import controllers.routes
import forms.MakeFileConfidentialFormProvider
import javax.inject.Inject
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages._
import play.api.data.Form
import play.api.mvc.{Call, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import views.html.makeFileConfidential

import scala.concurrent.ExecutionContext

class MakeFileConfidentialController @Inject()(
  appConfig: FrontendAppConfig,
  val dataCacheConnector: DataCacheConnector,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  formProvider: MakeFileConfidentialFormProvider,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext) extends MapCachingController[Boolean](cc) {
  lazy val form = formProvider()
  val questionPage = MakeFileConfidentialPage

  override def submitAction(mode: Mode): Call = routes.MakeFileConfidentialController.onSubmit(mode)

  def renderView(preparedForm: Form[(String, Boolean)], submitAction: Call, mode: Mode)(implicit request: DataRequest[_]): HtmlFormat.Appendable = {
    val fileId = request.userAnswers.get(UploadSupportingMaterialMultiplePage).map(_.last.id).get
    makeFileConfidential(appConfig, preparedForm, submitAction, mode, fileId)
  }

}
