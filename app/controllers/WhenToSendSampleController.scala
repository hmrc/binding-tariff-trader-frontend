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
import forms.WhenToSendSampleFormProvider
import javax.inject.Inject
import models.{Mode, ReturnSamples}
import navigation.Navigator
import pages._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.whenToSendSample

import scala.concurrent.Future

class WhenToSendSampleController @Inject()(
                                            appConfig: FrontendAppConfig,
                                            override val messagesApi: MessagesApi,
                                            override val dataCacheConnector: DataCacheConnector,
                                            override val navigator: Navigator,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            formProvider: WhenToSendSampleFormProvider
                                          ) extends FrontendController with I18nSupport with YesNoBehaviour[ReturnSamples] {

  private lazy val form = formProvider()

  override val page = WhenToSendSamplePage
  override val pageDetails = ReturnSamplesPage
  override val nextPage = SimilarItemCommodityCodePage

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(WhenToSendSamplePage) match {
      case None => form
      case Some(value) => form.fill(value)
    }

    Ok(whenToSendSample(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        Future.successful(BadRequest(whenToSendSample(appConfig, formWithErrors, mode))),
      value => applyAnswer(value, mode)
    )
  }

}
