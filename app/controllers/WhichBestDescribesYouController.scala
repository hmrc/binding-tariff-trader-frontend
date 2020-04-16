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
import forms.WhichBestDescribesYouFormProvider
import javax.inject.Inject
import models.WhichBestDescribesYou.{BusinessOwner, BusinessRepresentative}
import models.requests.DataRequest
import models.{Enumerable, Mode, UserAnswers, WhichBestDescribesYou}
import navigation.Navigator
import pages._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Results}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.whichBestDescribesYou

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WhichBestDescribesYouController @Inject()(
                                                 appConfig: FrontendAppConfig,
                                                 dataCacheConnector: DataCacheConnector,
                                                 navigator: Navigator,
                                                 identify: IdentifierAction,
                                                 getData: DataRetrievalAction,
                                                 requireData: DataRequiredAction,
                                                 formProvider: WhichBestDescribesYouFormProvider,
                                                 cc: MessagesControllerComponents
                                               ) extends FrontendController(cc) with I18nSupport with Enumerable.Implicits {

  private lazy val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(WhichBestDescribesYouPage) match {
      case Some(value) => form.fill(value)
      case _ => form
    }

    Ok(whichBestDescribesYou(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request: DataRequest[AnyContent] =>

    def update(o: WhichBestDescribesYou): UserAnswers = {
      o match {
        case BusinessRepresentative =>
          request.userAnswers.set(WhichBestDescribesYouPage, o)
        case BusinessOwner =>
          request.userAnswers.set(WhichBestDescribesYouPage, o)
            .remove(RegisterBusinessRepresentingPage)
            .remove(UploadWrittenAuthorisationPage)
      }
    }

    def nextPage: WhichBestDescribesYou => Page = {
      case BusinessRepresentative => RegisterBusinessRepresentingPage
      case BusinessOwner => SelectApplicationTypePage
    }

    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        Future.successful(BadRequest(whichBestDescribesYou(appConfig, formWithErrors, mode))),
      selectedOption => {
        val updatedAnswers = update(selectedOption)
        dataCacheConnector.save(updatedAnswers.cacheMap).map(
          _ => Results.Redirect(navigator.nextPage(nextPage(selectedOption), mode)(updatedAnswers))
        )
      }
    )

  }

}
