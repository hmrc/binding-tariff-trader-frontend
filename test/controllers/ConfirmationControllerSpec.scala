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

import config.FrontendAppConfig
import controllers.actions._
import models.cache.CacheMap
import models.requests.DataRequest
import models.{Confirmation, UserAnswers, oCase}
import org.mockito.BDDMockito.given
import org.mockito.Mockito.{mock, reset}
import pages.{ConfirmationPage, PdfViewPage}
import play.api.libs.json.JsValue
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service._
import utils.JsonFormatters._
import viewmodels.ConfirmationHomeUrlViewModel
import views.html.confirmation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfirmationControllerSpec extends ControllerSpecBase {

  private val mockDataCacheService          = mock(classOf[DataCacheService])
  private val mockPdfService                = mock(classOf[PdfService])
  private val pdfViewModel                  = oCase.pdf
  private val countriesService              = new CountriesService
  private val mockBtaUserService            = mock(classOf[BTAUserService])
  private val mockUserAnswerDeletionService = mock(classOf[UserAnswerDeletionService])
  private val mockApplicationConfig         = mock(classOf[FrontendAppConfig])

  override def beforeEach(): Unit = {
    reset(mockDataCacheService)
    reset(mockPdfService)
    reset(mockBtaUserService)
    reset(mockApplicationConfig)
  }

  val confirmationView: confirmation = app.injector.instanceOf(classOf[confirmation])

  override def emptyCacheMap: CacheMap = CacheMap(cacheMapId, Map[String, JsValue]())

  private def controller(userAnswers: Option[UserAnswers]): ConfirmationController =
    new ConfirmationController(
      appConfig = mockApplicationConfig,
      identify = FakeIdentifierAction,
      getData = new FakeDataRetrievalAction(userAnswers.map(_.cacheMap)),
      requireData = new DataRequiredActionImpl,
      dataCacheService = mockDataCacheService,
      countriesService = countriesService,
      pdfService = mockPdfService,
      btaUserService = mockBtaUserService,
      userAnswerDeletionService = mockUserAnswerDeletionService,
      cc = cc,
      confirmationView = confirmationView
    )

  private def viewAsString: String =
    confirmationView(
      appConfig = mockApplicationConfig,
      confirmation = Confirmation("ref", "eori", "marisa@example.test"),
      pdfToken = "token",
      pdf = pdfViewModel,
      getCountryName = _ => Some(""),
      urlViewModel = ConfirmationHomeUrlViewModel
    )(fakeRequest, messages).toString

  val confirmation: Confirmation = Confirmation("ref", "eori", "marisa@example.test")

  "ConfirmationController" when {

    ".onPageLoad()" when {

      "for HappyPath and correct data present" should {

        "return OK and the correct view for a GET" in {

          val ua =
            UserAnswers(emptyCacheMap)
              .set(ConfirmationPage, confirmation)
              .set(PdfViewPage, pdfViewModel)

          val fakeDataRequest =
            DataRequest(
              request = FakeRequest(),
              internalId = "id",
              eoriNumber = Some("eori-789012"),
              userAnswers = ua
            )

          given(mockBtaUserService.isBTAUser(fakeDataRequest.internalId)).willReturn(false)

          given(
            mockUserAnswerDeletionService.deleteAllUserAnswersExcept(ua, Seq(ConfirmationPage, PdfViewPage))
          ).willReturn(ua)

          given(mockDataCacheService.save(ua.cacheMap)).willReturn(ua.cacheMap)
          given(mockPdfService.encodeToken("eori")).willReturn("token")

          val result = controller(Some(ua)).onPageLoad(fakeDataRequest)

          status(result)          shouldBe OK
          contentAsString(result) shouldBe viewAsString
        }
      }

      "given no user answers redirect to SessionExpired page" in {

        val result = controller(None).onPageLoad(FakeRequest())

        status(result)           shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
      }

      "is a BTA User and given a failure" should {

        "redirect to the Error page" in {

          val ua =
            UserAnswers(emptyCacheMap)
              .set(ConfirmationPage, confirmation)
              .set(PdfViewPage, pdfViewModel)

          val fakeDataRequest =
            DataRequest(
              request = FakeRequest(),
              internalId = "id",
              eoriNumber = Some("eori-789012"),
              userAnswers = ua
            )

          given(mockBtaUserService.isBTAUser(fakeDataRequest.internalId))
            .willReturn(Future.failed(new RuntimeException(" Fetch Error")))

          val result = controller(Some(ua)).onPageLoad(fakeDataRequest)

          status(result)           shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ErrorController.onPageLoad.url)
        }
      }

      "the ConfirmationPage and PdfViewPage data is missing" should {

        "redirect to the Error page" in {

          val ua =
            UserAnswers(emptyCacheMap)

          val fakeDataRequest =
            DataRequest(
              request = FakeRequest(),
              internalId = "id",
              eoriNumber = Some("eori-789012"),
              userAnswers = ua
            )

          val result = controller(Some(ua)).onPageLoad(fakeDataRequest)

          status(result)           shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ErrorController.onPageLoad.url)
        }
      }
    }

    ".onSubmit()" when {

      "isBTAUser" should {

        "return SEE_OTHER and redirect" in {

          val ua =
            UserAnswers(emptyCacheMap)
              .set(ConfirmationPage, confirmation)
              .set(PdfViewPage, pdfViewModel)

          val fakeDataRequest =
            DataRequest(
              request = FakeRequest(),
              internalId = "id",
              eoriNumber = Some("eori-789012"),
              userAnswers = ua
            )

          given(mockBtaUserService.isBTAUser(fakeDataRequest.internalId)).willReturn(true)

          given(
            mockUserAnswerDeletionService.deleteAllUserAnswersExcept(ua, Seq(ConfirmationPage, PdfViewPage))
          ).willReturn(ua)

          given(mockDataCacheService.remove(ua.cacheMap)).willReturn(Future(true))
          given(mockPdfService.encodeToken("eori")).willReturn("token")

          val result = controller(Some(ua)).onSubmit()(fakeDataRequest)

          status(result)           shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.routes.BTARedirectController.redirectToBTA.url)
        }
      }

      "is NOT a BTAUser" should {

        "return SEE_OTHER and redirect" in {

          val ua =
            UserAnswers(emptyCacheMap)
              .set(ConfirmationPage, confirmation)
              .set(PdfViewPage, pdfViewModel)

          val fakeDataRequest =
            DataRequest(
              request = FakeRequest(),
              internalId = "id",
              eoriNumber = Some("eori-789012"),
              userAnswers = ua
            )

          given(mockBtaUserService.isBTAUser(fakeDataRequest.internalId)).willReturn(false)

          given(
            mockUserAnswerDeletionService.deleteAllUserAnswersExcept(ua, Seq(ConfirmationPage, PdfViewPage))
          ).willReturn(ua)

          given(mockDataCacheService.remove(ua.cacheMap)).willReturn(Future(true))
          given(mockPdfService.encodeToken("eori")).willReturn("token")

          val result = controller(Some(ua)).onSubmit()(fakeDataRequest)

          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(
            controllers.routes.IndexController.getApplicationsAndRulings(sortBy = None, order = None).url
          )
        }
      }
    }
  }
}
