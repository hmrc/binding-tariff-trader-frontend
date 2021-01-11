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

import com.github.tomakehurst.wiremock.client.WireMock._
import models.PdfFile
import play.api.http.Status
import play.twirl.api.Html

import scala.concurrent.ExecutionContext.Implicits.global

class PdfGeneratorServiceConnectorSpec extends ConnectorTest {

  private val pdfTemplate = Html("")

  private val connector = new PdfGeneratorServiceConnector(mockConfig, wsClient, metrics)

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

      verify(
        postRequestedFor(urlEqualTo("/pdf-generator-service/generate"))
          .withoutHeader("X-Api-Token")
      )
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

      verify(
        postRequestedFor(urlEqualTo("/pdf-generator-service/generate"))
          .withoutHeader("X-Api-Token")
      )
    }
  }

}
