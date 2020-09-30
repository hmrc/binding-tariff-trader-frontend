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

import controllers.actions._
import models.Mode
import models.requests.DataRequest
import play.api.data.Form
import play.api.mvc.{ Action, AnyContent, MessagesControllerComponents, Results }
import play.api.i18n.I18nSupport
import play.api.libs.json.Format
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ ExecutionContext, Future }

abstract class YesNoCachingController(cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends AnswerCachingController[Boolean](cc) with YesNoCaching

abstract class AnswerCachingController[A](cc: MessagesControllerComponents)(implicit ec: ExecutionContext, format: Format[A]) extends FrontendController(cc) with I18nSupport with AnswerCaching[A] {
  def identify: IdentifierAction
  def getData: DataRetrievalAction
  def requireData: DataRequiredAction
  def form: Form[A]

  def renderView(preparedForm: Form[A], mode: Mode)(implicit request: DataRequest[_]): HtmlFormat.Appendable

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request: DataRequest[_] =>
    val preparedForm = request.userAnswers.get(questionPage) match {
      case Some(value) => form.fill(value)
      case _ => form
    }

    Ok(renderView(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request: DataRequest[_] =>
    val badRequest = (formWithErrors: Form[A]) => Future.successful(Results.BadRequest(renderView(formWithErrors, mode)))
    form.bindFromRequest().fold(badRequest, submitAnswer(_, mode))
  }
}