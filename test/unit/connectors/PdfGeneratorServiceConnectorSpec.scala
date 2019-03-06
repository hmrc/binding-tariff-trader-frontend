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

import com.github.tomakehurst.wiremock.client.WireMock._
import config.FrontendAppConfig
import models.PdfFile
import org.mockito.BDDMockito.given
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.ws.WSClient
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class PdfGeneratorServiceConnectorSpec extends UnitSpec with WithFakeApplication
  with WiremockTestServer with MockitoSugar with BeforeAndAfterEach with ResourceFiles {

  private val config = mock[FrontendAppConfig]
  private val pdfTemplate = mock[Html]
  private val wsClient: WSClient = fakeApplication.injector.instanceOf[WSClient]
  private implicit val headers: HeaderCarrier = HeaderCarrier()

  private val connector = new PdfGeneratorServiceConnector(config, wsClient)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    given(config.pdfGeneratorUrl).willReturn(wireMockUrl)
  }

  "Connector" should {

    "Generate Pdf" in {
      val expectedContent = "Some content".getBytes
      stubFor(
        post("/pdf-generator-service/generate")
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody(expectedContent)
          )
      )

      val response: PdfFile = await(connector.generatePdf(pdfTemplate))

      response.contentType shouldBe "application/pdf"
      response.content shouldBe expectedContent
    }

    "throw exception when call fails" in {

      stubFor(
        post("/pdf-generator-service/generate")
          .willReturn(
            aResponse()
              .withStatus(Status.SERVICE_UNAVAILABLE)
          )
      )

      val caught: Exception = intercept[Exception] {
        await(connector.generatePdf(pdfTemplate))
      }

      caught.getMessage contains "Error calling PdfGeneratorService"
    }
  }

}
