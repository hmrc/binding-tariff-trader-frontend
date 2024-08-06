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

package controllers.actions

import base.SpecBase
import models.cache.CacheMap
import models.requests.{IdentifierRequest, OptionalDataRequest}
import org.mockito.Mockito.{mock, when}
import org.scalatest.concurrent.ScalaFutures
import service.DataCacheService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with ScalaFutures {

  class Harness(dataCacheService: DataCacheService) extends DataRetrievalActionImpl(dataCacheService) {
    def callTransform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" when {

    "there is no data in the cache" must {

      "set userAnswers to 'None' in the request" in {
        val dataCacheService = mock(classOf[DataCacheService])
        when(dataCacheService.fetch("id")) thenReturn Future(None)
        val action = new Harness(dataCacheService)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "id", Some("eori-789012")))

        whenReady(futureResult) {
          _.userAnswers.isEmpty shouldBe true
        }
      }

    }

    "there is data in the cache" must {

      "build a userAnswers object and add it to the request" in {
        val dataCacheService = mock(classOf[DataCacheService])
        when(dataCacheService.fetch("id")) thenReturn Future(Some(new CacheMap("id", Map())))
        val action = new Harness(dataCacheService)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "id", Some("eori-789012")))

        whenReady(futureResult) {
          _.userAnswers.isDefined shouldBe true
        }
      }

    }

  }

}
