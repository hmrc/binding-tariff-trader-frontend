/*
 * Copyright 2023 HM Revenue & Customs
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

import audit.AuditService
import connectors.FakeDataCacheConnector
import controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction, FakeIdentifierAction}
import mapper.CaseRequestMapper
import models._
import navigation.FakeNavigator
import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.{CheckYourAnswersPage, MakeFileConfidentialPage, UploadSupportingMaterialMultiplePage}
import play.api.http.Status
import play.api.libs.json._
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.Html
import service.{CasesService, CountriesService, FileService, PdfService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import viewmodels.AnswerSection
import views.html.check_your_answers

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.{failed, successful}

class CheckYourAnswersControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  private lazy val error = new IllegalStateException("expected error")
  private val testAnswer = "answer"

  private val mapper              = mock[CaseRequestMapper]
  private val newCaseReq          = mock[NewCaseRequest]
  private val attachment          = FileAttachment("file-id", "pikachu.jpg", "image/jpeg", 1L)
  private val publishedAttachment = PublishedFileAttachment("file-id", "pikachu.jpg", "image/jpeg", 1L)
  private val applicationPdf      = FileAttachment("id", "file.pdf", "application/pdf", 0L)
  private val createdCase         = mock[Case]
  private val auditService        = mock[AuditService]
  private val casesService        = mock[CasesService]
  private val pdfService          = mock[PdfService]
  private val fileService         = mock[FileService]
  private val btiApp              = mock[Application]

  private val countriesService = new CountriesService

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    when(createdCase.attachments).thenReturn(Seq.empty)
    when(createdCase.createdDate).thenReturn(Instant.now)
    when(createdCase.reference).thenReturn("reference")
    when(createdCase.application).thenReturn(btiApp)
    when(btiApp.agent).thenReturn(None)
    when(btiApp.holder).thenReturn(EORIDetails("eori", "", "", "", "", "", ""))
    when(btiApp.contact).thenReturn(Contact("luigi", "luigi@example.test", Some("0123")))
    when(btiApp.goodName).thenReturn("goods name")
    when(btiApp.goodDescription).thenReturn("goods description")
    when(btiApp.confidentialInformation).thenReturn(Some("goods description"))
    when(btiApp.envisagedCommodityCode).thenReturn(Some("goods description"))
    when(btiApp.knownLegalProceedings).thenReturn(Some("goods description"))
    when(btiApp.relatedBTIReferences).thenReturn(List("similar goods"))
    when(btiApp.sampleToBeProvided).thenReturn(true)
    when(btiApp.sampleIsHazardous).thenReturn(Some(true))
    when(btiApp.sampleToBeReturned).thenReturn(true)
    when(btiApp.reissuedBTIReference).thenReturn(Some("reissuedBTIReference"))

    when(mapper.map(any[UserAnswers])).thenReturn(newCaseReq)
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    reset(casesService, auditService)
  }

  val checkYourAnswersView: check_your_answers = app.injector.instanceOf(classOf[check_your_answers])

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new CheckYourAnswersController(
      frontendAppConfig,
      FakeDataCacheConnector,
      auditService,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      countriesService,
      casesService,
      pdfService,
      fileService,
      mapper,
      cc,
      checkYourAnswersView
    )

  "Check Your Answers Controller" must {

    "return 200 and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      status(result) shouldBe OK

      val expectedSections = Seq(
        AnswerSection(Some("checkYourAnswers.applicantRegisteredSection"), Seq.empty),
        AnswerSection(Some("checkYourAnswers.applicantOtherBusiness"), Seq.empty),
        AnswerSection(Some("checkYourAnswers.informationAboutYourItemSection"), Seq.empty),
        AnswerSection(Some("checkYourAnswers.otherInformation"), Seq.empty)
      )
      contentAsString(result) shouldBe checkYourAnswersView(frontendAppConfig, expectedSections, false)(
        fakeRequest,
        messages
      ).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }

  "return OK and the correct view for a POST" in {
    givenTheCaseCreatesSuccessfully()
    givenTheAttachmentPublishSucceeds()
    givenTheApplicationPdfGenerates()
    givenTheApplicationPdfIsUploaded()
    givenTheCaseIsUpdatedWithPdf()
    givenTheCaseCreatedEventIsSuccessful()

    val result = await(controller(extractDataFromCache).onSubmit()(fakeRequest))

    result.header.status            shouldBe Status.SEE_OTHER
    result.header.headers(LOCATION) shouldBe "/foo"
  }

  "create an event when the ATAR application has been submitted successfully" in {
    givenTheCaseCreatesSuccessfully()
    givenTheAttachmentPublishSucceeds()
    givenTheApplicationPdfGenerates()
    givenTheApplicationPdfIsUploaded()
    givenTheCaseIsUpdatedWithPdf()
    givenTheCaseCreatedEventIsSuccessful()

    val c   = controller(extractDataFromCache)
    val req = fakeRequest

    await(c.onSubmit()(req))

    verify(casesService, times(1))
      .addCaseCreatedEvent(refEq(createdCase), refEq(Operator("", Some("luigi"))))(any[HeaderCarrier])
  }

  "not create an event when the ATAR application failed to be submitted " in {
    givenTheCaseCreateFails()

    val c   = controller(extractDataFromCache)
    val req = fakeRequest

    val caught = intercept[RuntimeException] {
      await(c.onSubmit()(req))
    }

    caught shouldBe error

    verify(casesService, never()).addCaseCreatedEvent(any[Case], any[Operator])(any[HeaderCarrier])
  }

  "send the expected explicit audit events when the ATAR application has been submitted successfully" in {
    givenTheCaseCreatesSuccessfully()
    givenTheAttachmentPublishSucceeds()
    givenTheApplicationPdfGenerates()
    givenTheApplicationPdfIsUploaded()
    givenTheCaseIsUpdatedWithPdf()
    givenTheCaseCreatedEventIsSuccessful()

    val c   = controller(extractDataFromCache)
    val req = fakeRequest

    await(c.onSubmit()(req))

    verify(auditService, times(1)).auditBTIApplicationSubmissionSuccessful(refEq(createdCase))(any[HeaderCarrier])
    verifyNoMoreInteractions(auditService)
  }

  "not send the expected explicit audit events when the ATAR application failed to be submitted" in {
    givenTheCaseCreateFails()

    val c   = controller(extractDataFromCache)
    val req = fakeRequest

    val caught = intercept[error.type] {
      await(c.onSubmit()(req))
    }
    caught shouldBe error

    verify(auditService, never).auditBTIApplicationSubmissionSuccessful(any[Case])(any[HeaderCarrier])
    verifyNoMoreInteractions(auditService)
  }

  private def onwardRoute = Call("GET", "/foo")

  private def givenTheCaseCreatesSuccessfully(): Unit =
    when(casesService.create(any[NewCaseRequest])(any[HeaderCarrier])).thenReturn(successful(createdCase))

  private def givenTheCaseCreatedEventIsSuccessful(): Unit =
    when(casesService.addCaseCreatedEvent(any[Case], any[Operator])(any[HeaderCarrier])).thenReturn(successful(()))

  private def givenTheCaseCreateFails(): Unit =
    when(casesService.create(any[NewCaseRequest])(any[HeaderCarrier])).thenReturn(failed(error))

  private def givenTheAttachmentPublishSucceeds(): Unit =
    when(fileService.publish(any[Seq[FileAttachment]])(any[HeaderCarrier]))
      .thenReturn(successful(Seq(publishedAttachment)))

  private def givenTheApplicationPdfGenerates(): Unit =
    when(pdfService.generatePdf(any[Html])).thenReturn(successful(PdfFile(Array.empty, "application/pdf")))

  private def givenTheApplicationPdfIsUploaded(): Unit =
    when(fileService.uploadApplicationPdf(any[String], any[Array[Byte]])(any[HeaderCarrier]))
      .thenReturn(successful(applicationPdf))

  private def givenTheCaseIsUpdatedWithPdf(): Unit =
    when(casesService.update(any[String], any[CaseUpdate])(any[HeaderCarrier]))
      .thenReturn(successful(Some(createdCase)))

  private def extractDataFromCache: DataRetrievalAction = {
    val validData = Map(
      CheckYourAnswersPage.toString                 -> JsString(testAnswer),
      UploadSupportingMaterialMultiplePage.toString -> Json.toJson(Seq(attachment)),
      MakeFileConfidentialPage.toString             -> JsObject(Map("file-id" -> JsBoolean(false)))
    )
    new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
  }

}
