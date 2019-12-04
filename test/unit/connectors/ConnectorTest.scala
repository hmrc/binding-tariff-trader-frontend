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

package connectors

import akka.actor.ActorSystem
import base.SpecBase
import config.FrontendAppConfig
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient

trait ConnectorTest extends SpecBase with WiremockTestServer
 with MockitoSugar with ResourceFiles {

  private val actorSystem = ActorSystem.create("testActorSystem")

  protected implicit val realConfig: FrontendAppConfig = fakeApplication.injector.instanceOf[FrontendAppConfig]
  protected val appConfig: FrontendAppConfig = mock[FrontendAppConfig]

  protected val fakeAuthToken = "AUTH_TOKEN"

  protected implicit val hc: HeaderCarrier = HeaderCarrier()

  private val auditConnector = mock[HttpAuditing]

  protected val wsClient: WSClient = fakeApplication.injector.instanceOf[WSClient]
  protected val standardHttpClient = new DefaultHttpClient(fakeApplication.configuration, auditConnector, wsClient, actorSystem)

  override def beforeAll(): Unit = {
    super.beforeAll()

    when(appConfig.bindingTariffFileStoreUrl) thenReturn wireMockUrl
    when(appConfig.bindingTariffClassificationUrl) thenReturn wireMockUrl
    when(appConfig.pdfGeneratorUrl) thenReturn wireMockUrl
    when(appConfig.emailUrl) thenReturn wireMockUrl

    when(appConfig.apiToken) thenReturn fakeAuthToken
  }

}
