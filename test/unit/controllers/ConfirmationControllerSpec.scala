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
import controllers.actions._
import models.Confirmation
import org.mockito.BDDMockito.given
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import pages.ConfirmationPage
import play.api.test.Helpers._
import service.PdfService
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.confirmation

import scala.concurrent.Future

class ConfirmationControllerSpec extends ControllerSpecBase with MockitoSugar {

  private val cache = mock[DataCacheConnector]
  private val cacheMap = mock[CacheMap]
  private val pdfService = mock[PdfService]

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): ConfirmationController = {
    new ConfirmationController(
      frontendAppConfig,
      messagesApi,
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      cache,
      pdfService
    )
  }

  private def viewAsString: String = {
    confirmation(frontendAppConfig, Confirmation("ref", "eori", "marisa@example.test", sendingSamples = true), "token")(fakeRequest, messages).toString
  }

  "Confirmation Controller" must {

    "return OK and the correct view for a GET" in {
      given(cache.remove(cacheMap)).willReturn(Future.successful(true))
      given(cacheMap.getEntry[Confirmation](ConfirmationPage.toString)).willReturn(Some(Confirmation("ref", "eori", "marisa@example.test", sendingSamples = true)))
      given(pdfService.encodeToken("eori", "ref")).willReturn("token")

      val result = controller(new FakeDataRetrievalAction(Some(cacheMap))).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString
      verify(cache).remove(cacheMap)
    }

    "redirect given missing data" in {
      given(cacheMap.getEntry[Confirmation](ConfirmationPage.toString)).willReturn(None)

      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/binding-tariff-application/this-service-has-been-reset")
    }

  }

}
