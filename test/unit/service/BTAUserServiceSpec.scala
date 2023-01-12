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

package unit.service

import base.SpecBase
import connectors.DataCacheConnector
import org.mockito.Mockito.{reset, times, verify, when}
import play.api.libs.json.Json
import service.BTAUserService
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.{ExecutionContext, Future}

class BTAUserServiceSpec extends SpecBase {
  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  private val dataCacheConnector    = mock[DataCacheConnector]

  val keyPrefix          = "btaUser-"
  val requestId          = "testRequestId"
  val cacheMapId         = s"$keyPrefix$requestId"
  val cacheMap: CacheMap = new CacheMap(cacheMapId, Map("isBTAUser" -> Json.toJson(true)))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(dataCacheConnector)
  }

  "save" should {
    "store the boolean value true in a cacheMap " when {
      "a CacheMap is not already present " in {
        val btaUserService = new BTAUserService(dataCacheConnector)
        when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(None))
        when(dataCacheConnector.save(cacheMap)).thenReturn(Future.successful(cacheMap))
        val cacheMapResult: CacheMap = await(btaUserService.save(requestId))

        cacheMapResult shouldBe cacheMap
      }
    }

    "return a saved CacheMap" in {
      val btaUserService = new BTAUserService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(Some(cacheMap)))
      val cacheMapResult: CacheMap = await(btaUserService.save(requestId))

      cacheMapResult shouldBe cacheMap
      verify(dataCacheConnector, times(0)).save(cacheMap)
    }
  }

  "isBTAUser" should {
    "return true if a boolean flag has been cached" in {
      val btaUserService = new BTAUserService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(Some(cacheMap)))
      val result: Boolean = await(btaUserService.isBTAUser(requestId))

      result shouldBe true
    }

    "return false if a boolean flag has not been cached" in {
      val btaUserService = new BTAUserService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(None))
      val result: Boolean = await(btaUserService.isBTAUser(requestId))

      result shouldBe false
    }
  }

  "remove" should {
    "remove a cacheMap containing a boolean true value" in {
      val btaUserService = new BTAUserService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(Some(cacheMap)))
      when(dataCacheConnector.remove(cacheMap)).thenReturn(Future.successful(true))

      val result = await(btaUserService.remove(requestId))

      result shouldBe true
      verify(dataCacheConnector, times(1)).remove(cacheMap)
    }

    "not remove a cacheMap containing a boolean value if one does not exist" in {
      val btaUserService = new BTAUserService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(None))
      val result = await(btaUserService.remove(requestId))

      result shouldBe false
      verify(dataCacheConnector, times(0)).remove(cacheMap)
    }
  }
}
