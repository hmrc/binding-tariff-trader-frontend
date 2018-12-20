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

package audit

import models._
import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.Mockito.verify
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.{JsValue, Writes}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.audit.DefaultAuditConnector
import uk.gov.hmrc.play.test.UnitSpec
import utils.JsonFormatters

import scala.concurrent.ExecutionContext

class AuditServiceTest extends UnitSpec with MockitoSugar {

  private implicit val hc: HeaderCarrier = HeaderCarrier()
  private val writes: Writes[Case] = mock[Writes[Case]]
  private val connector: DefaultAuditConnector = mock[DefaultAuditConnector]

  private val service = new AuditService(connector)

  "Service 'audit application success'" should {
    val aCase = oCase.btiCaseExample
    val json: JsValue = JsonFormatters.caseFormat.writes(aCase)

    "Delegate to connector" in {
      service.auditBTIApplicationSubmissionSuccessful(aCase)

      verify(connector).sendExplicitAudit(
        refEq(AuditPayloadType.BTIApplicationSubmission.toString),
        refEq(json)
      )(any[HeaderCarrier], any[ExecutionContext], any[Writes[JsValue]])
    }
  }

}
