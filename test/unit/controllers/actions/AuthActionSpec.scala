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

import play.api.mvc.{Controller, Result}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import base.SpecBase
import controllers.routes
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class AuthActionSpec extends SpecBase {

  private class Harness(authAction: IdentifierAction) extends Controller {
    def onPageLoad() = authAction { _ => Ok }
  }

  "Auth Action" when {

    "the user hasn't logged in" must {
      "redirect the user to log in " in {
        val result: Future[Result] = handleAuthError(MissingBearerToken())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must beTheLoginPage
      }
    }

    "the user's session has expired" must {
      "redirect the user to log in " in {
        val result: Future[Result] = handleAuthError(BearerTokenExpired())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must beTheLoginPage
      }
    }

    "the user's credentials are invalid" must {
      "redirect the user to log in " in {
        val result: Future[Result] = handleAuthError(InvalidBearerToken())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must beTheLoginPage
      }
    }

    "the user's session cannot be found" must {
      "redirect the user to log in " in {
        val result: Future[Result] = handleAuthError(SessionRecordNotFound())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must beTheLoginPage
      }
    }

    "the user doesn't have sufficient enrolments" must {
      "redirect the user to the unauthorised page" in {
        val result: Future[Result] = handleAuthError(InsufficientEnrolments())

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe unauthorisedLocation
      }
    }

    "the user doesn't have sufficient confidence level" must {
      "redirect the user to the unauthorised page" in {
        val result: Future[Result] = handleAuthError(InsufficientConfidenceLevel())

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe unauthorisedLocation
      }
    }

    "the user used an unaccepted auth provider" must {
      "redirect the user to the unauthorised page" in {
        val result: Future[Result] = handleAuthError(UnsupportedAuthProvider())

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe unauthorisedLocation
      }
    }

    "the user has an unsupported affinity group" must {
      "redirect the user to the unauthorised page" in {
        val result: Future[Result] = handleAuthError(UnsupportedAffinityGroup())

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe unauthorisedLocation
      }
    }

    "the user has an unsupported credential role" must {
      "redirect the user to the unauthorised page" in {
        val result: Future[Result] = handleAuthError(UnsupportedCredentialRole())

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe unauthorisedLocation
      }
    }

  }

  private def unauthorisedLocation = {
    Some(routes.UnauthorisedController.onPageLoad().url)
  }

  private def beTheLoginPage = {
    startWith(frontendAppConfig.loginUrl)
  }

  private def handleAuthError(exc: AuthorisationException): Future[Result] = {
    val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(exc), frontendAppConfig, messagesControllerComponents)
    val controller = new Harness(authAction)
    controller.onPageLoad()(fakeRequest)
  }

}

class FakeFailingAuthConnector(exceptionToReturn: Throwable) extends AuthConnector {

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = {
    Future.failed(exceptionToReturn)
  }

}
