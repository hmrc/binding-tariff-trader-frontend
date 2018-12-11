/*
 * Copyright 2018 HM Revenue & Customs
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

import java.nio.charset.Charset

import akka.stream.Materializer
import akka.util.ByteString
import audit.AuditService
import connectors.FakeDataCacheConnector
import controllers.actions._
import mapper.CaseRequestMapper
import models._
import navigation.FakeNavigator
import org.mockito.ArgumentMatchers.{any, anyString, refEq}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import pages.{DeclarationPage, UploadSupportingMaterialMultiplePage}
import play.api.http.Status
import play.api.libs.json.{JsString, Json}
import play.api.mvc.{Call, Result}
import play.api.test.Helpers._
import service.{CasesService, FileService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.declaration

import scala.concurrent.Future.{failed, successful}

class DeclarationControllerSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  private def onwardRoute = Call("GET", "/foo")

  private val testAnswer = "answer"

  private val mapper = mock[CaseRequestMapper]
  private val newCaseReq = mock[NewCaseRequest]
  private val attachment = mock[FileAttachment]
  private val attachments = Seq(attachment)
  private val publishedAttachment = mock[PublishedFileAttachment]
  private val unpublishedAttachment = mock[UnpublishedFileAttachment]
  private val createdCase = mock[Case]
  private val auditService = mock[AuditService]
  private val casesService = mock[CasesService]
  private val fileService = mock[FileService]

  private val fieldsToBeExcludedWhenComparingHeaderCarriers = List("requestChain", "nsStamp")

  private implicit val mat: Materializer = app.materializer

  private lazy val error = new IllegalStateException("expected error")

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(casesService, auditService)
    when(createdCase.reference).thenReturn("reference")
    when(mapper.map(any[UserAnswers])).thenReturn(newCaseReq)
  }

  override def afterEach(): Unit = {
    super.afterEach()
  }

  private def givenTheCaseCreatesSuccessfully(): Unit = {
    when(casesService.create(any[NewCaseRequest])(any[HeaderCarrier])).thenReturn(successful(createdCase))
  }

  private def givenTheCaseCreateFails(): Unit = {
    when(casesService.create(any[NewCaseRequest])(any[HeaderCarrier])).thenReturn(failed(error))
  }

  private def givenTheAttachmentPublishFails(): Unit = {
    when(fileService.publish(any[Seq[FileAttachment]])(any[HeaderCarrier])).thenReturn(failed(error))
  }

  private def givenTheAttachmentPublishesNone(): Unit = {
    when(fileService.publish(any[Seq[FileAttachment]])(any[HeaderCarrier])).thenReturn(successful(Seq(unpublishedAttachment)))
  }

  private def givenTheAttachmentPublishSucceeds(): Unit = {
    when(fileService.publish(any[Seq[FileAttachment]])(any[HeaderCarrier])).thenReturn(successful(Seq(publishedAttachment)))
  }

  "Declaration Controller" must {

    "return OK and the correct view for a GET" in {
      val result = await(controller().onPageLoad(NormalMode)(fakeRequest))

      result.header.status mustBe Status.OK
      bodyOf(result) mustBe viewAsString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val result = await(controller(extractDataFromCache).onPageLoad(NormalMode)(fakeRequest))

      result.header.status mustBe Status.OK
      bodyOf(result) mustBe viewAsString
    }

    "return OK and the correct view for a POST" in {
      givenTheCaseCreatesSuccessfully()
      givenTheAttachmentPublishSucceeds()

      val result = await(controller(extractDataFromCache).onSubmit(NormalMode)(fakeRequest))

      result.header.status mustBe Status.SEE_OTHER
      result.header.headers(LOCATION) mustBe "/foo"
    }

    "return OK and the correct view for a POST with failed attachments" in {
      givenTheCaseCreatesSuccessfully()
      givenTheAttachmentPublishesNone()

      val result = await(controller(extractDataFromCache).onSubmit(NormalMode)(fakeRequest))

      result.header.status mustBe Status.SEE_OTHER
      result.header.headers(LOCATION) mustBe "/foo"
    }

    "send the expected explicit audit events when the BTI application has been submitted successfully" in {
      givenTheCaseCreatesSuccessfully()

      val c = controller(extractDataFromCache)
      val req = fakeRequest
      val expectedHeaderCarrier = c.hc(req)

      await(c.onSubmit(NormalMode)(req))

      verify(auditService, times(1)).auditBTIApplicationSubmission(any[NewCaseRequest])(any[HeaderCarrier])
      verify(auditService, times(1)).auditBTIApplicationSubmissionSuccessful(refEq(createdCase))(any[HeaderCarrier])
      verify(auditService, never).auditBTIApplicationSubmissionFailed(any[NewCaseRequest], anyString)(any[HeaderCarrier])

      verifyNoMoreInteractions(auditService)
    }

    "send the expected explicit audit events when the BTI application failed to be submitted" in {
      givenTheCaseCreateFails()

      val c = controller(extractDataFromCache)
      val req = fakeRequest
      val expectedHeaderCarrier = c.hc(req)

      val caught = intercept[error.type] {
        await(c.onSubmit(NormalMode)(req))
      }
      caught mustBe error

      verify(auditService, times(1)).auditBTIApplicationSubmission(any[NewCaseRequest])(any[HeaderCarrier])
      verify(auditService, times(1)).auditBTIApplicationSubmissionFailed(any[NewCaseRequest], refEq(error.getMessage))(any[HeaderCarrier])
      verify(auditService, never).auditBTIApplicationSubmissionSuccessful(any[Case])(any[HeaderCarrier])

      verifyNoMoreInteractions(auditService)
    }

  }

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): DeclarationController = {
    new DeclarationController(
      frontendAppConfig,
      messagesApi,
      FakeDataCacheConnector,
      auditService,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      casesService,
      fileService,
      mapper
    )
  }

  private def extractDataFromCache: DataRetrievalAction = {
    val validData = Map(
      DeclarationPage.toString -> JsString(testAnswer),
      UploadSupportingMaterialMultiplePage.toString -> Json.toJson(Seq(attachment))
    )
    new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
  }

  private def bodyOf(result: Result)(implicit mat: Materializer): String = {
    val bodyBytes: ByteString = await(result.body.consumeData)
    bodyBytes.decodeString(Charset.defaultCharset().name)
  }

  private def viewAsString: String = {
    declaration(frontendAppConfig, NormalMode)(fakeRequest, messages).toString
  }

}
