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

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.CommodityCodeRulingReferenceFormProvider
import models.Mode
import pages.{CommodityCodeRulingReferencePage, LegalChallengePage}
import navigation.Navigator
import play.api.mvc.{Action, AnyContent}
import views.html.commodityCodeRulingReference

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CommodityCodeRulingReferenceController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: CommodityCodeRulingReferenceFormProvider
                                      , cc: MessagesControllerComponents)extends FrontendController(cc) with I18nSupport {

  private lazy val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(CommodityCodeRulingReferencePage) match {
      case Some(value) => form.fill(value)
      case _ => form
    }

    Ok(commodityCodeRulingReference(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        Future.successful(BadRequest(commodityCodeRulingReference(appConfig, formWithErrors, mode))),
      value => {
        val updatedAnswers = request.userAnswers.set(CommodityCodeRulingReferencePage, value)

        dataCacheConnector.save(updatedAnswers.cacheMap).map(
          _ => Redirect(navigator.nextPage(LegalChallengePage, mode)(updatedAnswers))
        )
      }
    )
  }

}
