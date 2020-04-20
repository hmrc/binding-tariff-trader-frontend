/*
 * Copyright 2020 HM Revenue & Customs
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

package base

import com.codahale.metrics.SharedMetricRegistries
import config.FrontendAppConfig
import models.UserAnswers
import models.requests.{DataRequest, OptionalDataRequest}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.Injector
import play.api.libs.Files.TemporaryFileCreator
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.UnitSpec

trait SpecBase extends UnitSpec with GuiceOneAppPerSuite with MockitoSugar {

  SharedMetricRegistries.clear()

  protected lazy val injector: Injector = fakeApplication.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  implicit val cc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]
  implicit val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  implicit val lang: Lang = appConfig.defaultLang

  def fakeRequestWithEori = OptionalDataRequest(fakeRequest, "id", Some("eori-789012"), None)

  def fakeRequestWithNotOptionalEoriAndCache = DataRequest(fakeRequest, "id", Some("eori-789012"), UserAnswers(CacheMap("id", Map.empty)))

  def fakeRequest = FakeRequest()

  def fakeRequestWithEoriAndCache = OptionalDataRequest(fakeRequest, "id", Some("eori-789012"), Some(UserAnswers(CacheMap("id", Map.empty))))

  def messages: Messages = messagesApi.preferred(fakeRequest)

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def tempFileCreator: TemporaryFileCreator = injector.instanceOf[TemporaryFileCreator]

}
