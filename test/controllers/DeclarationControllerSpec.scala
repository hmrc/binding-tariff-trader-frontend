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
import org.mockito.Matchers._
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import pages.DeclarationPage
import play.api.libs.json.JsString
import play.api.mvc.Call
import play.api.test.Helpers._
import service.{AuditService, CasesService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.declaration

import scala.concurrent.Future

class DeclarationControllerSpec extends ControllerSpecBase with MockitoSugar {

  private def onwardRoute = Call("GET", "/foo")

  private val casesService = mock[CasesService]
  private val mapper = mock[CaseRequestMapper]
  private val newCase = mock[NewCaseRequest]
  private val createdCase = mock[Case]
  private val testAnswer = "answer"
  private val auditService = mock[AuditService]

  private val fieldsToExcludeFromHeaderCarrier = List("requestChain", "nsStamp")

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) = {
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

  private def viewAsString = declaration(frontendAppConfig, NormalMode)(fakeRequest, messages).toString

  private def reset(): Unit = {
//    Mockito.when(mapper.map(any[UserAnswers])).thenReturn(newCase)
//    Mockito.when(createdCase.reference).thenReturn("reference")

    // /CANNOT for Unit type
//    Mockito.when(auditService.auditBTIApplicationSubmissionFailed(any[NewCaseRequest])(any[HeaderCarrier])).thenReturn((): Unit)
//    Mockito.when(auditService.auditBTIApplicationSubmissionFilled(any[NewCaseRequest])(any[HeaderCarrier])).thenReturn((): Unit)
//    Mockito.when(auditService.auditBTIApplicationSubmissionSuccessful(any[Case])(any[HeaderCarrier])).thenReturn((): Unit)
  }

  "Declaration Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(DeclarationPage.toString -> JsString(testAnswer))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString
    }

    "return OK and the correct view for a POST" in {
      Mockito.reset(casesService, auditService)
      Mockito.when(casesService.createCase(refEq(newCase))(any[HeaderCarrier])).thenReturn(Future.successful(createdCase))
      reset()

      val result = controller().onSubmit(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/foo")
    }

    "send the expected explicit audit events when the BTI application information has been submitted successfully" in {
      Mockito.reset(casesService, auditService)
      Mockito.when(casesService.createCase(refEq(newCase))(any[HeaderCarrier])).thenReturn(Future.successful(createdCase))
      reset()

      val req = fakeRequest
      val validData = Map(DeclarationPage.toString -> JsString(testAnswer))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val c = controller(getRelevantData)
      val expectedHeaderCarrier = c.hc(req)

      // TODO: mock happy path
      c.onSubmit(NormalMode)(req)

      Mockito.verify(auditService, Mockito.times(1)).auditBTIApplicationSubmissionFilled(refEq(newCase))(refEq(expectedHeaderCarrier, fieldsToExcludeFromHeaderCarrier: _*))
      Mockito.verify(auditService, Mockito.times(1)).auditBTIApplicationSubmissionSuccessful(refEq(createdCase))(refEq(expectedHeaderCarrier, fieldsToExcludeFromHeaderCarrier: _*))
      Mockito.verify(auditService, Mockito.never()).auditBTIApplicationSubmissionFailed(any[NewCaseRequest])(any[HeaderCarrier])
    }

    "send the expected explicit audit events when the BTI application failed to be submitted" in {
      Mockito.reset(casesService, auditService)
      Mockito.when(casesService.createCase(refEq(newCase))(any[HeaderCarrier])).thenReturn(Future.failed(new RuntimeException))
      reset()

      val req = fakeRequest
      val validData = Map(DeclarationPage.toString -> JsString(testAnswer))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val c = controller(getRelevantData)
      val expectedHeaderCarrier = c.hc(req)

      // TODO: mock failure
      c.onSubmit(NormalMode)(req)

      Mockito.verify(auditService, Mockito.times(1)).auditBTIApplicationSubmissionFilled(refEq(newCase))(refEq(expectedHeaderCarrier, fieldsToExcludeFromHeaderCarrier: _*))
      Mockito.verify(auditService, Mockito.never()).auditBTIApplicationSubmissionSuccessful(any[Case])(any[HeaderCarrier])
      Mockito.verify(auditService, Mockito.times(1)).auditBTIApplicationSubmissionFailed(refEq(newCase))(refEq(expectedHeaderCarrier, fieldsToExcludeFromHeaderCarrier: _*))
    }

  }

}
