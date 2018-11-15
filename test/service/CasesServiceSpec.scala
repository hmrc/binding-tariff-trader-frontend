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
import models.Case
import org.mockito.BDDMockito.given
import org.mockito.Matchers._
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class CasesServiceSpec extends UnitSpec with MockitoSugar {

  val c = mock[Case]
  val connector = mock[BindingTariffClassificationConnector]

  val service = new CasesService(connector)

  "Service 'Create Case'" should {

    "delegate to connector" in {
      given(connector.createCase(refEq(c))(any[HeaderCarrier])).willReturn(Future.successful(c))

      await(connector.createCase(c)(HeaderCarrier())) shouldBe c
    }
  }

}
