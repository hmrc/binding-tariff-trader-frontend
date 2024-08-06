/*
 * Copyright 2024 HM Revenue & Customs
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
import controllers.actions._
import forms.CommodityCodeRulingReferenceFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.CommodityCodeRulingReferencePage
import play.api.data.Form
import play.api.mvc.{Call, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import service.DataCacheService
import views.html.commodityCodeRulingReference

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CommodityCodeRulingReferenceController @Inject() (
  appConfig: FrontendAppConfig,
  val dataCacheService: DataCacheService,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  formProvider: CommodityCodeRulingReferenceFormProvider,
  cc: MessagesControllerComponents,
  commodityCodeRulingReferenceView: commodityCodeRulingReference
)(implicit ec: ExecutionContext)
    extends ListEditingController[String](cc) {
  lazy val form: Form[String]                             = formProvider()
  val questionPage: CommodityCodeRulingReferencePage.type = CommodityCodeRulingReferencePage

  def submitAction(mode: Mode): Call = routes.CommodityCodeRulingReferenceController.onSubmit(mode)

  def editSubmitAction(index: Int, mode: Mode): Call =
    routes.CommodityCodeRulingReferenceController.onEditSubmit(index, mode)

  def renderView(preparedForm: Form[String], submitAction: Call, mode: Mode)(implicit
    request: DataRequest[_]
  ): HtmlFormat.Appendable =
    commodityCodeRulingReferenceView(appConfig, preparedForm, submitAction, mode)

}
