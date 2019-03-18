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
import models.CaseStatus.CaseStatus
import models._
import models.oCase._
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import play.api.test.Helpers._
import service.CasesService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class IndexControllerSpec extends ControllerSpecBase with MockitoSugar {

  private val casesService = mock[CasesService]
  private lazy val givenUserDoesntHaveAnEORI = FakeIdentifierAction(None)
  private def controller(identifier: IdentifierAction = FakeIdentifierAction): IndexController = new IndexController(
    frontendAppConfig,
    identifier,
    casesService,
    messagesApi
  )

  "Index Controller - Get Applications" must {

    "return the correct view for a load applications" in {

      given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
        .willReturn(Future.successful(Paged(Seq(btiCaseExample), 1, 10, 0)))

      val result = controller().getApplications(page = 1)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include("applications-list-table")
    }

    "return 200 and show no results for a GET when no applications are found" in {
      given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
        .willReturn(Future.successful(Paged(Seq[Case](), 1, 10, 0)))

      val result = controller().getApplications(page = 1)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include("application-list-empty")

    }

    "redirect to BeforeYouStart when EORI unavailable" in {
      val result = controller(givenUserDoesntHaveAnEORI).getApplications(page = 1)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }

  }

  "Index Controller - Get Rulings" should {

    "return the correct view for a load rulings" in {

      given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
        .willReturn(Future.successful(Paged(Seq(btiCaseWithDecision), 1, 10, 0)))

      val result = controller().getRulings(page = 1)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include("rulings-list-table")
    }

    "return 200 and show no results  for a GET when no rulings are found" in {

      given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
        .willReturn(Future.successful(Paged(Seq[Case](), 1, 10, 0)))

      val result = controller().getRulings(page = 1)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include("ruling-list-empty")
    }

    "redirect to BeforeYouStart when EORI unavailable" in {
      val result = controller(givenUserDoesntHaveAnEORI).getRulings(page = 1)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }

  }

}
