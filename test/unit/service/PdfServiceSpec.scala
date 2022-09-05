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

package service

import base.SpecBase
import config.{Crypto, FrontendAppConfig}
import connectors.PdfGeneratorServiceConnector
import models.PdfFile
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import play.twirl.api.Html

import scala.concurrent.Future._

class PdfServiceSpec extends SpecBase {

  private val pdfHtml           = mock[Html]
  private val config            = mock[FrontendAppConfig]
  private val connector         = mock[PdfGeneratorServiceConnector]
  private val connectorResponse = PdfFile("Some content".getBytes)

  private def service = new PdfService(connector, new Crypto(config))

  "Service 'Generate Pdf'" should {

    "delegate to connector" in {
      given(connector.generatePdf(any[Html])).willReturn(successful(connectorResponse))

      val file: PdfFile = await(service.generatePdf(pdfHtml))

      file shouldBe connectorResponse
    }

    "propagates errors" in {
      given(connector.generatePdf(any[Html])).willReturn(failed(new RuntimeException))

      intercept[RuntimeException] {
        await(service.generatePdf(pdfHtml))
      }
    }

  }

  "Service" should {
    val key = "9368B45C6E87AB6C45839EB23A123763"

    "return None when decoding a bad token with no aesKey specified" in {

      val token = service.decodeToken("token")

      token shouldBe None
    }

    "Decode BadToken" in {
      given(config.aesKey).willReturn(key)

      val token = service.decodeToken("token")

      token shouldBe None
    }

    "Reversibly Encode & Decode Token" in {
      given(config.aesKey).willReturn(key)

      val pair: Option[String] = service.decodeToken(service.encodeToken("eori"))

      pair shouldBe Some("eori")
    }
  }

}
