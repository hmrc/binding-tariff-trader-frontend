/*
 * Copyright 2024 HM Revenue & Customs
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
import connectors.DataCacheConnector
import controllers.actions._
import models.cache.CacheMap
import models.{Confirmation, oCase}
import org.mockito.BDDMockito.given
import org.mockito.Mockito.{mock, reset, verify}
import pages.{ConfirmationPage, PdfViewPage}
import play.api.test.Helpers._
import service.{BTAUserService, CountriesService, PdfService}
import utils.JsonFormatters._
import viewmodels.{ConfirmationHomeUrlViewModel, PdfViewModel}
import views.html.confirmation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfirmationControllerSpec extends ControllerSpecBase {

  private val cache             = mock(classOf[DataCacheConnector])
  private val cacheMap          = mock(classOf[CacheMap])
  private val pdfService        = mock(classOf[PdfService])
  private val pdfViewModel      = oCase.pdf
  private val countriesService  = new CountriesService
  private val btaUserService    = mock(classOf[BTAUserService])
  private val applicationConfig = mock(classOf[FrontendAppConfig])

  override def beforeEach(): Unit = {
    reset(cache)
    reset(cacheMap)
    reset(pdfService)
    reset(btaUserService)
    reset(applicationConfig)
  }

  val confirmationView: confirmation = app.injector.instanceOf(classOf[confirmation])

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): ConfirmationController =
    new ConfirmationController(
      applicationConfig,
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      cache,
      countriesService,
      pdfService,
      btaUserService,
      cc,
      confirmationView
    )

  private def viewAsString: String =
    confirmationView(
      applicationConfig,
      Confirmation("ref", "eori", "marisa@example.test"),
      "token",
      pdfViewModel,
      _ => Some(""),
      urlViewModel = ConfirmationHomeUrlViewModel
    )(fakeRequest, messages).toString

  "Confirmation Controller" must {

    "return OK and the correct view for a GET" in {
      val fakeRequest = fakeRequestWithNotOptionalEoriAndCache

      given(cache.remove(cacheMap)).willReturn(Future.successful(true))
      given(cacheMap.getEntry[Confirmation](ConfirmationPage.toString))
        .willReturn(Some(Confirmation("ref", "eori", "marisa@example.test")))
      given(cacheMap.getEntry[PdfViewModel](PdfViewPage.toString)).willReturn(Some(pdfViewModel))
      given(pdfService.encodeToken("eori")).willReturn("token")
      given(btaUserService.isBTAUser(fakeRequest.internalId)).willReturn(false)
      given(applicationConfig.businessTaxAccountUrl).willReturn("testBtaHostUrl")

      val result = controller(new FakeDataRetrievalAction(Some(cacheMap))).onPageLoad(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString
      verify(cache).remove(cacheMap)
    }

    "redirect given missing data" in {
      given(cacheMap.getEntry[Confirmation](ConfirmationPage.toString)).willReturn(None)

      val result = controller().onPageLoad(fakeRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    val errorScenarios = List(
      ("isBTAUser", () => Future.failed(new RuntimeException(" Fetch Error"))),
      ("remove", () => Future.successful(true))
    )

    "redirect to an error page" when {
      given(cache.remove(cacheMap)).willReturn(Future.successful(true))
      errorScenarios foreach { data =>
        s"there is an issue calling ${data._1}" in {
          val fakeRequest = fakeRequestWithNotOptionalEoriAndCache

          given(cacheMap.getEntry[Confirmation](ConfirmationPage.toString))
            .willReturn(Some(Confirmation("ref", "eori", "marisa@example.test")))
          given(cacheMap.getEntry[PdfViewModel](PdfViewPage.toString)).willReturn(Some(pdfViewModel))
          given(btaUserService.isBTAUser(fakeRequest.internalId)).willReturn(data._2())

          val result = controller(new FakeDataRetrievalAction(Some(cacheMap))).onPageLoad(fakeRequest)

          status(result)           shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ErrorController.onPageLoad.url)
        }
      }
    }
  }
}
