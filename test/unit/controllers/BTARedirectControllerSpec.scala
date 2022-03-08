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

package unit.controllers

import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import controllers.{BTARedirectController, ControllerSpecBase, routes}
import models.requests.{BTARequest, IdentifierRequest}
import org.mockito.Mockito.when
import play.api.Application
import play.api.http.Status.{BAD_GATEWAY, BAD_REQUEST, SEE_OTHER}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, AnyContentAsJson, AnyContentAsText}
import play.api.test.{FakeHeaders, FakeRequest}
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation}
import service.URLCacheService
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.Future

class BTARedirectControllerSpec extends ControllerSpecBase {

  val urlCacheService: URLCacheService = mock[URLCacheService]
  val btaCallBackURL = "testBTAUrl"
  val validJson: JsValue = Json.obj("url" -> btaCallBackURL)
  val cacheMap = new CacheMap("testId", Map.empty)

  lazy val btaApp: Application = GuiceApplicationBuilder()
    .configure(
      "metrics.jvm" -> false,
      "metrics.enabled" -> false,
      "toggle.samplesNotAccepted" -> false)
    .overrides(
      bind[IdentifierAction].toInstance(FakeIdentifierAction),
      bind[URLCacheService].toInstance(urlCacheService)
    ).build()

  val controller: BTARedirectController = btaApp.injector.instanceOf[BTARedirectController]

  "btaRedirect" should {
    "redirect to Applications And Rulings" when {
      "a url has been successfully cached" in {
        val request = createFakePostRequest(validJson)
        when(urlCacheService.saveBTACallbackURL(request.identifier, btaCallBackURL)).thenReturn(Future.successful(cacheMap))
        val result = controller.bta(request)

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.IndexController.getApplicationsAndRulings(sortBy = None, order = None).url)
      }
    }

    "return a BadRequest result" when {
      "the input is not JSON" in {
        val request =  fakeRequestWithIdentifier(FakeRequest( "POST", "/", FakeHeaders(), AnyContentAsText("testUrl")))
        when(urlCacheService.saveBTACallbackURL(request.identifier, btaCallBackURL)).thenReturn(Future.successful(cacheMap))
        val result = controller.bta(request)

        status(result) shouldBe BAD_REQUEST
      }

      "the input is not valid json" in {
        val invalidJson: JsValue = Json.obj("invalid" -> "bta")
        val request =  createFakePostRequest(invalidJson)
        when(urlCacheService.saveBTACallbackURL(request.identifier, btaCallBackURL)).thenReturn(Future.successful(cacheMap))
        val result = controller.bta(request)

        status(result) shouldBe BAD_REQUEST
      }
    }

    "return a BadGateway result" when {
      "an error occurs whilst caching" in {
        val request = createFakePostRequest(validJson)
        when(urlCacheService.saveBTACallbackURL(request.identifier, btaCallBackURL)).thenReturn(Future.failed(new RuntimeException))
        val result = controller.bta(request)

        status(result) shouldBe BAD_GATEWAY
      }
    }
  }

  private def createFakePostRequest(json: JsValue): IdentifierRequest[AnyContent] = {
    fakeRequestWithIdentifier(FakeRequest( "POST", "/", FakeHeaders(), AnyContentAsJson(json)))
  }
}
