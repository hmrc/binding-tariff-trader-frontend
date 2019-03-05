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
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.ws.WSResponse
import play.api.mvc._
import play.twirl.api.Html
import uk.gov.hmrc.play.test.UnitSpec
import play.api.test.Helpers._

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
      given(pdfResponse.bodyAsBytes).willReturn(ByteString())

      val result: Result = await(service.generatePdf("some.pdf", pdfHtml))

      result.header.status shouldBe Status.OK
      result.header.headers("Content-Disposition").toString shouldBe "attachment; filename=some.pdf"
      result.body.contentType shouldBe Some("application/pdf")
    }

    "connector fails" in {

      val expectedBody = "some error message"

      given(connector.generatePdf(any[Html])).willReturn(Future.successful(pdfResponse))
      given(pdfResponse.status).willReturn(Status.BAD_REQUEST)
      given(pdfResponse.body).willReturn(expectedBody)

      val result: Result = await(service.generatePdf("some.pdf", pdfHtml))

      result.header.status shouldBe Status.BAD_REQUEST
      contentAsString(result) shouldBe expectedBody
    }

  }

}
