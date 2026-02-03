/*
 * Copyright 2026 HM Revenue & Customs
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
import connectors.{BindingTariffClassificationConnector, EmailConnector}
import models.*
import models.requests.NewEventRequest
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.{mock, never, verify, when}
import play.api.libs.json.Writes
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CasesServiceSpec extends SpecBase {

  private val existingCase   = mock(classOf[Case])
  private val newCase        = mock(classOf[NewCaseRequest])
  private val pagination     = mock(classOf[Pagination])
  private val application    = mock(classOf[Application])
  private val contact        = mock(classOf[Contact])
  private val sort           = mock(classOf[Sort])
  private val caseConnector  = mock(classOf[BindingTariffClassificationConnector])
  private val emailConnector = mock(classOf[EmailConnector])
  private val traderEori     = "eoriTrader"
  private val agentEori      = "eoriAgent"
  private val caseRef        = "123114"

  private val service = new CasesService(caseConnector, emailConnector)

  "Service 'Create Case'" should {
    when(existingCase.reference).thenReturn(caseRef)
    when(existingCase.application).thenReturn(application)
    when(application.contact).thenReturn(contact)
    when(contact.email).thenReturn("email")
    when(contact.name).thenReturn("name")

    "delegate to connector" in {
      when(caseConnector.createCase(refEq(newCase))(any[HeaderCarrier])).thenReturn(Future.successful(existingCase))
      when(emailConnector.send(any[ApplicationSubmittedEmail])(any[HeaderCarrier], any[Writes[Email[?]]]))
        .thenReturn(Future.successful(()))

      await(service.create(newCase)(HeaderCarrier())) shouldBe existingCase

      theEmailSent shouldBe ApplicationSubmittedEmail(
        Seq("email"),
        ApplicationSubmittedParameters(
          "name",
          caseRef
        )
      )
    }

    "handle error with email silently" in {
      when(caseConnector.createCase(refEq(newCase))(any[HeaderCarrier])).thenReturn(Future.successful(existingCase))
      when(emailConnector.send(any[ApplicationSubmittedEmail])(any[HeaderCarrier], any[Writes[Email[?]]]))
        .thenReturn(Future.failed(new RuntimeException("Error")))

      await(service.create(newCase)(HeaderCarrier())) shouldBe existingCase
    }

    "propagate any error on case create" in {
      val exception = new RuntimeException("Error")
      when(caseConnector.createCase(any[NewCaseRequest])(any[HeaderCarrier])).thenReturn(Future.failed(exception))

      val caught = intercept[RuntimeException] {
        await(service.create(newCase)(HeaderCarrier()))
      }
      caught shouldBe exception
    }

    def theEmailSent: ApplicationSubmittedEmail = {
      val captor: ArgumentCaptor[ApplicationSubmittedEmail] =
        ArgumentCaptor.forClass(classOf[ApplicationSubmittedEmail])
      verify(emailConnector).send(captor.capture())(any[HeaderCarrier], any[Writes[Email[?]]])
      captor.getValue
    }
  }

  "Service 'find Cases'" should {

    "return paged cases" in {

      val pagedResult = Paged(Seq(oCase.btiCaseExample, oCase.btiCaseExample), 1, 2, 2)

      when(
        caseConnector
          .findCasesBy(refEq(caseRef), any[Set[CaseStatus]], refEq(pagination), refEq(sort))(any[HeaderCarrier])
      ).thenReturn(Future.successful(pagedResult))

      await(service.getCases(caseRef, Set.empty, pagination, sort)(HeaderCarrier())) shouldBe pagedResult
    }

    "propagate any error" in {
      val exception = new RuntimeException("Error")
      when(
        caseConnector
          .findCasesBy(refEq(caseRef), any[Set[CaseStatus]], refEq(pagination), refEq(sort))(any[HeaderCarrier])
      ).thenReturn(Future.failed(exception))

      val caught = intercept[RuntimeException] {
        await(service.getCases(caseRef, Set.empty, pagination, sort)(HeaderCarrier()))
      }
      caught shouldBe exception
    }
  }

  "Service put" should {

    "return appropriate case result" in {

      val caseResult = oCase.btiCaseExample

      when(caseConnector.putCase(refEq(existingCase))(any[HeaderCarrier])).thenReturn(Future.successful(caseResult))

      await(service.put(existingCase)(HeaderCarrier())) shouldBe caseResult
    }
  }

  "Service update" should {

    "return appropriate case result" in {

      val caseResult = Some(oCase.btiCaseExample)

      when(caseConnector.updateCase(refEq(caseRef), refEq(CaseUpdate(None)))(any[HeaderCarrier]))
        .thenReturn(Future.successful(caseResult))

      await(service.update(caseRef, CaseUpdate(None))(HeaderCarrier())) shouldBe caseResult
    }
  }

  "addCaseCreatedEvent" should {
    val atar     = oCase.btiCaseExample
    val operator = Operator("", Some(""))

    "create an event" in {

      when(caseConnector.createEvent(refEq(atar), any[NewEventRequest])(any[HeaderCarrier]))
        .thenReturn(Future.successful(mock(classOf[Event])))

      await(service.addCaseCreatedEvent(atar, operator)(HeaderCarrier())) shouldBe ()

      val eventCreated = theEventCreatedFor(caseConnector, atar)
      eventCreated.operator shouldBe Operator("", Some(""))
      eventCreated.details  shouldBe CaseCreated(comment = "Application submitted")
    }

    "not create an event when connector throws an exception" in {

      val exception = new RuntimeException("Error")

      when(caseConnector.createEvent(refEq(atar), any[NewEventRequest])(any[HeaderCarrier])).thenThrow(exception)

      val caught = intercept[RuntimeException] {
        await(service.addCaseCreatedEvent(atar, operator)(HeaderCarrier()))

      }

      caught shouldBe exception
      verify(caseConnector, never).createEvent(refEq(existingCase), any[NewEventRequest])(any[HeaderCarrier])
    }

    def theEventCreatedFor(connector: BindingTariffClassificationConnector, c: Case): NewEventRequest = {
      val captor: ArgumentCaptor[NewEventRequest] = ArgumentCaptor.forClass(classOf[NewEventRequest])
      verify(connector).createEvent(refEq(c), captor.capture())(any[HeaderCarrier])
      captor.getValue
    }
  }

  "Service 'Get Case For User'" should {

    "return case for trader" in {
      val traderCase = oCase.btiCaseExample.copy(application = oCase.btiApplicationExample.copy(agent = None))
      when(caseConnector.findCase(refEq(caseRef))(any[HeaderCarrier])).thenReturn(Future.successful(Some(traderCase)))

      await(service.getCaseForUser(traderEori, caseRef)(HeaderCarrier())) shouldBe traderCase
    }

    "return case for agent for both agent and trader" in {
      val agentCase = oCase.btiCaseExample
      when(caseConnector.findCase(refEq(caseRef))(any[HeaderCarrier])).thenReturn(Future.successful(Some(agentCase)))

      await(service.getCaseForUser(traderEori, caseRef)(HeaderCarrier())) shouldBe agentCase
      await(service.getCaseForUser(agentEori, caseRef)(HeaderCarrier()))  shouldBe agentCase
    }

    "not return case for another EORI" in {
      val someCase = oCase.btiCaseExample
      when(caseConnector.findCase(refEq(caseRef))(any[HeaderCarrier])).thenReturn(Future.successful(Some(someCase)))

      val caught = intercept[RuntimeException] {
        await(service.getCaseForUser("someEORI", caseRef)(HeaderCarrier()))
      }
      caught.getMessage shouldBe "Case not found"
    }

    "propagate any error" in {
      val exception = new RuntimeException("Error")
      when(caseConnector.findCase(refEq(caseRef))(any[HeaderCarrier])).thenReturn(Future.failed(exception))

      val caught = intercept[RuntimeException] {
        await(service.getCaseForUser("eori", caseRef)(HeaderCarrier()))
      }
      caught shouldBe exception
    }
  }

  "Service 'Get Case With Ruling For User'" should {

    "return ruling case for trader" in {
      val traderCase = oCase.btiCaseWithDecision.copy(application = oCase.btiApplicationExample.copy(agent = None))
      when(caseConnector.findCase(refEq(caseRef))(any[HeaderCarrier])).thenReturn(Future.successful(Some(traderCase)))

      await(service.getCaseWithRulingForUser(traderEori, caseRef)(HeaderCarrier())) shouldBe traderCase
    }

    "return ruling case for agent for both agent and trader" in {
      val agentCase = oCase.btiCaseWithDecision
      when(caseConnector.findCase(refEq(caseRef))(any[HeaderCarrier])).thenReturn(Future.successful(Some(agentCase)))

      await(service.getCaseWithRulingForUser(traderEori, caseRef)(HeaderCarrier())) shouldBe agentCase
      await(service.getCaseWithRulingForUser(agentEori, caseRef)(HeaderCarrier()))  shouldBe agentCase
    }

    "not return ruling case for another EORI" in {
      val someCase = oCase.btiCaseWithDecision
      when(caseConnector.findCase(refEq(caseRef))(any[HeaderCarrier])).thenReturn(Future.successful(Some(someCase)))

      val caught = intercept[RuntimeException] {
        await(service.getCaseWithRulingForUser("someEORT", caseRef)(HeaderCarrier()))
      }
      caught.getMessage shouldBe "Case not found"
    }

    "not return case without ruling" in {
      val someCase = oCase.btiCaseExample
      when(caseConnector.findCase(refEq(caseRef))(any[HeaderCarrier])).thenReturn(Future.successful(Some(someCase)))

      val caught = intercept[RuntimeException] {
        await(service.getCaseWithRulingForUser(traderEori, caseRef)(HeaderCarrier()))
      }
      caught.getMessage shouldBe "Case not found"
    }

    "propagate any error" in {
      val exception = new RuntimeException("Error")
      when(caseConnector.findCase(refEq(caseRef))(any[HeaderCarrier])).thenReturn(Future.failed(exception))

      val caught = intercept[RuntimeException] {
        await(service.getCaseForUser("eort", caseRef)(HeaderCarrier()))
      }
      caught shouldBe exception
    }
  }

}
