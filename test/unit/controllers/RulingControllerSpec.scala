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

import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import models.oCase
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import play.api.test.Helpers.{redirectLocation, status, _}
import service.CasesService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class RulingControllerSpec extends ControllerSpecBase with MockitoSugar {

  private val casesService = mock[CasesService]
  private lazy val givenUserDoesntHaveAnEORI = FakeIdentifierAction(None)

  def controller(identifier: IdentifierAction = FakeIdentifierAction) =
    new RulingController(frontendAppConfig, identifier, casesService, messagesApi)


  "Ruling Controller" must {

    "return OK and the correct view for a GET" in {
      given(casesService.getCaseForUser(any[String], any[String])(any[HeaderCarrier]))
            .willReturn(Future.successful(oCase.btiCaseWithDecision))
      val result = controller().viewRuling("a-ruling")(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include(oCase.btiCaseWithDecision.reference)
      contentAsString(result) must include(oCase.btiCaseWithDecision.decision.get.bindingCommodityCode)
    }

    "redirect to BeforeYouStart when EORI unavailable" in {

      val result = controller(givenUserDoesntHaveAnEORI).viewRuling("aruling")(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }
  }
}
