/*
 * Copyright 2022 HM Revenue & Customs
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
import models.{Confirmation, oCase}
import org.mockito.BDDMockito.given
import org.mockito.Mockito._
import pages.{ConfirmationPage, PdfViewPage}
import play.api.test.Helpers._
import service.{CountriesService, PdfService, URLCacheService}
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.JsonFormatters._
import viewmodels.{ConfirmationHomeUrlViewModel, PdfViewModel}
import views.html.confirmation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfirmationControllerSpec extends ControllerSpecBase {

  private val cache = mock[DataCacheConnector]
  private val cacheMap = mock[CacheMap]
  private val pdfService = mock[PdfService]
  private val pdfViewModel = oCase.pdf
  private val countriesService = new CountriesService
  private val urlCacheService = mock[URLCacheService]
  private val applicationConfig = mock[FrontendAppConfig]

  override def beforeEach(): Unit = {
    reset(cache, cacheMap, pdfService, urlCacheService, applicationConfig)
  }

  val confirmationView: confirmation = app.injector.instanceOf(classOf[confirmation])

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): ConfirmationController = {
    new ConfirmationController(
      applicationConfig,
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      cache,
      countriesService,
      pdfService,
      urlCacheService,
      cc,
      confirmationView
    )
  }

  private def viewAsString: String = {
    confirmationView(applicationConfig, Confirmation("ref", "eori", "marisa@example.test"), "token", pdfViewModel, _ => Some(""),
      urlViewModel = ConfirmationHomeUrlViewModel)(fakeRequest, messages).toString
  }

  "Confirmation Controller" must {

    "return OK and the correct view for a GET" in {
      val fakeRequest = fakeRequestWithNotOptionalEoriAndCache

      given(cache.remove(cacheMap)).willReturn(Future.successful(true))
      given(cacheMap.getEntry[Confirmation](ConfirmationPage.toString)).willReturn(Some(Confirmation("ref", "eori", "marisa@example.test")))
      given(cacheMap.getEntry[PdfViewModel](PdfViewPage.toString)).willReturn(Some(pdfViewModel))
      given(pdfService.encodeToken("eori")).willReturn("token")
      given(urlCacheService.fetchBTACallbackURLWithDelete(fakeRequest.internalId)).willReturn(None)
      given(applicationConfig.businessTaxAccountUrl).willReturn("testBtaHostUrl")

      val result = controller(new FakeDataRetrievalAction(Some(cacheMap))).onPageLoad(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString
      verify(cache).remove(cacheMap)
    }

    "redirect given missing data" in {
      given(cacheMap.getEntry[Confirmation](ConfirmationPage.toString)).willReturn(None)

      val result = controller().onPageLoad(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

  }
}
