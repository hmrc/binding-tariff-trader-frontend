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
import models._
import navigation.Navigator
import pages._
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Results}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.selectApplicationType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SelectApplicationTypeController @Inject()(
                                                 appConfig: FrontendAppConfig,
                                                 dataCacheConnector: DataCacheConnector,
                                                 navigator: Navigator,
                                                 identify: IdentifierAction,
                                                 getData: DataRetrievalAction,
                                                 requireData: DataRequiredAction,
                                                 formProvider: SelectApplicationTypeFormProvider,
                                                 cc: MessagesControllerComponents
                                               ) extends FrontendController(cc) with I18nSupport with Enumerable.Implicits {

  private lazy val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")
    val preparedForm = request.userAnswers.get(SelectApplicationTypePage) match {
      case Some(value) => form.fill(value)
      case _ => form
    }

    Ok(selectApplicationType(appConfig, preparedForm, goodsName, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    def nextPage: Boolean => Page = {
      case true => PreviousCommodityCodePage
      case false => AcceptItemInformationPage
    }

    val goodsName = request.userAnswers.get(ProvideGoodsNamePage).getOrElse("goods")

    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        Future.successful(BadRequest(selectApplicationType(appConfig, formWithErrors, goodsName, mode))),
      selectedOption => {
        val updatedAnswers = request.userAnswers.set(SelectApplicationTypePage, selectedOption)
        dataCacheConnector.save(updatedAnswers.cacheMap).map(
          _ => Results.Redirect(navigator.nextPage(nextPage(selectedOption), mode)(updatedAnswers))
        )
      }
    )
  }

}
