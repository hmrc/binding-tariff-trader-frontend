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

package controllers

import controllers.actions._
import models.requests.IdentifierRequest
import models.{BinaryFile, Case, oCase}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.test.Helpers._
import play.twirl.api.Html
import service.{CasesService, FileService, PdfService}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future.successful

class PdfDownloadControllerSpec extends ControllerSpecBase with MockitoSugar {

  private val pdfService = mock[PdfService]
  private val caseService = mock[CasesService]
  private val fileService = mock[FileService]
  private val expectedResult = BinaryFile("application/pdf", "Some content".getBytes)
  private val testCase = oCase.btiCaseExample
  private val caseRef = "123"
  private val userEori = "eori-789012"

  private val fakeIdentityRequest = IdentifierRequest(fakeRequest, "id", userEori)

  private def controller(): PdfDownloadController = {
    new PdfDownloadController(
      frontendAppConfig,
      messagesApi,
      FakeIdentifierAction,
      pdfService,
      caseService,
      fileService
    )
  }

  private def givenTheCaseServiceFindsTheCase(expectedCase: Option[Case]): Unit = {
    when(caseService.getCaseForUser(any[String], any[String])(any[HeaderCarrier])).thenReturn(successful(expectedCase))
  }

  private def givenTheFileServiceFindsTheAttachements(): Unit = {
    when(fileService.getAttachmentMetadata(any[Case])(any[HeaderCarrier])).thenReturn(successful(Seq.empty))
  }

  private def givenThePdfServiceGeneratesThePdf(): Unit = {
    when(pdfService.generatePdf(any[Html])).thenReturn(successful(expectedResult))
  }

  "PdfDownloadController Controller" must {

    "return return PdfService result" in {
      givenTheCaseServiceFindsTheCase(Some(testCase))
      givenTheFileServiceFindsTheAttachements()
      givenThePdfServiceGeneratesThePdf()

      val result = controller().application(caseRef)(fakeIdentityRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe "Some content"
      contentType(result) mustBe Some("application/pdf")
    }

    "error when case not found" in {
      givenTheCaseServiceFindsTheCase(None)

      val caught: Exception = intercept[Exception] {
        await(controller().application(caseRef)(fakeIdentityRequest))
      }
      caught.getMessage mustBe s"Case (123) not found for user $userEori"
    }

  }

}