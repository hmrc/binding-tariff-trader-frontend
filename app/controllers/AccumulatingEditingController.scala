/*
 * Copyright 2022 HM Revenue & Customs
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

import models.Mode
import models.requests.DataRequest
import play.api.data.Form
import play.api.libs.json.Format
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Results}

import scala.concurrent.{ExecutionContext, Future}

abstract class ListEditingController[A](cc: MessagesControllerComponents)(implicit ec: ExecutionContext, format: Format[A])
  extends AccumulatingEditingController[List[A], A, Int](cc) with ListAnswerEditing[A]

abstract class MapEditingController[V](cc: MessagesControllerComponents)(implicit ec: ExecutionContext, format: Format[V])
  extends AccumulatingEditingController[Map[String, V], (String, V), String](cc) with MapAnswerEditing[String, V]

abstract class AccumulatingEditingController[F <: TraversableOnce[A], A, I](
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext, format: Format[F]) extends AccumulatingCachingController[F,A](cc) with AccumulatingAnswerEditing[F, A, I] {

  def editSubmitAction(index: I, mode: Mode): Call

  def onEditPageLoad(index: I, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request: DataRequest[_] =>
    Ok(renderView(form, editSubmitAction(index, mode), mode))
  }

  def onEditSubmit(index: I, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request: DataRequest[_] =>
    val badRequest = (formWithErrors: Form[A]) => Future.successful(Results.BadRequest(renderView(formWithErrors, editSubmitAction(index, mode), mode)))
    form.bindFromRequest().fold(badRequest, editAnswer(index, _, mode))
  }
}
