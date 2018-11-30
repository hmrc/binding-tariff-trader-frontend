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

package service

import connectors.BindingTariffClassificationConnector
import models.{Case, NewCaseRequest}
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

}
