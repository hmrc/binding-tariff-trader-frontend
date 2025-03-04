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
import models.*
import models.oCase.*
import navigation.FakeNavigator
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.{mock, reset, when}
import pages.ConfirmationPage
import play.api.mvc.Call
import play.api.test.Helpers.*
import service.{BTAUserService, CasesService, DataCacheService}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.account_dashboard_statuses

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IndexControllerSpec extends ControllerSpecBase {

  private val cache                          = mock(classOf[DataCacheService])
  private lazy val givenUserDoesntHaveAnEORI = FakeIdentifierAction(None)
  private val casesService                   = mock(classOf[CasesService])
  private val btaUserService                 = mock(classOf[BTAUserService])
  private def nextPageRoute                  = Call("GET", "/advance-tariff-application/information-you-need")

  private val pageIndex   = 1
  private val pageSize    = 10
  private val resultCount = 0

  val accountDashboardStatusesView: account_dashboard_statuses =
    app.injector.instanceOf(classOf[views.html.account_dashboard_statuses])

  override def beforeEach(): Unit = {
    reset(casesService)
    reset(btaUserService)
  }

  private def controller(identifier: IdentifierAction = FakeIdentifierAction): IndexController =
    new IndexController(
      frontendAppConfig,
      identifier,
      new FakeNavigator(nextPageRoute),
      casesService,
      getEmptyCacheMap,
      cache,
      cc,
      btaUserService,
      accountDashboardStatusesView
    )

  "Index Controller - Get Applications and Rulings" when {

    ".onPageLoad" should {

      "return the correct view for a load applications and rulings" in {

        val request = fakeRequestWithIdentifier()

        when(
          casesService
            .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
        ).thenReturn(Future.successful(Paged(Seq(btiCaseExample), pageIndex, pageSize, resultCount)))
        when(btaUserService.isBTAUser(request.identifier)).thenReturn(Future.successful(true))

        val result = controller().getApplicationsAndRulings(page = 1, sortBy = None, order = None)(request)

        status(result)        shouldBe OK
        contentAsString(result) should include("applications-rulings-list-table")
      }

      "redirect to an error page" when {

        "there is an issue fetching data from the btaUserService" in {
          val request = fakeRequestWithIdentifier()

          when(
            casesService
              .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
          ).thenReturn(Future.successful(Paged(Seq(btiCaseExample), pageIndex, pageSize, resultCount)))
          when(btaUserService.isBTAUser(request.identifier)).thenReturn(Future.failed(new RuntimeException("error")))

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

      for (caseStatus <- CaseStatus.values.toSeq)
        s"return the correct view with correct applications and rulings status in table for case status '$caseStatus'" in {

          val request  = fakeRequestWithIdentifier()
          val testCase = btiCaseWithDecision.copy(status = caseStatus)

          when(
            casesService
              .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
          ).thenReturn(Future.successful(Paged(Seq(testCase), pageIndex, pageSize, resultCount)))
          when(btaUserService.isBTAUser(request.identifier)).thenReturn(Future.successful(true))

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

      "return the correct view with View ruling link for COMPLETED cases" in {
        val request  = fakeRequestWithIdentifier()
        val testCase = btiCaseWithDecision.copy(status = CaseStatus.COMPLETED)

        when(
          casesService
            .getCases(any[String], any[Set[CaseStatus]], refEq(SearchPagination()), any[Sort])(any[HeaderCarrier])
        ).thenReturn(Future.successful(Paged(Seq(testCase), pageIndex, pageSize, resultCount)))
        when(btaUserService.isBTAUser(request.identifier)).thenReturn(Future.successful(true))

        val result = controller().getApplicationsAndRulings(page = 1, sortBy = None, order = None)(request)

        status(result) shouldBe OK

        val doc            = Jsoup.parse(contentAsString(result))
        val actualLinkText = doc.getElementById("applications-rulings-list-row-0-view-rulings-link").text().trim

        val expectedLinkTextView = messages("case.application.ruling.viewRuling")

        actualLinkText should startWith(expectedLinkTextView)
      }
    }

    ".onSubmit" should {

      "return the correct view for a load applications and rulings" in {

        val request = fakeRequestWithIdentifier()

        val confirmation: Confirmation = Confirmation("ref", "eori", "marisa@example.test")

        val ua =
          UserAnswers(emptyCacheMap)
            .set(ConfirmationPage, confirmation)

        when(btaUserService.isBTAUser(request.identifier)).thenReturn(Future.successful(true))
        when(cache.remove(ua.cacheMap)).thenReturn(Future(true))

        val result = controller().onSubmit()(request)

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.BeforeYouStartController.onPageLoad().url)
      }
    }
  }
}
