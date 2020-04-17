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

package connectors

import akka.actor.ActorSystem
import config.FrontendAppConfig
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.Injector
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.io.Source

trait ConnectorTest
  extends UnitSpec
    with WiremockTestServer
    with MockitoSugar
    with WithFakeApplication {

  protected lazy val injector: Injector = fakeApplication.injector
  protected val appConfig: FrontendAppConfig = mock[FrontendAppConfig]

  protected implicit val realConfig: FrontendAppConfig = fakeApplication.injector.instanceOf[FrontendAppConfig]
  protected val fakeAuthToken = "AUTH_TOKEN"
  protected val wsClient: WSClient = fakeApplication.injector.instanceOf[WSClient]
  protected val authenticatedHttpClient = new AuthenticatedHttpClient(
    auditing,
    wsClient,
    actorSystem
  )
  protected val standardHttpClient = new DefaultHttpClient(
    fakeApplication.configuration,
    auditing,
    wsClient,
    actorSystem
  )

  protected implicit val hc: HeaderCarrier = HeaderCarrier()
  private val actorSystem = ActorSystem.create("testActorSystem")
  private val auditing = injector.instanceOf[HttpAuditing]

  override def beforeAll(): Unit = {
    super.beforeAll()

    when(appConfig.bindingTariffFileStoreUrl) thenReturn wireMockUrl
    when(appConfig.bindingTariffClassificationUrl) thenReturn wireMockUrl
    when(appConfig.pdfGeneratorUrl) thenReturn wireMockUrl
    when(appConfig.emailUrl) thenReturn wireMockUrl

    when(appConfig.apiToken) thenReturn fakeAuthToken
  }

    protected def fromResource(path: String): String = {
      val url = getClass.getClassLoader.getResource(path)
      Source.fromURL(url, "UTF-8").getLines().mkString
  }
}
