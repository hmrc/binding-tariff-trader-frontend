/*
 * Copyright 2024 HM Revenue & Customs
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

import audit.AuditPayloadType._
import models.{Application, Case}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.DefaultAuditConnector
import utils.JsonFormatters.caseAuditPayloadFormat

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AuditService @Inject() (auditConnector: DefaultAuditConnector)(implicit ec: ExecutionContext) {

  def auditBTIApplicationSubmissionSuccessful(c: Case)(implicit hc: HeaderCarrier): Unit = {
    val payload = CaseAuditPayload(caseReference = c.reference, application = c.application)
    auditConnector.sendExplicitAudit(auditType = BTIApplicationSubmission, detail = payload)
  }

}

case class CaseAuditPayload(
  caseReference: String,
  application: Application
)

object AuditPayloadType {

  val BTIApplicationSubmission = "bindingTariffApplicationSubmission"
}
