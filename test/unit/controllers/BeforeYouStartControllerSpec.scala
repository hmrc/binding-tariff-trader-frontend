/*
 * Copyright 2021 HM Revenue & Customs
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
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import play.api.test.Helpers._
import views.html.beforeYouStart

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class BeforeYouStartControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  val mockDataCacheConnector = mock[DataCacheConnector]

  private def controller(identifier: IdentifierAction) =
    new BeforeYouStartController(
      frontendAppConfig,
      identifier,
      new FakeDataRetrievalAction(None),
      mockDataCacheConnector,
      cc
    )

  val fakeGETRequest = fakeGETRequestWithCSRF

  private def viewAsString(eori: Option[String]) = beforeYouStart(frontendAppConfig)(fakeGETRequest, messages).toString

  override protected def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
  }

  "BeforeYouStart Controller" must {

    "return OK and the correct view for a GET - with EORI" in {
      val cacheMap = emptyCacheMap
      given(mockDataCacheConnector.save(any())).willReturn(Future.successful(emptyCacheMap))
      val result = controller(identifier = FakeIdentifierAction(Some("eori"))).onPageLoad(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString(Some("eori"))
      verify(mockDataCacheConnector).save(refEq(cacheMap))
    }

    "return OK and the correct view for a GET - without EORI" in {
      val cacheMap = emptyCacheMap
      given(mockDataCacheConnector.save(any())).willReturn(Future.successful(emptyCacheMap))
      val result = controller(identifier = FakeIdentifierAction(None)).onPageLoad(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString(None)
      verify(mockDataCacheConnector).save(refEq(cacheMap))
    }

    "return bad gateway when the data cache connector fails" in {
      given(mockDataCacheConnector.save(any())).willReturn(Future.failed(new Exception))
      val result = controller(identifier = FakeIdentifierAction(None)).onPageLoad(fakeRequest)

      status(result) shouldBe BAD_GATEWAY
    }
  }
}




