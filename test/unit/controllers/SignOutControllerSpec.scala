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

import connectors.DataCacheConnector
import controllers.actions.{DataRequiredActionImpl, FakeIdentifierAction}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.Future

class SignOutControllerSpec extends ControllerSpecBase {

  private def controller = new SignOutController(
    frontendAppConfig,
    mock[DataCacheConnector],
    FakeIdentifierAction,
    getEmptyCacheMap,
    new DataRequiredActionImpl,
    cc
  )

  "Sign out controller" must {

    "return 200 for a start feedback survey" in {
      val result: Future[Result] = controller.startFeedbackSurvey(fakeRequestWithEori)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get should endWith("/feedback/ABTIR")
    }

    "clear user cache when present" in {
      val cache = mock[DataCacheConnector]
      controller.startFeedbackSurvey(fakeRequestWithEoriAndCache)
      verify(cache).remove(any())
    }
  }


  "Force sign out controller" must {

    "return 200 for a start feedback survey" in {
      val result: Future[Result] = controller.forceSignOut(fakeRequestWithEori)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get should endWith("/this-service-has-been-reset")
    }

    "clear user cache when present" in {
      val cache = mock[DataCacheConnector]
      controller.forceSignOut(fakeRequestWithEoriAndCache)
      verify(cache).remove(any())
    }
  }

  "Unauthorised sign out controller" must {

    "return a redirect and clear session" in {
      val result: Future[Result] = controller.unauthorisedSignOut(fakeRequest)
      status(result) shouldBe SEE_OTHER
      cookies(result).get("mdtp").isDefined shouldBe true
      redirectLocation(result).get should endWith("/applications")
    }

  }
}
