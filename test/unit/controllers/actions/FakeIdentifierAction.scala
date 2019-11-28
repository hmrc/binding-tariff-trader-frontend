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

package controllers.actions

import models.requests.IdentifierRequest
import play.api.mvc.{AnyContent, BodyParser, BodyParsers, PlayBodyParsers, Request, Result}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

class FakeIdentifierAction(eori: Option[String] = Some("eori-789012")) extends IdentifierAction {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    block(IdentifierRequest(request, "id", eori))
  }


  override def parser: BodyParser[AnyContent] = ???

  override protected def executionContext: ExecutionContext = global
}

object FakeIdentifierAction {
  def apply(): IdentifierAction = new FakeIdentifierAction()
  def apply(eori: Option[String]): IdentifierAction = new FakeIdentifierAction(eori)
}