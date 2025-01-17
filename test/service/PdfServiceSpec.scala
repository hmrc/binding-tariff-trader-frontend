/*
 * Copyright 2025 HM Revenue & Customs
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
import config.FrontendAppConfig
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import play.twirl.api.Html

import scala.concurrent.Future

class PdfServiceSpec extends SpecBase {

  private val pdfHtml             = mock(classOf[Html])
  private val config              = mock(classOf[FrontendAppConfig])
  private val pdfGeneratorService = mock(classOf[PdfGeneratorService])
  private val generatorResponse   = "Some content".getBytes

  private def service = new PdfService(pdfGeneratorService, config)

  "Service 'Render Pdf'" should {

    "delegate to PdfGeneratorService" in {
      given(pdfGeneratorService.render(any[Html], any[String])).willReturn(Future.successful(generatorResponse))

      val file = await(service.generatePdf(pdfHtml))

      file shouldBe generatorResponse

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
