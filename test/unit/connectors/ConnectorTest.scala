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

package connectors

import akka.actor.ActorSystem
import base.SpecBase
import com.kenshoo.play.metrics.Metrics
import config.FrontendAppConfig
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterAll
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import unit.base.WireMockObject
import unit.utils.TestMetrics

import scala.io.Source

trait ConnectorTest
  extends SpecBase
    with BeforeAndAfterAll {

  protected lazy val mockConfig: FrontendAppConfig = mock[FrontendAppConfig]

  protected lazy val fakeAuthToken = "AUTH_TOKEN"
  protected lazy val wsClient: WSClient = injector.instanceOf[WSClient]
  protected lazy val metrics: Metrics = new TestMetrics
  protected lazy val authenticatedHttpClient = new AuthenticatedHttpClient(
    auditing,
    wsClient,
    actorSystem
  )(appConfig)

  protected lazy val standardHttpClient = new DefaultHttpClient(
    app.configuration,
    auditing,
    wsClient,
    actorSystem
  )

  protected implicit val hc: HeaderCarrier = HeaderCarrier()
  private lazy val actorSystem = ActorSystem.create("testActorSystem")
  private lazy val auditing = injector.instanceOf[HttpAuditing]

  override def beforeAll(): Unit = {
    super.beforeAll()

    when(mockConfig.bindingTariffFileStoreUrl) thenReturn WireMockObject.wireMockUrl
    when(mockConfig.bindingTariffClassificationUrl) thenReturn WireMockObject.wireMockUrl
    when(mockConfig.pdfGeneratorUrl) thenReturn WireMockObject.wireMockUrl
    when(mockConfig.emailUrl) thenReturn WireMockObject.wireMockUrl

    when(mockConfig.apiToken) thenReturn fakeAuthToken
  }

  protected def fromResource(path: String): String = {
    val url = getClass.getClassLoader.getResource(path)
    Source.fromURL(url, "UTF-8").getLines().mkString
  }
}
