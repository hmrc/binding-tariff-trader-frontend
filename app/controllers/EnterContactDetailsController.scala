/*
 * Copyright 2024 HM Revenue & Customs
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
import controllers.actions._
import forms.EnterContactDetailsFormProvider
import models.requests.DataRequest
import models.{EnterContactDetails, Mode, UserAnswers}
import navigation.Navigator
import pages.{EnterContactDetailsPage, RegisteredAddressForEoriPage}
import play.api.data.Form
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Results}
import play.twirl.api.HtmlFormat
import service.DataCacheService
import views.html.enterContactDetails

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EnterContactDetailsController @Inject() (
  appConfig: FrontendAppConfig,
  val dataCacheService: DataCacheService,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  formProvider: EnterContactDetailsFormProvider,
  cc: MessagesControllerComponents,
  enterContactDetailsView: enterContactDetails
)(implicit ec: ExecutionContext)
    extends AnswerCachingController[EnterContactDetails](cc) {

  lazy val form: Form[EnterContactDetails]                  = formProvider()
  lazy val formWithTelValidation: Form[EnterContactDetails] = formProvider.formWithMinTelNumber

  val questionPage: EnterContactDetailsPage.type = EnterContactDetailsPage

  override def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request: DataRequest[_] =>
      val preparedForm = request.userAnswers.get(questionPage) match {
        case Some(value) => enterContactDetailsForm(request.userAnswers).fill(value)
        case _           => enterContactDetailsForm(request.userAnswers)
      }
      Ok(renderView(preparedForm, mode))
  }

  override def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request: DataRequest[_] =>
      val badRequest = (formWithErrors: Form[EnterContactDetails]) =>
        Future.successful(Results.BadRequest(renderView(formWithErrors, mode)))
      enterContactDetailsForm(request.userAnswers).bindFromRequest().fold(badRequest, submitAnswer(_, mode))
  }

  def renderView(preparedForm: Form[EnterContactDetails], mode: Mode)(implicit
    request: DataRequest[_]
  ): HtmlFormat.Appendable =
    enterContactDetailsView(appConfig, preparedForm, mode)

  private def enterContactDetailsForm(userAnswers: UserAnswers): Form[EnterContactDetails] =
    if (userAnswers.get(RegisteredAddressForEoriPage).exists(_.country == "GB")) {
      formWithTelValidation
    } else {
      form
    }
}
