/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.SimilarItemCommodityCodeFormProvider

import javax.inject.Inject
import models.Mode
import models.requests.DataRequest
import navigation.{Journey, LoopingJourney, Navigator}
import play.api.data.Form
import play.api.mvc.MessagesControllerComponents
import play.twirl.api.HtmlFormat
import views.html.similarItemCommodityCode

import scala.concurrent.ExecutionContext

class SimilarItemCommodityCodeController @Inject()(
                                                    appConfig: FrontendAppConfig,
                                                    val dataCacheConnector: DataCacheConnector,
                                                    val navigator: Navigator,
                                                    val identify: IdentifierAction,
                                                    val getData: DataRetrievalAction,
                                                    val requireData: DataRequiredAction,
                                                    formProvider: SimilarItemCommodityCodeFormProvider,
                                                    cc: MessagesControllerComponents,
                                                    similarItemCommodityCodeView: similarItemCommodityCode
                                                  )(implicit ec: ExecutionContext) extends YesNoCachingController(cc) {
  lazy val form: Form[Boolean] = formProvider()
  val journey: LoopingJourney = Journey.similarItem

  def renderView(preparedForm: Form[Boolean], mode: Mode)(implicit request: DataRequest[_]): HtmlFormat.Appendable =
    similarItemCommodityCodeView(appConfig, preparedForm, mode)
}
