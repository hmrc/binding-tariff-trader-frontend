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
import models.PdfFile
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status
import play.twirl.api.Html

class PdfGeneratorServiceConnectorSpec extends ConnectorTest with ScalaFutures{

  private val pdfTemplate = mock[Html]

  private val connector = new PdfGeneratorServiceConnector(appConfig, wsClient)

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

      whenReady(connector.generatePdf(pdfTemplate)) { result =>
        result.contentType mustBe  "application/pdf"
        result.content mustBe expectedContent

        verify(
          postRequestedFor(urlEqualTo("/pdf-generator-service/generate"))
            .withoutHeader("X-Api-Token")
        )
      }
    }

    "throw exception when call fails" in {

      stubFor(
        post("/pdf-generator-service/generate")
          .willReturn(
            aResponse()
              .withStatus(Status.SERVICE_UNAVAILABLE)
          )
      )


      whenReady(connector.generatePdf(pdfTemplate).failed) { ex =>
        ex.getMessage contains "Error calling PdfGeneratorService"

        verify(
          postRequestedFor(urlEqualTo("/pdf-generator-service/generate"))
            .withoutHeader("X-Api-Token")
        )
      }
    }

  }

}
