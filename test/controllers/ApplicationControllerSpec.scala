/*
 * Copyright 2024 HM Revenue & Customs
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

import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import controllers.actions._
import models.requests.IdentifierRequest
import models.response.FilestoreResponse
import models.{Attachment, Case, oCase}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.stubbing.ScalaOngoingStubbing
import org.scalatest.BeforeAndAfterEach
import play.api.test.Helpers._
import service.{CasesService, CountriesService, FileService, PdfService}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.documentNotFound
import views.html.templates.{applicationView, rulingCertificateView}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.{failed, successful}

class ApplicationControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  private val pdfService         = mock[PdfService]
  private val caseService        = mock[CasesService]
  private val fileService        = mock[FileService]
  private val countriesService   = new CountriesService
  private val testCase           = oCase.btiCaseExample
  private val testCaseWithRuling = oCase.btiCaseWithDecision
  private val caseRef            = "ref"
  private val token              = "123"
  private val userEori           = "eori-789012"

  private val request = IdentifierRequest(fakeRequest, "id", Some(userEori))

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(
      pdfService,
      caseService,
      fileService
    )
  }

  val applicationView: applicationView = app.injector.instanceOf(classOf[views.html.templates.applicationView])
  val rulingCertificateView: rulingCertificateView =
    app.injector.instanceOf(classOf[views.html.templates.rulingCertificateView])
  val documentNotFoundView: documentNotFound = app.injector.instanceOf(classOf[views.html.documentNotFound])

  private def controller(action: IdentifierAction = FakeIdentifierAction(Some(userEori))): ApplicationController =
    new ApplicationController(
      frontendAppConfig,
      action,
      pdfService,
      caseService,
      fileService,
      countriesService,
      cc,
      applicationView,
      rulingCertificateView,
      documentNotFoundView
    )

  private def givenTheCaseServiceFindsTheCase(): ScalaOngoingStubbing[Future[Case]] =
    when(caseService.getCaseForUser(any[String], any[String])(any[HeaderCarrier])).thenReturn(successful(testCase))

  private def givenTheCaseWithRulingFindsTheCaseWithRuling(): ScalaOngoingStubbing[Future[Case]] =
    when(caseService.getCaseWithRulingForUser(any[String], any[String])(any[HeaderCarrier]))
      .thenReturn(successful(testCaseWithRuling))

  private def givenTheCaseServiceDoesNotFindTheCase(): ScalaOngoingStubbing[Future[Case]] = {
    when(caseService.getCaseForUser(any[String], any[String])(any[HeaderCarrier]))
      .thenReturn(failed(new RuntimeException("Case not found")))
    when(caseService.getCaseWithRulingForUser(any[String], any[String])(any[HeaderCarrier]))
      .thenReturn(failed(new RuntimeException("Case not found")))
  }

  private def givenTheFileServiceFindsTheAttachments(): ScalaOngoingStubbing[Future[Seq[FilestoreResponse]]] =
    when(fileService.getAttachmentMetadata(any[Case])(any[HeaderCarrier])).thenReturn(successful(Seq.empty))

  private def givenTheFileServiceHaveNoLetterOfAuthority(): ScalaOngoingStubbing[Future[Option[FilestoreResponse]]] =
    when(fileService.getLetterOfAuthority(any[Case])(any[HeaderCarrier])).thenReturn(successful(None))

  private def givenThePdfServiceDecodesTheTokenWith(eori: String): ScalaOngoingStubbing[Option[String]] =
    when(pdfService.decodeToken(any[String])).thenReturn(Some(eori))

  private def givenThePdfServiceFailsToDecodeTheToken(): ScalaOngoingStubbing[Option[String]] =
    when(pdfService.decodeToken(any[String])).thenReturn(None)

  private def givenTheFileServiceFindsThePdf(): ScalaOngoingStubbing[Future[Option[Source[ByteString, _]]]] = {
    when(fileService.getAttachmentMetadata(any[Attachment])(any[HeaderCarrier])).thenReturn(
      successful(Some(FilestoreResponse("id", "some.pdf", "application/pdf", Some("http://localhost:4572/file/id"))))
    )
    when(fileService.downloadFile(any[String])(any[HeaderCarrier]))
      .thenReturn(successful(Some(Source.single(ByteString("Some content".getBytes())))))
  }

  private def givenTheFileServiceCannotBeReached(): ScalaOngoingStubbing[Future[Option[FilestoreResponse]]] =
    when(fileService.getAttachmentMetadata(any[Attachment])(any[HeaderCarrier])).thenReturn(failed(new Exception))

  private def givenTheFileServiceFindsNoPdf(): ScalaOngoingStubbing[Future[Option[FilestoreResponse]]] =
    when(fileService.getAttachmentMetadata(any[Attachment])(any[HeaderCarrier])).thenReturn(successful(None))

  "Application Pdf" must {

    "return 200 when a PDF can be found" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseServiceFindsTheCase()
      givenTheFileServiceFindsTheAttachments()
      givenTheFileServiceHaveNoLetterOfAuthority()
      givenTheFileServiceFindsThePdf()

      val result = controller().applicationPdf(caseRef, Some(token))(request)

      status(result)                        shouldBe OK
      contentAsString(result)               shouldBe "Some content"
      contentType(result)                   shouldBe Some("application/pdf")
      header("Content-Disposition", result) shouldBe Some("attachment; filename=some.pdf")
    }

    "return 404 when there is no PDF" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseServiceFindsTheCase()
      givenTheFileServiceFindsTheAttachments()
      givenTheFileServiceHaveNoLetterOfAuthority()
      givenTheFileServiceFindsNoPdf()

      val result = controller().applicationPdf(caseRef, Some(token))(request)

      status(result)          shouldBe NOT_FOUND
      contentAsString(result) should include(messages("documentNotFound.application"))
      contentType(result)     shouldBe Some("text/html")
    }

    "return 502 when the filestore cannot be reached" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseServiceFindsTheCase()
      givenTheFileServiceFindsTheAttachments()
      givenTheFileServiceCannotBeReached()

      val result = controller().applicationPdf(caseRef, Some(token))(request)

      status(result) shouldBe BAD_GATEWAY
    }

    "return 404 when no case is found" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseServiceDoesNotFindTheCase()

      val caught: Exception = intercept[Exception] {
        await(controller().applicationPdf(caseRef, Some(token))(request))
      }
      caught.getMessage shouldBe "Case not found"
    }

    "redirect to session expired when the token is invalid" in {
      givenThePdfServiceFailsToDecodeTheToken()

      val result = controller(FakeIdentifierAction(None)).applicationPdf(caseRef, Some(token))(request)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to unauthorized when the token is empty and session EORI is not present" in {
      givenThePdfServiceFailsToDecodeTheToken()

      val result = controller(FakeIdentifierAction(None)).applicationPdf(caseRef, None)(request)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.ErrorController.onPageLoad.url)
    }

  }

  "Application View" must {

    "return application view result" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseServiceFindsTheCase()
      givenTheFileServiceFindsTheAttachments()
      givenTheFileServiceHaveNoLetterOfAuthority()

      val result = controller().viewApplication(caseRef, Some(token))(request)

      status(result)          shouldBe OK
      contentAsString(result) should include(messages("view.application.header"))
      contentType(result)     shouldBe Some("text/html")
    }

    "error when case not found" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseServiceDoesNotFindTheCase()

      val caught: Exception = intercept[Exception] {
        await(controller().viewApplication(caseRef, Some(token))(request))
      }
      caught.getMessage shouldBe "Case not found"
    }

    "redirect to session expired when the token is invalid" in {
      givenThePdfServiceFailsToDecodeTheToken()

      val result = controller(FakeIdentifierAction(None)).viewApplication(caseRef, Some(token))(request)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }

  "Ruling Pdf" must {

    "return 200 when a PDF can be found" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseWithRulingFindsTheCaseWithRuling()
      givenTheFileServiceFindsThePdf()

      val result = controller().rulingCertificatePdf(caseRef, Some(token))(request)

      status(result)                        shouldBe OK
      contentType(result)                   shouldBe Some("application/pdf")
      header("Content-Disposition", result) shouldBe Some("attachment; filename=some.pdf")
    }

    "return 404 when there is no PDF" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseWithRulingFindsTheCaseWithRuling()
      givenTheFileServiceFindsNoPdf()

      val result = controller().rulingCertificatePdf(caseRef, Some(token))(request)

      status(result)          shouldBe NOT_FOUND
      contentAsString(result) should include(messages("documentNotFound.rulingCertificate"))
      contentType(result)     shouldBe Some("text/html")
    }

    "return 502 when the filestore cannot be reached" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseWithRulingFindsTheCaseWithRuling()
      givenTheFileServiceCannotBeReached()

      val result = controller().rulingCertificatePdf(caseRef, Some(token))(request)

      status(result) shouldBe BAD_GATEWAY
    }

    "return 404 when no case is found" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseServiceDoesNotFindTheCase()

      val caught: Exception = intercept[Exception] {
        await(controller().rulingCertificatePdf(caseRef, Some(token))(request))
      }
      caught.getMessage shouldBe "Case not found"
    }

    "redirect to session expired when the token is invalid" in {
      givenThePdfServiceFailsToDecodeTheToken()

      val result = controller(FakeIdentifierAction(None)).rulingCertificatePdf(caseRef, Some(token))(request)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to unauthorized when the token is empty and session EORI is not present" in {
      givenThePdfServiceFailsToDecodeTheToken()

      val result = controller(FakeIdentifierAction(None)).rulingCertificatePdf(caseRef, None)(request)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.ErrorController.onPageLoad.url)
    }

  }

  "Cover Letter Pdf" must {

    "return 200 when a PDF can be found" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseWithRulingFindsTheCaseWithRuling()
      givenTheFileServiceFindsThePdf()

      val result = controller().coverLetterPdf(caseRef, Some(token))(request)

      status(result)                        shouldBe OK
      contentType(result)                   shouldBe Some("application/pdf")
      header("Content-Disposition", result) shouldBe Some("attachment; filename=some.pdf")
    }

    "return 404 when there is no PDF" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseWithRulingFindsTheCaseWithRuling()
      givenTheFileServiceFindsNoPdf()

      val result = controller().coverLetterPdf(caseRef, Some(token))(request)

      status(result)          shouldBe NOT_FOUND
      contentAsString(result) should include(messages("documentNotFound.rulingCertificate"))
      contentType(result)     shouldBe Some("text/html")
    }

    "return 502 when the filestore cannot be reached" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseWithRulingFindsTheCaseWithRuling()
      givenTheFileServiceCannotBeReached()

      val result = controller().coverLetterPdf(caseRef, Some(token))(request)

      status(result) shouldBe BAD_GATEWAY
    }

    "return 404 when no case is found" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseServiceDoesNotFindTheCase()

      val caught: Exception = intercept[Exception] {
        await(controller().coverLetterPdf(caseRef, Some(token))(request))
      }
      caught.getMessage shouldBe "Case not found"
    }

    "redirect to session expired when the token is invalid" in {
      givenThePdfServiceFailsToDecodeTheToken()

      val result = controller(FakeIdentifierAction(None)).coverLetterPdf(caseRef, Some(token))(request)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to unauthorized when the token is empty and session EORI is not present" in {
      givenThePdfServiceFailsToDecodeTheToken()

      val result = controller(FakeIdentifierAction(None)).coverLetterPdf(caseRef, None)(request)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.ErrorController.onPageLoad.url)
    }

  }

  "Ruling View" must {

    "return ruling view result" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseWithRulingFindsTheCaseWithRuling()

      val result = controller().viewRulingCertificate(caseRef, Some(token))(request)

      status(result)          shouldBe OK
      contentAsString(result) should include("Advance Tariff Ruling certificate")
      contentType(result)     shouldBe Some("text/html")
    }

    "error when case not found" in {
      givenThePdfServiceDecodesTheTokenWith("eori")
      givenTheCaseServiceDoesNotFindTheCase()

      val caught: Exception = intercept[Exception] {
        await(controller().viewRulingCertificate(caseRef, Some(token))(request))
      }
      caught.getMessage shouldBe "Case not found"
    }

    "redirect to session expired when the token is invalid" in {
      givenThePdfServiceFailsToDecodeTheToken()

      val result = controller(FakeIdentifierAction(None)).viewRulingCertificate(caseRef, Some(token))(request)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

  }

  "getCountryName" must {

    "return a valid country when given a valid country code" in {
      val result: Option[String] = controller().getCountryName("IE")

      result shouldBe Some("title.ireland")
    }
  }
}
