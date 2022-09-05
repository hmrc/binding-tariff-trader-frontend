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

package controllers.actions

import base.SpecBase
import controllers.routes
import play.api.mvc._
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase {

  private class Harness(authAction: IdentifierAction) extends BaseController {
    def onPageLoad(): Action[AnyContent] = authAction(_ => Ok)

    override protected def controllerComponents: ControllerComponents = cc
  }

  "Auth Action" when {

    "the user hasn't logged in" must {
      "redirect the user to log in " in {
        val result: Future[Result] = handleAuthError(MissingBearerToken())

        status(result)               shouldBe SEE_OTHER
        redirectLocation(result).get should beTheLoginPage
      }
    }

    "the user's session has expired" must {
      "redirect the user to log in " in {
        val result: Future[Result] = handleAuthError(BearerTokenExpired())

        status(result)               shouldBe SEE_OTHER
        redirectLocation(result).get should beTheLoginPage
      }
    }

    "the user's credentials are invalid" must {
      "redirect the user to log in " in {
        val result: Future[Result] = handleAuthError(InvalidBearerToken())

        status(result)               shouldBe SEE_OTHER
        redirectLocation(result).get should beTheLoginPage
      }
    }

    "the user's session cannot be found" must {
      "redirect the user to log in " in {
        val result: Future[Result] = handleAuthError(SessionRecordNotFound())

        status(result)               shouldBe SEE_OTHER
        redirectLocation(result).get should beTheLoginPage
      }
    }

    "the user doesn't have sufficient enrolments" must {
      "redirect the user to the unauthorised page" in {
        val result: Future[Result] = handleAuthError(InsufficientEnrolments())

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe atarSubscribeLocation
      }
    }

    "the user doesn't have sufficient confidence level" must {
      "redirect the user to the unauthorised page" in {
        val result: Future[Result] = handleAuthError(InsufficientConfidenceLevel())

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe unauthorisedLocation
      }
    }

    "the user used an unaccepted auth provider" must {
      "redirect the user to the unauthorised page" in {
        val result: Future[Result] = handleAuthError(UnsupportedAuthProvider())

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe unauthorisedLocation
      }
    }

    "the user has an unsupported affinity group" must {
      "redirect the user to the unauthorised page" in {
        val result: Future[Result] = handleAuthError(UnsupportedAffinityGroup())

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe unauthorisedLocation
      }
    }

    "the user has an unsupported credential role" must {
      "redirect the user to the unauthorised page" in {
        val result: Future[Result] = handleAuthError(UnsupportedCredentialRole())

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe unauthorisedLocation
      }
    }

  }

  private def unauthorisedLocation =
    Some(routes.UnauthorisedController.onPageLoad.url)

  private def atarSubscribeLocation =
    Some(frontendAppConfig.atarSubscribeUrl)

  private def beTheLoginPage =
    startWith(frontendAppConfig.loginUrl)

  private def handleAuthError(exc: AuthorisationException): Future[Result] = {
    val authAction = new AuthenticatedIdentifierAction(
      new FakeFailingAuthConnector(exc),
      cc,
      frontendAppConfig
    )
    val controller = new Harness(authAction)
    controller.onPageLoad()(fakeRequest)
  }

}

class FakeFailingAuthConnector(exceptionToReturn: Throwable) extends AuthConnector {

  override def authorise[A](
    predicate: Predicate,
    retrieval: Retrieval[A]
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)

}
