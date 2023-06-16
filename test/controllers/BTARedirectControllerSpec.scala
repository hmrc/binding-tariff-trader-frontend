/*
 * Copyright 2023 HM Revenue & Customs
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

import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import play.api.Application
import play.api.http.Status.SEE_OTHER
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation}
import service.BTAUserService
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.Future

class BTARedirectControllerSpec extends ControllerSpecBase {

  val btaUserService: BTAUserService = mock[BTAUserService]
  val cacheMap                       = new CacheMap("testId", Map.empty)
  val testHost                       = "testHost"
  val testUrl                        = s"$testHost/business-account"

  lazy val btaApp: Application = GuiceApplicationBuilder()
    .configure(
      "toggle.samplesNotAccepted" -> false,
      "business-tax-account.host" -> testHost
    )
    .overrides(
      bind[IdentifierAction].toInstance(FakeIdentifierAction),
      bind[BTAUserService].toInstance(btaUserService)
    )
    .build()

  override def beforeEach(): Unit =
    reset(btaUserService)

  val controller: BTARedirectController = btaApp.injector.instanceOf[BTARedirectController]

  "applicationsAndRulings" should {
    "redirect to Applications And Rulings" when {
      "a bta user flag has been successfully saved" in {
        val request = fakeRequestWithIdentifier()
        when(btaUserService.save(request.identifier)).thenReturn(Future.successful(cacheMap))
        val result = controller.applicationsAndRulings(request)

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(
          routes.IndexController.getApplicationsAndRulings(sortBy = None, order = None).url
        )
      }
    }

    "redirect to an Error Page" when {
      "an error occurs whilst saving" in {
        val request = fakeRequestWithIdentifier()
        when(btaUserService.save(request.identifier)).thenReturn(Future.failed(new RuntimeException))
        val result = controller.applicationsAndRulings(request)

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ErrorController.onPageLoad.url)
      }
    }
  }

  "informationYouNeed" should {
    "redirect to Before You Start view" when {
      "a bta user flag has been successfully saved" in {
        val request = fakeRequestWithIdentifier()
        when(btaUserService.save(request.identifier)).thenReturn(Future.successful(cacheMap))
        val result = controller.informationYouNeed(request)

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.BeforeYouStartController.onPageLoad().url)
      }
    }

    "redirect to an Error Page" when {
      "an error occurs whilst saving" in {
        val request = fakeRequestWithIdentifier()
        when(btaUserService.save(request.identifier)).thenReturn(Future.failed(new RuntimeException))
        val result = controller.informationYouNeed(request)

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ErrorController.onPageLoad.url)
      }
    }

  }

  val redirectScenarios: List[(String, () => Future[Boolean])] = List(
    ("been successfully removed", () => Future.successful(true)),
    ("not been successfully removed", () => Future.failed(new RuntimeException("Remove Error")))
  )

  "redirectToBTA" should {
    "redirect to BTA" when {
      redirectScenarios foreach { data =>
        s"a btaUser has ${data._1}" in {
          val request = fakeRequestWithIdentifier()
          when(btaUserService.remove(request.identifier)).thenReturn(data._2())
          val result = controller.redirectToBTA(request)

          status(result)           shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(testUrl)
        }
      }
    }
  }
}
