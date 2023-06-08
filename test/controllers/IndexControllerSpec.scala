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
import models.CaseStatus.CaseStatus
import models._
import models.oCase._
import navigation.FakeNavigator
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import play.api.mvc.Call
import play.api.test.Helpers._
import service.{BTAUserService, CasesService}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.{account_dashboard_statuses, index}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IndexControllerSpec extends ControllerSpecBase {

  private lazy val givenUserDoesntHaveAnEORI = FakeIdentifierAction(None)
  private val casesService                   = mock[CasesService]
  private val btaUserService                 = mock[BTAUserService]
  private def nextPageRoute                  = Call("GET", "/advance-tariff-application/information-you-need")

  private val pageIndex   = 1
  private val pageSize    = 10
  private val resultCount = 0

  val accountDashboardStatusesView: account_dashboard_statuses =
    app.injector.instanceOf(classOf[views.html.account_dashboard_statuses])
  val indexView: index = app.injector.instanceOf(classOf[views.html.index])

  override def beforeEach(): Unit =
    reset(casesService, btaUserService)

  private def controller(identifier: IdentifierAction = FakeIdentifierAction): IndexController =
    new IndexController(
      frontendAppConfig,
      identifier,
      new FakeNavigator(nextPageRoute),
      casesService,
      cc,
      btaUserService,
      accountDashboardStatusesView,
      indexView
    )

  "Index Controller - Get Applications" must {

    "return the correct view for a load applications" in {

      given(
        casesService
          .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
      ).willReturn(Future.successful(Paged(Seq(btiCaseExample), pageIndex, pageSize, resultCount)))

      val result = controller().getApplications(page = 1)(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) should include("applications-list-table")
    }

    "return 200 and show no results for a GET when no applications are found" in {
      given(
        casesService
          .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
      ).willReturn(Future.successful(Paged(Seq[Case](), pageIndex, pageSize, resultCount)))

      val result = controller().getApplications(page = 1)(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) should include("application-list-empty")

    }

    "redirect to BeforeYouStart when EORI unavailable" in {
      val result = controller(givenUserDoesntHaveAnEORI).getApplications(page = 1)(fakeRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }

    "return the correct view for a load rulings without decision" in {

      given(
        casesService
          .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
      ).willReturn(
        Future.successful(Paged(Seq(btiCaseWithDecision.copy(decision = None)), pageIndex, pageSize, resultCount))
      )

      val result = controller().getApplications(page = 1)(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) should include("applications-list-table")
    }

    for (caseStatus <- CaseStatus.values.toSeq) {
      s"return the correct view with correct ruling status in table for case status '$caseStatus'" in {
        val testCase = btiCaseWithDecision.copy(status = caseStatus)
        given(
          casesService
            .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination(1)), any[Sort])(any[HeaderCarrier])
        ).willReturn(Future.successful(Paged(Seq(testCase), pageIndex, pageSize, resultCount)))

        val result = controller().getApplications(page = 1)(fakeRequest)

        status(result) shouldBe OK

        val doc          = Jsoup.parse(contentAsString(result))
        val actualStatus = doc.getElementById("application-list-row-0-status").text().trim

        val expectedStatus = testCase.status match {
          case CaseStatus.NEW | CaseStatus.OPEN =>
            messages("case.application.status.inProgress")
          case CaseStatus.SUPPRESSED | CaseStatus.REJECTED =>
            messages("case.application.status.rejected")
          case CaseStatus.REFERRED =>
            messages("case.application.status.infoRequested")
          case CaseStatus.COMPLETED | CaseStatus.CANCELLED =>
            messages("case.application.status.completed")
          case CaseStatus.DRAFT =>
            messages("case.application.status.draft")
          case CaseStatus.SUSPENDED =>
            messages("case.application.status.suspended")
          case _ => ""
        }

        actualStatus shouldBe expectedStatus
      }

      s"return the correct view with case bti ruling link in table for case status '$caseStatus'" in {
        val testCase = btiCaseWithDecision.copy(status = caseStatus)
        given(
          casesService
            .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
        ).willReturn(Future.successful(Paged(Seq(testCase), pageIndex, pageSize, resultCount)))

        val result = controller().getApplications(page = 1)(fakeRequest)

        status(result) shouldBe OK

        val doc              = Jsoup.parse(contentAsString(result))
        val actualLinkText   = doc.getElementById("application-list-row-0-download").text().trim
        val expectedLinkText = messages("case.application.viewApplication")

        actualLinkText should startWith(expectedLinkText)
      }
    }

  }

  "Index Controller - Get Rulings" should {

    "return the correct view for a load rulings with decision" in {

      given(
        casesService
          .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
      ).willReturn(Future.successful(Paged(Seq(btiCaseWithDecision), pageIndex, pageSize, resultCount)))

      val result = controller().getRulings(page = 1)(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) should include("rulings-list-table")
    }

    "return 200 and show no results  for a GET when no rulings are found" in {

      given(
        casesService
          .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
      ).willReturn(Future.successful(Paged(Seq[Case](), pageIndex, pageSize, resultCount)))

      val result = controller().getRulings(page = 1)(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) should include("ruling-list-empty")
    }

    "redirect to BeforeYouStart when EORI unavailable" in {
      val result = controller(givenUserDoesntHaveAnEORI).getRulings(page = 1)(fakeRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }

    "return the correct view for a load rulings without decision" in {

      given(
        casesService
          .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
      ).willReturn(
        Future.successful(Paged(Seq(btiCaseWithDecision.copy(decision = None)), pageIndex, pageSize, resultCount))
      )

      val result = controller().getRulings(page = 1)(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) should include("rulings-list-table")
    }

    for (caseStatus <- CaseStatus.values.toSeq) {
      s"return the correct view with correct ruling status in table for case status '$caseStatus'" in {
        val testCase = btiCaseWithDecision.copy(status = caseStatus)
        given(
          casesService
            .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
        ).willReturn(Future.successful(Paged(Seq(testCase), pageIndex, pageSize, resultCount)))

        val result = controller().getRulings(page = 1)(fakeRequest)

        status(result) shouldBe OK

        val doc          = Jsoup.parse(contentAsString(result))
        val actualStatus = doc.getElementById("rulings-list-row-0-status").text().trim

        val expectedStatus = testCase.status match {
          case CaseStatus.CANCELLED                                   => messages("case.ruling.status.cancelled")
          case CaseStatus.COMPLETED if testCase.hasActiveDecision     => messages("case.ruling.status.active")
          case CaseStatus.COMPLETED if testCase.hasExpiredDecision    => messages("case.ruling.status.expired")
          case CaseStatus.REJECTED                                    => messages("case.ruling.status.rejected")
          case CaseStatus.SUSPENDED                                   => messages("case.ruling.status.suspended")
          case CaseStatus.NEW | CaseStatus.OPEN | CaseStatus.REFERRED => messages("case.ruling.status.in.progress")
          case _                                                      => testCase.status.toString.toLowerCase.capitalize
        }

        actualStatus shouldBe expectedStatus
      }

      s"return the correct view with case bti ruling link in table for case status '$caseStatus'" in {
        val testCase = btiCaseWithDecision.copy(status = caseStatus)
        given(
          casesService
            .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
        ).willReturn(Future.successful(Paged(Seq(testCase), pageIndex, pageSize, resultCount)))

        val result = controller().getRulings(page = 1)(fakeRequest)

        status(result) shouldBe OK

        val doc            = Jsoup.parse(contentAsString(result))
        val actualLinkText = doc.getElementById("rulings-list-row-0-view").text().trim
        val notShowLinkStatuses =
          Set(CaseStatus.REJECTED, CaseStatus.SUSPENDED, CaseStatus.NEW, CaseStatus.OPEN, CaseStatus.REFERRED)
        val expectedLinkText = if (notShowLinkStatuses.contains(testCase.status)) { "" }
        else { messages("case.ruling.viewRuling") }

        actualLinkText should startWith(expectedLinkText)
      }
    }

  }

  "Index Controller - Get Applications and Rulings" should {

    "return the correct view for a load applications and rulings" in {
      val request = fakeRequestWithIdentifier()

      given(
        casesService
          .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
      ).willReturn(Future.successful(Paged(Seq(btiCaseExample), pageIndex, pageSize, resultCount)))
      given(btaUserService.isBTAUser(request.identifier)).willReturn(Future.successful(true))

      val result = controller().getApplicationsAndRulings(page = 1, sortBy = None, order = None)(request)

      status(result)          shouldBe OK
      contentAsString(result) should include("applications-rulings-list-table")
    }

    "redirect to an error page" when {
      "there is an issue fetching data from the btaUserService" in {
        val request = fakeRequestWithIdentifier()

        given(
          casesService
            .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
        ).willReturn(Future.successful(Paged(Seq(btiCaseExample), pageIndex, pageSize, resultCount)))
        given(btaUserService.isBTAUser(request.identifier)).willReturn(Future.failed(new RuntimeException("error")))

        val result = controller().getApplicationsAndRulings(page = 1, sortBy = None, order = None)(request)

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ErrorController.onPageLoad.url)
      }
    }

    "redirect to BeforeYouStart when EORI unavailable" in {
      val result = controller(givenUserDoesntHaveAnEORI)
        .getApplicationsAndRulings(page = 1, sortBy = None, order = None)(fakeRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }

    for (caseStatus <- CaseStatus.values.toSeq) {
      s"return the correct view with correct applications and rulings status in table for case status '$caseStatus'" in {
        val request  = fakeRequestWithIdentifier()
        val testCase = btiCaseWithDecision.copy(status = caseStatus)

        given(
          casesService
            .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
        ).willReturn(Future.successful(Paged(Seq(testCase), pageIndex, pageSize, resultCount)))
        given(btaUserService.isBTAUser(request.identifier)).willReturn(Future.successful(true))

        val result = controller().getApplicationsAndRulings(page = 1, sortBy = None, order = None)(fakeRequest)

        status(result) shouldBe OK

        val doc          = Jsoup.parse(contentAsString(result))
        val actualStatus = doc.getElementById("applications-rulings-list-row-0-status").text().trim

        val expectedStatus = testCase.status match {
          case CaseStatus.NEW =>
            messages("case.application.status.submitted")
          case CaseStatus.OPEN | CaseStatus.SUSPENDED =>
            messages("case.application.status.inProgress")
          case CaseStatus.REFERRED =>
            messages("case.application.status.infoRequested")
          case CaseStatus.SUPPRESSED | CaseStatus.REJECTED =>
            messages("case.application.status.rejected")
          case CaseStatus.COMPLETED if testCase.hasActiveDecision =>
            if (testCase.daysUntilExpiry.get > 120) {
              messages("case.application.status.approvedRuling")
            } else {
              messages("case.application.status.approvedRulingExpiring", testCase.daysUntilExpiry.get)
            }
          case CaseStatus.COMPLETED if testCase.hasExpiredDecision =>
            messages("case.application.ruling.status.expired")
          case CaseStatus.CANCELLED =>
            messages("case.application.status.cancelled")
          case _ => ""
        }
        actualStatus shouldBe expectedStatus
      }
    }

    "return the correct view with View ruling link for COMPLETED cases" in {
      val request  = fakeRequestWithIdentifier()
      val testCase = btiCaseWithDecision.copy(status = CaseStatus.COMPLETED)

      given(
        casesService
          .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
      ).willReturn(Future.successful(Paged(Seq(testCase), pageIndex, pageSize, resultCount)))
      given(btaUserService.isBTAUser(request.identifier)).willReturn(Future.successful(true))

      val result = controller().getApplicationsAndRulings(page = 1, sortBy = None, order = None)(request)

      status(result) shouldBe OK

      val doc            = Jsoup.parse(contentAsString(result))
      val actualLinkText = doc.getElementById("applications-rulings-list-row-0-view-rulings-link").text().trim

      val expectedLinkTextView = messages("case.application.ruling.viewRuling")

      actualLinkText should startWith(expectedLinkTextView)
    }
  }
}
