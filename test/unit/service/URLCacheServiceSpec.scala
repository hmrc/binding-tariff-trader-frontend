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

package unit.service

import base.SpecBase
import connectors.DataCacheConnector
import org.mockito.Mockito.{reset, times, verify, when}
import play.api.libs.json.Json
import service.URLCacheService
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.{ExecutionContext, Future}

class URLCacheServiceSpec extends SpecBase {
  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  private val dataCacheConnector = mock[DataCacheConnector]

  val requestId = "testRequestId"
  val btaURL = "testURL"
  val cacheMapId = s"url-$requestId"
  val cacheMap: CacheMap = new CacheMap(s"url-$requestId", Map("btaCallBackURLKey" -> Json.toJson(btaURL)))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(dataCacheConnector)
  }

  "saveBTACallbackURL" should {
    "save a btaCallBackUrl for a given key" when {
      "a CacheMap is not already present " in {
        val urlCacheService = new URLCacheService(dataCacheConnector)
        when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(None))
        when(dataCacheConnector.save(cacheMap)).thenReturn(Future.successful(cacheMap))
        val cacheMapResult: CacheMap = await(urlCacheService.saveBTACallbackURL(requestId, btaURL))

        cacheMapResult shouldBe cacheMap
      }
    }

    "return a saved CacheMap" in {
      val urlCacheService = new URLCacheService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(Some(cacheMap)))
      val cacheMapResult: CacheMap = await(urlCacheService.saveBTACallbackURL(requestId, btaURL))

      cacheMapResult shouldBe cacheMap
      verify(dataCacheConnector, times(0)).save(cacheMap)
    }
  }

  "fetchBTACallbackURL" should {
    "return a url if one has been cached" in {
      val urlCacheService = new URLCacheService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(Some(cacheMap)))
      val urlResult: Option[String] = await(urlCacheService.fetchBTACallbackURL(requestId))

      urlResult shouldBe Some(btaURL)
    }

    "return None if a url has not been cached" in {
      val urlCacheService = new URLCacheService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(None))
      val urlResult: Option[String] = await(urlCacheService.fetchBTACallbackURL(requestId))

      urlResult shouldBe None
    }
  }

  "fetchBTACallbackURLWithDelete" should {
    "return a url if present and delete the associated CacheMap" in {
      val urlCacheService = new URLCacheService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(Some(cacheMap)))
      when(dataCacheConnector.remove(cacheMap)).thenReturn(Future.successful(true))

      val urlResult: Option[String] = await(urlCacheService.fetchBTACallbackURLWithDelete(requestId))

      urlResult shouldBe Some(btaURL)
      verify(dataCacheConnector).remove(cacheMap)
    }

    "return None if no url is present" in {
      val urlCacheService = new URLCacheService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(None))

      val urlResult: Option[String] = await(urlCacheService.fetchBTACallbackURLWithDelete(requestId))

      urlResult shouldBe None
      verify(dataCacheConnector, times(0)).remove(cacheMap)
    }
  }

  "removeBTACallbackURL" should {
    "remove a cacheMap containing a url" in {
      val urlCacheService = new URLCacheService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(Some(cacheMap)))
      when(dataCacheConnector.remove(cacheMap)).thenReturn(Future.successful(true))

      val result = await(urlCacheService.removeBTACallbackURL(requestId))

      result shouldBe true
      verify(dataCacheConnector).remove(cacheMap)
    }

    "not remove a cacheMap containing a url if one does not exist" in {
      val urlCacheService = new URLCacheService(dataCacheConnector)
      when(dataCacheConnector.fetch(cacheMapId)).thenReturn(Future.successful(None))
      val result = await(urlCacheService.removeBTACallbackURL(requestId))

      result shouldBe false
      verify(dataCacheConnector,times(0)).remove(cacheMap)
    }
  }




}
