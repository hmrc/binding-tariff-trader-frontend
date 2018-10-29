/*
 * Copyright 2018 HM Revenue & Customs
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
import forms.WhichBestDescribesYouFormProvider
import models.{Enumerable, Mode}
import pages.WhichBestDescribesYouPage
import navigation.Navigator
import views.html.whichBestDescribesYou
import models.WhichBestDescribesYou.{Option1, Option2}
import pages.{RegisterBusinessRepresentingPage, SelectApplicationTypePage, WhichBestDescribesYouPage}

import scala.concurrent.Future

class WhichBestDescribesYouController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: WhichBestDescribesYouFormProvider
                                      ) extends FrontendController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode) = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhichBestDescribesYouPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(whichBestDescribesYou(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(whichBestDescribesYou(appConfig, formWithErrors, mode))),
        (value) => {
          val updatedAnswers = request.userAnswers.set(WhichBestDescribesYouPage, value)

          val redirectedPage = value match {
            case Option1 => SelectApplicationTypePage
            case Option2 => RegisterBusinessRepresentingPage
          }

          dataCacheConnector.save(updatedAnswers.cacheMap).map(
            _ =>
              Redirect(navigator.nextPage(redirectedPage, mode)(updatedAnswers))
          )
        }
      )
  }
}
