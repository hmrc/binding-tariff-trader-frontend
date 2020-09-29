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
import forms.SelectApplicationTypeFormProvider
import javax.inject.Inject
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.{ ProvideGoodsNamePage, SelectApplicationTypePage, PreviousCommodityCodePage }
import play.api.data.Form
import play.api.mvc.MessagesControllerComponents
import views.html.selectApplicationType

import scala.concurrent.ExecutionContext
import play.twirl.api.HtmlFormat

class SelectApplicationTypeController @Inject()(
  appConfig: FrontendAppConfig,
  val dataCacheConnector: DataCacheConnector,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  formProvider: SelectApplicationTypeFormProvider,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext) extends YesNoCachingController(cc) {
  lazy val form = formProvider()
  val questionPage = SelectApplicationTypePage
  val detailPages = List(PreviousCommodityCodePage)

  def renderView(preparedForm: Form[Boolean], mode: Mode)(implicit request: DataRequest[_]): HtmlFormat.Appendable = {
    val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")
    selectApplicationType(appConfig, preparedForm, goodsName, mode)
  }
}
