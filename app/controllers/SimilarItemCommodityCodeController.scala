/*
 * Copyright 2019 HM Revenue & Customs
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
import navigation.Navigator
import pages.{CommodityCodeRulingReferencePage, LegalChallengePage, SimilarItemCommodityCodePage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import views.html.{legalChallenge, similarItemCommodityCode}

import scala.concurrent.Future

class SimilarItemCommodityCodeController @Inject()(
                                                    appConfig: FrontendAppConfig,
                                                    override val messagesApi: MessagesApi,
                                                    dataCacheConnector: DataCacheConnector,
                                                    navigator: Navigator,
                                                    identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    formProvider: SimilarItemCommodityCodeFormProvider
                                                  ) extends YesNoController[String](dataCacheConnector, navigator) {

  private lazy val form = formProvider()

  override val page = SimilarItemCommodityCodePage
  override val pageDetails = CommodityCodeRulingReferencePage
  override val nextPage = LegalChallengePage

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(SimilarItemCommodityCodePage) match {
      case Some(value) => form.fill(value)
      case _ => form
    }

    Ok(similarItemCommodityCode(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>


    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        Future.successful(BadRequest(legalChallenge(appConfig, formWithErrors, mode))),
      value => applyAnswer(value, mode)
    )
  }

}
