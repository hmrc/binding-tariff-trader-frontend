/*
 * Copyright 2020 HM Revenue & Customs
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

import akka.stream.Materializer
import audit.AuditService
import connectors.FakeDataCacheConnector
import controllers.actions._
import mapper.CaseRequestMapper
import models._
import navigation.FakeNavigator
import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.{DeclarationPage, UploadSupportingMaterialMultiplePage}
import play.api.http.Status
import play.api.libs.json.{JsString, Json}
import play.api.mvc.Call
import play.api.test.Helpers._
import service.{CasesService, FileService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.declaration

import scala.concurrent.Future.{failed, successful}

class DeclarationControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {
  private lazy val error = new IllegalStateException("expected error")
  private val testAnswer = "answer"

  private val mapper = mock[CaseRequestMapper]
  private val newCaseReq = mock[NewCaseRequest]
  private val attachment = mock[FileAttachment]
  private val publishedAttachment = mock[PublishedFileAttachment]
  private val createdCase = mock[Case]
  private val auditService = mock[AuditService]
  private val casesService = mock[CasesService]
  private val fileService = mock[FileService]
  private val btiApp = mock[Application]
  private val contact = mock[Contact]

  private implicit val mat: Materializer = app.materializer

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    when(createdCase.reference).thenReturn("reference")
    when(createdCase.application).thenReturn(btiApp)
    when(btiApp.agent).thenReturn(None)
    when(btiApp.holder).thenReturn(EORIDetails("eori", "", "", "", "", "", ""))
    when(btiApp.contact).thenReturn(contact)
    when(contact.email).thenReturn("luigi@example.test")

    when(mapper.map(any[UserAnswers])).thenReturn(newCaseReq)
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    reset(casesService, auditService)
  }

  private def givenTheCaseCreatesSuccessfully(): Unit = {
    when(casesService.create(any[NewCaseRequest])(any[HeaderCarrier])).thenReturn(successful(createdCase))
  }

  private def givenTheCaseCreateFails(): Unit = {
    when(casesService.create(any[NewCaseRequest])(any[HeaderCarrier])).thenReturn(failed(error))
  }

  private def givenTheAttachmentPublishSucceeds(): Unit = {
    when(fileService.publish(any[Seq[FileAttachment]])(any[HeaderCarrier])).thenReturn(successful(Seq(publishedAttachment)))
  }

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): DeclarationController = {
    new DeclarationController(
      frontendAppConfig,
      FakeDataCacheConnector,
      auditService,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      casesService,
      fileService,
      mapper,
      cc
    )
  }

  "Declaration Controller" must {

    "return OK and the correct view for a GET" in {
      val result = await(controller().onPageLoad(NormalMode)(fakeRequest))

      result.header.status shouldBe Status.OK
      bodyOf(result) shouldBe viewAsString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val result = await(controller(extractDataFromCache).onPageLoad(NormalMode)(fakeRequest))

      result.header.status shouldBe Status.OK
      bodyOf(result) shouldBe viewAsString
    }

    "return OK and the correct view for a POST" in {
      givenTheCaseCreatesSuccessfully()
      givenTheAttachmentPublishSucceeds()

      val result = await(controller(extractDataFromCache).onSubmit(NormalMode)(fakeRequest))

      result.header.status shouldBe Status.SEE_OTHER
      result.header.headers(LOCATION) shouldBe "/foo"
    }

    "send the expected explicit audit events when the BTI application has been submitted successfully" in {
      givenTheCaseCreatesSuccessfully()

      val c = controller(extractDataFromCache)
      val req = fakeRequest

      await(c.onSubmit(NormalMode)(req))

      verify(auditService, times(1)).auditBTIApplicationSubmissionSuccessful(refEq(createdCase))(any[HeaderCarrier])
      verifyNoMoreInteractions(auditService)
    }

    "not send the expected explicit audit events when the BTI application failed to be submitted" in {
      givenTheCaseCreateFails()

      val c = controller(extractDataFromCache)
      val req = fakeRequest

      val caught = intercept[error.type] {
        await(c.onSubmit(NormalMode)(req))
      }
      caught shouldBe error

      verify(auditService, never).auditBTIApplicationSubmissionSuccessful(any[Case])(any[HeaderCarrier])
      verifyNoMoreInteractions(auditService)
    }

  }

  private def onwardRoute = Call("GET", "/foo")

  private def extractDataFromCache: DataRetrievalAction = {
    val validData = Map(
      DeclarationPage.toString -> JsString(testAnswer),
      UploadSupportingMaterialMultiplePage.toString -> Json.toJson(Seq(attachment))
    )
    new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
  }

  private def viewAsString: String = {
    declaration(frontendAppConfig, NormalMode)(fakeRequest, messages).toString
  }

}
