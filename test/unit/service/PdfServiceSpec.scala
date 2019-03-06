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

package service

import connectors.PdfGeneratorServiceConnector
import models.PdfFile
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import play.twirl.api.Html
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class PdfServiceSpec extends UnitSpec with MockitoSugar {

  private val pdfHtml = mock[Html]
  private val connector = mock[PdfGeneratorServiceConnector]
  private val connectorResponse = PdfFile("Some content".getBytes)

  private val service = new PdfService(connector)

  "Service 'Generate Pdf'" should {

    "delegate to connector" in {
      given(connector.generatePdf(any[Html])).willReturn(Future.successful(connectorResponse))

      val file: PdfFile = await(service.generatePdf(pdfHtml))

      file shouldBe connectorResponse
    }
  }

}
