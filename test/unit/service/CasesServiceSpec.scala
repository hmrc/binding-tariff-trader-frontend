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

package service

import connectors.BindingTariffClassificationConnector
import models.{Case, NewCaseRequest, oCase}
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class CasesServiceSpec extends UnitSpec with MockitoSugar {

  private val existingCase = mock[Case]
  private val newCase = mock[NewCaseRequest]
  private val connector = mock[BindingTariffClassificationConnector]
  private val traderEori = "eoriTrader"
  private val agentEori = "eoriAgent"
  private val caseRef = "123114"

  private val service = new CasesService(connector)

  "Service 'Create Case'" should {

    "delegate to connector" in {
      given(connector.createCase(refEq(newCase))(any[HeaderCarrier])).willReturn(Future.successful(existingCase))

      await(service.create(newCase)(HeaderCarrier())) shouldBe existingCase
    }

    "propagate any error" in {
      val exception = new RuntimeException("Error")
      given(connector.createCase(any[NewCaseRequest])(any[HeaderCarrier])).willThrow(exception)

      val caught = intercept[RuntimeException] {
        await(service.create(newCase)(HeaderCarrier()))
      }
      caught shouldBe exception
    }
  }

  "Service 'Get Case For User'" should {

    "return case for trader" in {
      val traderCase = oCase.btiCaseExample.copy(application = oCase.btiApplicationExample.copy(agent = None))
      given(connector.findCase(refEq(caseRef))(any[HeaderCarrier])).willReturn(Future.successful(Some(traderCase)))

      await(service.getCaseForUser(traderEori, caseRef)(HeaderCarrier())) shouldBe traderCase
    }

    "return case for agent for both agent and trader" in {
      val agentCase = oCase.btiCaseExample
      given(connector.findCase(refEq(caseRef))(any[HeaderCarrier])).willReturn(Future.successful(Some(agentCase)))

      await(service.getCaseForUser(traderEori, caseRef)(HeaderCarrier())) shouldBe agentCase
      await(service.getCaseForUser(agentEori, caseRef)(HeaderCarrier())) shouldBe agentCase
    }

    "not return case for another EORI" in {
      val someCase = oCase.btiCaseExample
      given(connector.findCase(refEq(caseRef))(any[HeaderCarrier])).willReturn(Future.successful(Some(someCase)))

      val caught = intercept[RuntimeException] {
        await(service.getCaseForUser("someEORI", caseRef)(HeaderCarrier()))
      }
      caught.getMessage shouldBe "Case not found"
    }

    "propagate any error" in {
      val exception = new RuntimeException("Error")
      given(connector.findCase(refEq(caseRef))(any[HeaderCarrier])).willThrow(exception)

      val caught = intercept[RuntimeException] {
        await(service.getCaseForUser("eori", caseRef)(HeaderCarrier()))
      }
      caught shouldBe exception
    }
  }

  "Service 'Get Case With Ruling For User'" should {

    "return ruling case for trader" in {
      val traderCase = oCase.btiCaseWithDecision.copy(application = oCase.btiApplicationExample.copy(agent = None))
      given(connector.findCase(refEq(caseRef))(any[HeaderCarrier])).willReturn(Future.successful(Some(traderCase)))

      await(service.getCaseWithRulingForUser(traderEori, caseRef)(HeaderCarrier())) shouldBe traderCase
    }

    "return ruling case for agent for both agent and trader" in {
      val agentCase = oCase.btiCaseWithDecision
      given(connector.findCase(refEq(caseRef))(any[HeaderCarrier])).willReturn(Future.successful(Some(agentCase)))

      await(service.getCaseWithRulingForUser(traderEori, caseRef)(HeaderCarrier())) shouldBe agentCase
      await(service.getCaseWithRulingForUser(agentEori, caseRef)(HeaderCarrier())) shouldBe agentCase
    }

    "not return ruling case for another EORI" in {
      val someCase = oCase.btiCaseWithDecision
      given(connector.findCase(refEq(caseRef))(any[HeaderCarrier])).willReturn(Future.successful(Some(someCase)))

      val caught = intercept[RuntimeException] {
        await(service.getCaseWithRulingForUser("someEORT", caseRef)(HeaderCarrier()))
      }
      caught.getMessage shouldBe "Case not found"
    }

    "not return case without ruling" in {
      val someCase = oCase.btiCaseExample
      given(connector.findCase(refEq(caseRef))(any[HeaderCarrier])).willReturn(Future.successful(Some(someCase)))

      val caught = intercept[RuntimeException] {
        await(service.getCaseWithRulingForUser(traderEori, caseRef)(HeaderCarrier()))
      }
      caught.getMessage shouldBe "Case not found"
    }

    "propagate any error" in {
      val exception = new RuntimeException("Error")
      given(connector.findCase(refEq(caseRef))(any[HeaderCarrier])).willThrow(exception)

      val caught = intercept[RuntimeException] {
        await(service.getCaseForUser("eort", caseRef)(HeaderCarrier()))
      }
      caught shouldBe exception
    }
  }

}
