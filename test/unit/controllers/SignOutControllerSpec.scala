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

package controllers

import connectors.DataCacheConnector
import controllers.actions.{DataRequiredActionImpl, FakeIdentifierAction}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.scalatest.mockito.MockitoSugar._
import play.api.mvc.{MessagesControllerComponents, Result}
import play.api.test.Helpers._

import scala.concurrent.Future

class SignOutControllerSpec extends ControllerSpecBase {

  private val mcc = app.injector.instanceOf[MessagesControllerComponents]


  "Sign out controller" must {

    "return 200 for a start feedback survey" in {
      val result: Future[Result] = new SignOutController(frontendAppConfig, mock[DataCacheConnector], FakeIdentifierAction(messagesControllerComponents), getEmptyCacheMap,
        app.injector.instanceOf[DataRequiredActionImpl], mcc).startFeedbackSurvey(fakeRequestWithEori)
      status(result) mustBe SEE_OTHER
      redirectLocation(result).get must endWith( "/feedback/ABTIR")
    }

    "clear user cache when present" in {
      val cache = mock[DataCacheConnector]
      new SignOutController(frontendAppConfig, cache, FakeIdentifierAction(messagesControllerComponents), getEmptyCacheMap,
        app.injector.instanceOf[DataRequiredActionImpl], mcc).startFeedbackSurvey(fakeRequestWithEoriAndCache)
      verify(cache).remove(any())
    }
  }

  "Force sign out controller" must {

    "return 200 for a start feedback survey" in {
      val result: Future[Result] = new SignOutController(frontendAppConfig, mock[DataCacheConnector], FakeIdentifierAction(messagesControllerComponents), getEmptyCacheMap,
        app.injector.instanceOf[DataRequiredActionImpl], mcc).forceSignOut(fakeRequestWithEori)
      status(result) mustBe SEE_OTHER
      redirectLocation(result).get must endWith( "/this-service-has-been-reset")
    }

    "clear user cache when present" in {
      val cache = mock[DataCacheConnector]
      new SignOutController(frontendAppConfig, cache, FakeIdentifierAction(messagesControllerComponents), getEmptyCacheMap,
        app.injector.instanceOf[DataRequiredActionImpl], mcc).forceSignOut(fakeRequestWithEoriAndCache)
      verify(cache).remove(any())
    }
  }
}
