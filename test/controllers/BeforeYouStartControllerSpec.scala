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

import controllers.actions.*
import models.cache.CacheMap
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.{mock, reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import service.DataCacheService
import views.html.beforeYouStart

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BeforeYouStartControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  val mockDataCacheService: DataCacheService = mock(classOf[DataCacheService])

  val beforeYouStartView: beforeYouStart = app.injector.instanceOf(classOf[beforeYouStart])

  private def controller(identifier: IdentifierAction) =
    new BeforeYouStartController(
      frontendAppConfig,
      identifier,
      new FakeDataRetrievalAction(None),
      mockDataCacheService,
      cc,
      beforeYouStartView
    )

  val fakeGETRequest: FakeRequest[AnyContentAsEmpty.type] = fakeGETRequestWithCSRF

  private val viewAsString = beforeYouStartView.ref.f(frontendAppConfig)(fakeGETRequest, messages).toString

  override protected def beforeEach(): Unit =
    reset(mockDataCacheService)

  "BeforeYouStart Controller" must {

    "return OK and the correct view for a GET - with EORI" in {
      val cacheMap = emptyCacheMap
      when(mockDataCacheService.save(any[CacheMap])).thenReturn(Future.successful(emptyCacheMap))
      val result = controller(identifier = FakeIdentifierAction(Some("eori"))).onPageLoad(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString
      verify(mockDataCacheService).save(refEq(cacheMap))
    }

    "return OK and the correct view for a GET - without EORI" in {
      val cacheMap = emptyCacheMap
      when(mockDataCacheService.save(any[CacheMap])).thenReturn(Future.successful(emptyCacheMap))
      val result = controller(identifier = FakeIdentifierAction(None)).onPageLoad(fakeRequest)

      status(result)          shouldBe OK
      contentAsString(result) shouldBe viewAsString
      verify(mockDataCacheService).save(refEq(cacheMap))
    }

    "return bad gateway when the data cache connector fails" in {
      when(mockDataCacheService.save(any[CacheMap])).thenReturn(Future.failed(new Exception))
      val result = controller(identifier = FakeIdentifierAction(None)).onPageLoad(fakeRequest)

      status(result) shouldBe BAD_GATEWAY
    }
  }
}
