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

import connectors.DataCacheConnector
import controllers.actions.FakeIdentifierAction
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{verify, when}
import org.scalatest.BeforeAndAfterEach
import play.api.mvc.Result
import play.api.test.Helpers._
import service.URLCacheService

import scala.concurrent.{ExecutionContext, Future}

class SignOutControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  private val dataCache: DataCacheConnector = mock[DataCacheConnector]
  private val urlCacheService: URLCacheService = mock[URLCacheService]
  private implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    Mockito.reset(dataCache, urlCacheService)
  }

  private def controller = new SignOutController(
    frontendAppConfig,
    dataCache,
    urlCacheService,
    FakeIdentifierAction,
    getEmptyCacheMap,
    cc
  )

  "Sign out controller" must {

    "return 200 for a start feedback survey" in {
      val result: Future[Result] = controller.startFeedbackSurvey(fakeRequestWithEori)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get should endWith("/feedback/ABTIR")
    }

    "clear user cache when present" in {
      await(controller.startFeedbackSurvey(fakeRequestWithEoriAndCache))
      verify(dataCache).remove(any())
    }
  }


  "Force sign out controller" must {

    "return 200 for a start feedback survey" in {
      val request = fakeRequestWithEori
      when(urlCacheService.removeBTACallbackURL(request.internalId)).thenReturn(Future.successful(true))

      val result: Future[Result] = controller.forceSignOut(request)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get should endWith("/this-service-has-been-reset")
    }

    "clear user cache when present" in {
      val request = fakeRequestWithEori
      when(urlCacheService.removeBTACallbackURL(request.internalId)).thenReturn(Future.successful(true))

      await(controller.forceSignOut(request))
      verify(dataCache).remove(any())
      verify(urlCacheService).removeBTACallbackURL(request.internalId)
    }
  }

  "Unauthorised sign out controller" must {

    "return a redirect and clear session" in {
      val result: Future[Result] = controller.unauthorisedSignOut(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get should endWith("/applications")
      cookies(result).size shouldBe 0
    }
  }

  "Cancel application controller" must {
    "return a redirect and clear session" in {
      val result: Future[Result] = controller.cancelApplication(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get should endWith("/applications-and-rulings")
      cookies(result).size shouldBe 0
    }
  }
}
