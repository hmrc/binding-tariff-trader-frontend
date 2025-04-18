/*
 * Copyright 2025 HM Revenue & Customs
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
import org.mockito.Mockito.{mock, when}
import play.api.test.Helpers.{redirectLocation, *}
import service.CasesService
import uk.gov.hmrc.http.HeaderCarrier
import views.html.ruling_information

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RulingControllerSpec extends ControllerSpecBase {

  private lazy val givenUserDoesntHaveAnEORI = FakeIdentifierAction(None)
  private val casesService                   = mock(classOf[CasesService])

  val rulingInformationView: ruling_information = app.injector.instanceOf(classOf[views.html.ruling_information])

  private def controller(identifier: IdentifierAction = FakeIdentifierAction) =
    new RulingController(
      frontendAppConfig,
      identifier,
      casesService,
      cc,
      rulingInformationView
    )

  "Ruling Controller" must {

    "return OK and the correct view for a GET" in {
      when(casesService.getCaseForUser(any[String], any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(oCase.btiCaseWithDecision))
      val result = controller().viewRuling("a-ruling")(fakeRequest)

      status(result)        shouldBe OK
      contentAsString(result) should include(oCase.btiCaseWithDecision.reference)
      contentAsString(result) should include(oCase.btiCaseWithDecision.decision.get.bindingCommodityCode)
    }

    "redirect to BeforeYouStart when EORI unavailable" in {

      val result = controller(givenUserDoesntHaveAnEORI).viewRuling("aruling")(fakeRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }
  }
}
