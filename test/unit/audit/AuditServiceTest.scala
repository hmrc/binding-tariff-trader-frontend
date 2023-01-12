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

package audit

import audit.AuditPayloadType.BTIApplicationSubmission
import base.SpecBase
import models._
import org.mockito.Mockito.verify
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.DefaultAuditConnector

import scala.concurrent.ExecutionContext.Implicits.global

class AuditServiceTest extends SpecBase {

  private implicit val hc: HeaderCarrier            = HeaderCarrier()
  private val auditConnector: DefaultAuditConnector = mock[DefaultAuditConnector]

  private val service = new AuditService(auditConnector)

  "BTI application submission auditing" should {

    val aCase     = oCase.btiCaseExample
    val auditJson = CaseAuditPayload(caseReference = aCase.reference, application = aCase.application)

    "call the audit connector as expected " in {

      import utils.JsonFormatters.caseAuditPayloadFormat

      service.auditBTIApplicationSubmissionSuccessful(aCase)

      verify(auditConnector).sendExplicitAudit(BTIApplicationSubmission, auditJson)(hc, global, caseAuditPayloadFormat)
    }
  }

}
