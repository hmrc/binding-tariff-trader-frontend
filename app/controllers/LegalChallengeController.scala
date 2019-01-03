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

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.LegalChallengeFormProvider
import models.{Enumerable, Mode}
import pages.{LegalChallengeDetailsPage, LegalChallengePage, SupportingInformationPage}
import navigation.Navigator
import views.html.legalChallenge
import models.LegalChallenge.{Nolegalchallenge, Yeslegalchallenge}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LegalChallengeController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: LegalChallengeFormProvider
                                      ) extends FrontendController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(LegalChallengePage) match {
      case Some(value) => form.fill(value)
      case _ => form
    }

    Ok(legalChallenge(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        Future.successful(BadRequest(legalChallenge(appConfig, formWithErrors, mode))),
      value => {
        val updatedAnswers = request.userAnswers.set(LegalChallengePage, value)

        val redirectedPage = value match {
          case Yeslegalchallenge => LegalChallengeDetailsPage
          case Nolegalchallenge => SupportingInformationPage
        }

        dataCacheConnector.save(updatedAnswers.cacheMap).map(
          _ => Redirect(navigator.nextPage(redirectedPage, mode)(updatedAnswers))
        )
      }
    )

  }

}
