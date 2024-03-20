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

package controllers.actions

import base.SpecBase
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.auth.core.{AuthConnector, Enrolment, EnrolmentIdentifier, Enrolments}

import scala.concurrent.ExecutionContext

class AuthenticatedIdentifierActionSpec extends SpecBase {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  private val controllerComponents = stubControllerComponents()
  private val mockAuthConnector    = mock[AuthConnector]
  private val enrolmentIdentifier  = EnrolmentIdentifier("EORINumber", "identifierValue")

  private val enrolment  = Enrolment("HMRC-ATAR-ORG", Seq(enrolmentIdentifier), "state")
  private val enrolments = Enrolments(Set(enrolment))

  private val authenticatedIdentifierAction = new AuthenticatedIdentifierAction(
    authConnector = mockAuthConnector,
    cc            = controllerComponents,
    config        = frontendAppConfig
  )

  "AuthenticatedIdentifierAction" when {
    "eori" must {
      "return the expected string identifierValue" in {
        authenticatedIdentifierAction.eori(enrolments) shouldBe Some(enrolmentIdentifier.value)
      }
    }
  }
}
