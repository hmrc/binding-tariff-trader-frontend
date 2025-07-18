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
import forms.ProvideGoodsNameFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.ProvideGoodsNamePage
import play.api.data.Form
import play.api.mvc.MessagesControllerComponents
import play.twirl.api.HtmlFormat
import service.DataCacheService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ProvideGoodsNameController @Inject() (
  appConfig: FrontendAppConfig,
  val dataCacheService: DataCacheService,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  provideGoodsNameFormProvider: ProvideGoodsNameFormProvider,
  val provide_goods_name_view: views.html.provideGoodsName,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends AnswerCachingController[String](cc) {
  lazy val form: Form[String]                 = provideGoodsNameFormProvider()
  val questionPage: ProvideGoodsNamePage.type = ProvideGoodsNamePage

  def renderView(preparedForm: Form[String], mode: Mode)(implicit request: DataRequest[?]): HtmlFormat.Appendable =
    provide_goods_name_view(appConfig, preparedForm, mode)
}
