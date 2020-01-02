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
import models.SelectApplicationType.{NewCommodity, PreviousCommodity}
import models._
import navigation.Navigator
import pages._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Results}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.selectApplicationType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SelectApplicationTypeController @Inject()(
                                                 appConfig: FrontendAppConfig,
                                                 override val messagesApi: MessagesApi,
                                                 dataCacheConnector: DataCacheConnector,
                                                 navigator: Navigator,
                                                 identify: IdentifierAction,
                                                 getData: DataRetrievalAction,
                                                 requireData: DataRequiredAction,
                                                 formProvider: SelectApplicationTypeFormProvider
                                               ) extends FrontendController with I18nSupport with Enumerable.Implicits {

  private lazy val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(SelectApplicationTypePage) match {
      case Some(value) => form.fill(value)
      case _ => form
    }

    Ok(selectApplicationType(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    def update(o: SelectApplicationType): UserAnswers = {
      o match {
        case PreviousCommodity => request.userAnswers.set(SelectApplicationTypePage, o)
        case NewCommodity => request.userAnswers.set(SelectApplicationTypePage, o).remove(PreviousCommodityCodePage)
      }
    }

    def nextPage: SelectApplicationType => Page = {
      case PreviousCommodity => PreviousCommodityCodePage
      case NewCommodity => AcceptItemInformationPage
    }

    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        Future.successful(BadRequest(selectApplicationType(appConfig, formWithErrors, mode))),
      selectedOption => {
        val updatedAnswers = update(selectedOption)
        dataCacheConnector.save(updatedAnswers.cacheMap).map(
          _ => Results.Redirect(navigator.nextPage(nextPage(selectedOption), mode)(updatedAnswers))
        )
      }
    )
  }

}
