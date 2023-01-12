/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.LegalChallengeDetailsFormProvider
import javax.inject.Inject
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.{LegalChallengeDetailsPage, ProvideGoodsNamePage}
import play.api.data.Form
import play.api.mvc.MessagesControllerComponents
import play.twirl.api.HtmlFormat
import views.html.legalChallengeDetails

import scala.concurrent.ExecutionContext

class LegalChallengeDetailsController @Inject() (
  appConfig: FrontendAppConfig,
  val dataCacheConnector: DataCacheConnector,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  formProvider: LegalChallengeDetailsFormProvider,
  cc: MessagesControllerComponents,
  legalChallengeDetailsView: legalChallengeDetails
)(implicit ec: ExecutionContext)
    extends AnswerCachingController[String](cc) {
  lazy val form: Form[String]                      = formProvider()
  val questionPage: LegalChallengeDetailsPage.type = LegalChallengeDetailsPage

  def renderView(preparedForm: Form[String], mode: Mode)(implicit request: DataRequest[_]): HtmlFormat.Appendable = {
    val goodsName: String = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")
    legalChallengeDetailsView(appConfig, preparedForm, mode, goodsName)
  }
}
