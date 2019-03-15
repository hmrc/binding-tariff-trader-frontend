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
import models.{Case, PdfFile, oCase}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.test.Helpers._
import play.twirl.api.Html
import service.{CasesService, FileService, PdfService}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future.{failed, successful}

class PdfDownloadControllerSpec extends ControllerSpecBase with MockitoSugar {

  private val pdfService = mock[PdfService]
  private val caseService = mock[CasesService]
  private val fileService = mock[FileService]
  private val expectedResult = PdfFile("Some content".getBytes)
  private val testCase = oCase.btiCaseExample
  private val testCaseWithRuling = oCase.btiCaseWithDecision
  private val token = "123"
  private val userEori = "eori-789012"

  private val request = IdentifierRequest(fakeRequest, "id", Some(userEori))

  private def controller(action: IdentifierAction = FakeIdentifierAction(Some(userEori))): PdfDownloadController = {
    new PdfDownloadController(
      frontendAppConfig,
      messagesApi,
      action,
      pdfService,
      caseService,
      fileService
    )
  }

  private def givenTheCaseServiceFindsTheCase(): Unit = {
    when(caseService.getCaseForUser(any[String], any[String])(any[HeaderCarrier])).thenReturn(successful(testCase))
  }

  private def givenTheCaseServiceFindsTheCaseWithRuling(): Unit = {
    when(caseService.getCaseWithRulingForUser(any[String], any[String])(any[HeaderCarrier])).thenReturn(successful(testCaseWithRuling))
  }

  private def givenTheCaseServiceDoesNotFindTheCase(): Unit = {
    when(caseService.getCaseForUser(any[String], any[String])(any[HeaderCarrier])).thenReturn(failed(new RuntimeException("Case not found")))
    when(caseService.getCaseWithRulingForUser(any[String], any[String])(any[HeaderCarrier])).thenReturn(failed(new RuntimeException("Case not found")))
  }

  private def givenTheFileServiceFindsTheAttachements(): Unit = {
    when(fileService.getAttachmentMetadata(any[Case])(any[HeaderCarrier])).thenReturn(successful(Seq.empty))
  }

  private def givenThePdfServiceDecodesTheTokenWith(eori: String, reference: String): Unit = {
    when(pdfService.decodeToken(any[String])).thenReturn(Some((eori, reference)))
  }

  private def givenThePdfServiceFailsToDecodeTheToken(): Unit = {
    when(pdfService.decodeToken(any[String])).thenReturn(None)
  }

  private def givenThePdfServiceGeneratesThePdf(): Unit = {
    when(pdfService.generatePdf(any[Html])).thenReturn(successful(expectedResult))
  }

  "PdfDownloadController Application" must {

    "return return PdfService result" in {
      givenThePdfServiceDecodesTheTokenWith("eori", "reference")
      givenTheCaseServiceFindsTheCase()
      givenTheFileServiceFindsTheAttachements()
      givenThePdfServiceGeneratesThePdf()

      val result = controller().application(token)(request)

      status(result) mustBe OK
      contentAsString(result) mustBe "Some content"
      contentType(result) mustBe Some("application/pdf")
    }

    "error when case not found" in {
      givenThePdfServiceDecodesTheTokenWith("eori", "reference")
      givenTheCaseServiceDoesNotFindTheCase()

      val caught: Exception = intercept[Exception] {
        await(controller().application(token)(request))
      }
      caught.getMessage mustBe "Case not found"
    }

    "redirect to session expired when the token is invalid" in {
      givenThePdfServiceFailsToDecodeTheToken()

      val result = controller(FakeIdentifierAction(None)).application(token)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

  }

  "PdfDownloadController Ruling" must {

    "return return PdfService result" in {
      givenThePdfServiceDecodesTheTokenWith("eori", "reference")
      givenTheCaseServiceFindsTheCaseWithRuling()
      givenThePdfServiceGeneratesThePdf()

      val result = controller().ruling(token)(request)

      status(result) mustBe OK
      contentAsString(result) mustBe "Some content"
      contentType(result) mustBe Some("application/pdf")
    }

    "error when case not found" in {
      givenThePdfServiceDecodesTheTokenWith("eori", "reference")
      givenTheCaseServiceDoesNotFindTheCase()

      val caught: Exception = intercept[Exception] {
        await(controller().ruling(token)(request))
      }
      caught.getMessage mustBe "Case not found"
    }

    "redirect to session expired when the token is invalid" in {
      givenThePdfServiceFailsToDecodeTheToken()

      val result = controller(FakeIdentifierAction(None)).ruling(token)(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

  }

}