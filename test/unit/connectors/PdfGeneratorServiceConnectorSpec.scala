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
import com.github.tomakehurst.wiremock.client.WireMock._
import config.FrontendAppConfig
import org.mockito.BDDMockito.given
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import play.api.Environment
import play.api.http.Status
import play.api.libs.ws.{WSClient, WSResponse}
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.audit.DefaultAuditConnector
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class PdfGeneratorServiceConnectorSpec extends UnitSpec with WithFakeApplication
  with WiremockTestServer with MockitoSugar with BeforeAndAfterEach with ResourceFiles {

  private val config = mock[FrontendAppConfig]
  private val pdfTemplate = mock[Html]
  private val actorSystem = ActorSystem.create("test")
  private val wsClient: WSClient = fakeApplication.injector.instanceOf[WSClient]
  private val auditConnector = new DefaultAuditConnector(fakeApplication.configuration, fakeApplication.injector.instanceOf[Environment])
  private val hmrcWsClient = new DefaultHttpClient(fakeApplication.configuration, auditConnector, wsClient, actorSystem)
  private implicit val headers: HeaderCarrier = HeaderCarrier()

  private val connector = new PdfGeneratorServiceConnector(config, wsClient)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    given(config.pdfGeneratorUrl).willReturn(wireMockUrl)
  }

  "Connector" should {

    "Generate Pdf" in {
      stubFor(
        post("/pdf-generator-service/generate")
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
          )
      )

      val response: WSResponse = await(connector.generatePdf(pdfTemplate))

      response.status shouldBe(Status.OK)
    }
  }

}
