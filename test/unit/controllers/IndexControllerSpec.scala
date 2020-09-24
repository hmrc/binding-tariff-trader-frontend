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

import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import models.CaseStatus.CaseStatus
import models._
import models.oCase._
import navigation.FakeNavigator
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import play.api.mvc.Call
import play.api.test.Helpers._
import service.CasesService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class IndexControllerSpec extends ControllerSpecBase {

  private lazy val givenUserDoesntHaveAnEORI = FakeIdentifierAction(None)
  private val casesService = mock[CasesService]
  def nextPageRoute = Call("GET", "/advance-tariff-application/information-you-need")


  private def controller(identifier: IdentifierAction = FakeIdentifierAction): IndexController =
    new IndexController(
      frontendAppConfig,
      identifier,
      new FakeNavigator(nextPageRoute),
      casesService,
      cc
    )

  "Index Controller - Get Applications" must {

    "return the correct view for a load applications" in {

      given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
        .willReturn(Future.successful(Paged(Seq(btiCaseExample), 1, 10, 0)))

      val result = controller().getApplications(page = 1)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) should include("applications-list-table")
    }

    "return 200 and show no results for a GET when no applications are found" in {
      given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
        .willReturn(Future.successful(Paged(Seq[Case](), 1, 10, 0)))

      val result = controller().getApplications(page = 1)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) should include("application-list-empty")

    }

    "redirect to BeforeYouStart when EORI unavailable" in {
      val result = controller(givenUserDoesntHaveAnEORI).getApplications(page = 1)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }

    "return the correct view for a load rulings without decision" in {

      given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
        .willReturn(Future.successful(Paged(Seq(btiCaseWithDecision.copy(decision = None)), 1, 10, 0)))

      val result = controller().getApplications(page = 1)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) should include("applications-list-table")
    }

    for(caseStatus <- CaseStatus.values.toSeq) {
      s"return the correct view with correct ruling status in table for case status '$caseStatus'" in {
        val testCase = btiCaseWithDecision.copy(status = caseStatus)
        given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
          .willReturn(Future.successful(Paged(Seq(testCase), 1, 10, 0)))

        val result = controller().getApplications(page = 1)(fakeRequest)

        status(result) shouldBe OK

        val doc = Jsoup.parse(contentAsString(result))
        val actualStatus = doc.getElementById("application-list-row-0-status").text().trim

        val expectedStatus = testCase.status match {
          case CaseStatus.NEW | CaseStatus.OPEN => {
            messages("case.application.status.inProgress")
          }
          case CaseStatus.SUPPRESSED | CaseStatus.REJECTED => {
            messages("case.application.status.rejected")
          }
          case CaseStatus.REFERRED => {
            messages("case.application.status.infoRequested")
          }
          case CaseStatus.COMPLETED | CaseStatus.CANCELLED => {
            messages("case.application.status.completed")
          }
          case CaseStatus.DRAFT => {
            messages("case.application.status.draft")
          }
          case CaseStatus.SUSPENDED => {
            messages("case.application.status.suspended")
          }
          case _ => ""
        }

        actualStatus shouldBe expectedStatus
      }

      s"return the correct view with case bti ruling link in table for case status '$caseStatus'" in {
        val testCase = btiCaseWithDecision.copy(status = caseStatus)
        given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
          .willReturn(Future.successful(Paged(Seq(testCase), 1, 10, 0)))

        val result = controller().getApplications(page = 1)(fakeRequest)

        status(result) shouldBe OK

        val doc = Jsoup.parse(contentAsString(result))
        val actualLinkText = doc.getElementById("application-list-row-0-download").text().trim
        val expectedLinkText = messages("case.application.viewApplication")

        actualLinkText should startWith(expectedLinkText)
      }
    }

  }

  "Index Controller - Get Rulings" should {

    "return the correct view for a load rulings with decision" in {

      given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
        .willReturn(Future.successful(Paged(Seq(btiCaseWithDecision), 1, 10, 0)))

      val result = controller().getRulings(page = 1)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) should include("rulings-list-table")
    }

    "return 200 and show no results  for a GET when no rulings are found" in {

      given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
        .willReturn(Future.successful(Paged(Seq[Case](), 1, 10, 0)))

      val result = controller().getRulings(page = 1)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) should include("ruling-list-empty")
    }

    "redirect to BeforeYouStart when EORI unavailable" in {
      val result = controller(givenUserDoesntHaveAnEORI).getRulings(page = 1)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }

    "return the correct view for a load rulings without decision" in {

      given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
        .willReturn(Future.successful(Paged(Seq(btiCaseWithDecision.copy(decision = None)), 1, 10, 0)))

      val result = controller().getRulings(page = 1)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) should include("rulings-list-table")
    }

    for(caseStatus <- CaseStatus.values.toSeq) {
      s"return the correct view with correct ruling status in table for case status '$caseStatus'" in {
        val testCase = btiCaseWithDecision.copy(status = caseStatus)
        given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
          .willReturn(Future.successful(Paged(Seq(testCase), 1, 10, 0)))

        val result = controller().getRulings(page = 1)(fakeRequest)

        status(result) shouldBe OK

        val doc = Jsoup.parse(contentAsString(result))
        val actualStatus = doc.getElementById("rulings-list-row-0-status").text().trim

        val expectedStatus = testCase.status match {
          case CaseStatus.CANCELLED => messages("case.ruling.status.cancelled")
          case CaseStatus.COMPLETED if testCase.hasActiveDecision => messages("case.ruling.status.active")
          case CaseStatus.COMPLETED if testCase.hasExpiredDecision => messages("case.ruling.status.expired")
          case CaseStatus.REJECTED => messages("case.ruling.status.rejected")
          case CaseStatus.SUSPENDED => messages("case.ruling.status.suspended")
          case CaseStatus.NEW | CaseStatus.OPEN | CaseStatus.REFERRED => { messages("case.ruling.status.in.progress") }
          case _ => testCase.status.toString.toLowerCase.capitalize
        }

        actualStatus shouldBe expectedStatus
      }

      s"return the correct view with case bti ruling link in table for case status '$caseStatus'" in {
        val testCase = btiCaseWithDecision.copy(status = caseStatus)
        given(casesService.getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier]))
          .willReturn(Future.successful(Paged(Seq(testCase), 1, 10, 0)))

        val result = controller().getRulings(page = 1)(fakeRequest)

        status(result) shouldBe OK

        val doc = Jsoup.parse(contentAsString(result))
        val actualLinkText = doc.getElementById("rulings-list-row-0-view").text().trim
        val notShowLinkStatuses = Set(CaseStatus.REJECTED, CaseStatus.SUSPENDED, CaseStatus.NEW, CaseStatus.OPEN, CaseStatus.REFERRED)
        val expectedLinkText = if(notShowLinkStatuses.contains(testCase.status)) { "" } else { messages("case.ruling.viewRuling") }

        actualLinkText shouldBe expectedLinkText
      }
    }

  }

}
