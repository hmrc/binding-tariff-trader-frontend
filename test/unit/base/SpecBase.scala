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

import config.FrontendAppConfig
import models.UserAnswers
import models.requests.{DataRequest, OptionalDataRequest}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.Files.TemporaryFileCreator
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.CSRFTokenHelper.CSRFFRequestHeader
import play.api.test.{FakeHeaders, FakeRequest}
import uk.gov.hmrc.http.cache.client.CacheMap
import unit.base.WireMockObject
import unit.utils.UnitSpec

trait SpecBase extends UnitSpec with GuiceOneAppPerSuite with MockitoSugar {

  override implicit lazy val app: Application = GuiceApplicationBuilder()
    .configure(
      "metrics.jvm" -> false,
      "metrics.enabled" -> false
    ).build()

  protected lazy val injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  implicit val cc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]
  implicit val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def fakeRequestWithEori = OptionalDataRequest(fakeRequest, "id", Some("eori-789012"), None)

  def fakeRequestWithNotOptionalEoriAndCache = DataRequest(fakeRequest, "id", Some("eori-789012"), UserAnswers(CacheMap("id", Map.empty)))

  def fakeRequest = FakeRequest()

  def fakeRequestWithEoriAndCache = OptionalDataRequest(fakeRequest, "id", Some("eori-789012"), Some(UserAnswers(CacheMap("id", Map.empty))))

  def fakeGETRequestWithCSRF: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    "GET", "/", FakeHeaders(Seq("csrfToken"->"csrfToken")), AnyContentAsEmpty)
    .withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  def fakePOSTRequestWithCSRF: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    "POST", "/", FakeHeaders(Seq("csrfToken"->"csrfToken")), AnyContentAsEmpty)
    .withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  def messages: Messages = messagesApi.preferred(fakeRequest)

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def tempFileCreator: TemporaryFileCreator = injector.instanceOf[TemporaryFileCreator]

  WireMockObject.start()

}
