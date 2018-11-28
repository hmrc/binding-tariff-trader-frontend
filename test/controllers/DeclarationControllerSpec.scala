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

import connectors.FakeDataCacheConnector
import controllers.actions._
import mapper.CaseRequestMapper
import models.{Case, NewCaseRequest, NormalMode, UserAnswers}
import navigation.FakeNavigator
import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.Mockito.{doNothing, never, reset, times, verify, verifyNoMoreInteractions, verifyZeroInteractions, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import pages.DeclarationPage
import play.api.libs.json.JsString
import play.api.mvc.Call
import play.api.test.Helpers._
import service.{AuditService, CasesService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.declaration

import scala.concurrent.Future.{failed, successful}

class DeclarationControllerSpec extends ControllerSpecBase
  with MockitoSugar with BeforeAndAfterEach {

  private def onwardRoute = Call("GET", "/foo")

  private val testAnswer = "answer"

  private val mapper = mock[CaseRequestMapper]
  private val newCaseReq = mock[NewCaseRequest]
  private val createdCase = mock[Case]
  private val auditService = mock[AuditService]
  private val casesService = mock[CasesService]
//  private val userAnswers = mock[UserAnswers]

  private val fieldsToExcludeFromHeaderCarrier = List("requestChain", "nsStamp")

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
      mapper
    )
  }

  private def extractDataFromCache: DataRetrievalAction = {
    val validData = Map(DeclarationPage.toString -> JsString(testAnswer))
    new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
  }

  private def stubSuccessfulCaseCreation(): Unit = {
    when(casesService.createCase(refEq(newCaseReq))(any[HeaderCarrier])).thenReturn(successful(createdCase))
  }

  private def stubFailingCaseCreation(): Unit = {
    when(casesService.createCase(refEq(newCaseReq))(any[HeaderCarrier])).thenReturn(failed(new RuntimeException))
  }

  override def beforeEach(): Unit = {
    super.beforeEach()

    when(createdCase.reference).thenReturn("reference")
    when(mapper.map(any[UserAnswers])).thenReturn(newCaseReq)

//    doNothing().when(auditService.auditBTIApplicationSubmissionSuccessful(any[Case])(any[HeaderCarrier]))
//    doNothing().when(auditService.auditBTIApplicationSubmissionFilled(any[NewCaseRequest])(any[HeaderCarrier]))
//    doNothing().when(auditService.auditBTIApplicationSubmissionFailed(any[NewCaseRequest])(any[HeaderCarrier]))
  }

  override def afterEach(): Unit = {
    super.afterEach()

    reset(casesService, auditService, mapper, newCaseReq, createdCase)
  }

  private def viewAsString = {
    declaration(frontendAppConfig, NormalMode)(fakeRequest, messages).toString
  }

  "Declaration Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val result = controller(extractDataFromCache).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString
    }

    "return OK and the correct view for a POST" in {
      stubSuccessfulCaseCreation()

      val result = controller().onSubmit(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/foo")
    }

    "send the expected explicit audit events when the BTI application information has been submitted successfully" in {
      stubSuccessfulCaseCreation()

      val c = controller(extractDataFromCache)
      val req = fakeRequest
      val expectedHeaderCarrier = c.hc(req)

      c.onSubmit(NormalMode)(req)

      verify(auditService, never()).auditBTIApplicationSubmissionFailed(any[NewCaseRequest])(any[HeaderCarrier])
      verify(auditService, times(1)).auditBTIApplicationSubmissionFilled(refEq(newCaseReq))(refEq(expectedHeaderCarrier, fieldsToExcludeFromHeaderCarrier: _*))
      verify(auditService, times(1)).auditBTIApplicationSubmissionSuccessful(refEq(createdCase))(refEq(expectedHeaderCarrier, fieldsToExcludeFromHeaderCarrier: _*))

      verifyNoMoreInteractions(auditService)
      verifyZeroInteractions(auditService)
    }

    "send the expected explicit audit events when the BTI application failed to be submitted" in {
      stubFailingCaseCreation()

      val c = controller(extractDataFromCache)
      val req = fakeRequest
      val expectedHeaderCarrier = c.hc(req)

      c.onSubmit(NormalMode)(req)

      verify(auditService, never()).auditBTIApplicationSubmissionSuccessful(any[Case])(any[HeaderCarrier])
      verify(auditService, times(1)).auditBTIApplicationSubmissionFilled(refEq(newCaseReq))(refEq(expectedHeaderCarrier, fieldsToExcludeFromHeaderCarrier: _*))
      verify(auditService, times(1)).auditBTIApplicationSubmissionFailed(refEq(newCaseReq))(refEq(expectedHeaderCarrier, fieldsToExcludeFromHeaderCarrier: _*))

      verifyNoMoreInteractions(auditService)
      verifyZeroInteractions(auditService)
    }

  }

}
