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

import akka.util.ByteString
import connectors.PdfGeneratorServiceConnector
import models.BinaryFile
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.ws.WSResponse
import play.twirl.api.Html
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class PdfServiceSpec extends UnitSpec with MockitoSugar {

  private val pdfResponse = mock[WSResponse]
  private val pdfHtml = mock[Html]
  private val connector = mock[PdfGeneratorServiceConnector]

  private val service = new PdfService(connector)

  "Service 'Generate Pdf'" should {

    "delegate to connector" in {
      given(connector.generatePdf(any[Html])).willReturn(Future.successful(pdfResponse))
      given(pdfResponse.status).willReturn(Status.OK)
      val byteString = ByteString()
      given(pdfResponse.bodyAsBytes).willReturn(byteString)

      val file: BinaryFile = await(service.generatePdf(pdfHtml))

      file.contentType shouldBe "application/pdf"
      file.content shouldBe byteString.toArray
    }

    "throw exception when connector fails" in {

      given(connector.generatePdf(any[Html])).willReturn(Future.successful(pdfResponse))
      given(pdfResponse.status).willReturn(Status.BAD_REQUEST)

      val caught: Exception = intercept[Exception] {
        await(service.generatePdf(pdfHtml))
      }

      caught.getMessage contains "Error calling PdfGeneratorService"
    }

  }

}
