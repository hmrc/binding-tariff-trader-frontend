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

import javax.inject.{Inject, Singleton}
import audit.AuditPayloadType._
import audit.model.CaseRequestAuditPayload
import models.{Case, NewCaseRequest}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.audit.DefaultAuditConnector
import utils.JsonFormatters.{caseFormat, newCaseRequestAuditPayload}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuditService @Inject()(auditConnector: DefaultAuditConnector) {

  def auditBTIApplicationSubmission(req: NewCaseRequest)
                                   (implicit hc: HeaderCarrier): Unit = {
    sendExplicitAuditEvent(auditEventType = BTIApplicationSubmission, toJson(CaseRequestAuditPayload(req)))
  }

  def auditBTIApplicationSubmissionFailed(req: NewCaseRequest, error: String)
                                         (implicit hc: HeaderCarrier): Unit = {
    sendExplicitAuditEvent(BTIApplicationSubmissionFailed, toJson(CaseRequestAuditPayload(req, Some(error))))
  }

  def auditBTIApplicationSubmissionSuccessful(c: Case)
                                             (implicit hc: HeaderCarrier): Unit = {
    sendExplicitAuditEvent(BTIApplicationSubmissionSuccessful, toJson(c))
  }

  private def sendExplicitAuditEvent(auditEventType: String, auditPayload: JsValue)
                                    (implicit hc : uk.gov.hmrc.http.HeaderCarrier): Unit = {
    auditConnector.sendExplicitAudit(auditType = auditEventType, detail = auditPayload)
  }

}

object AuditPayloadType {

  val BTIApplicationSubmission = "BindingTariffApplication"
  val BTIApplicationSubmissionSuccessful = "BindingTariffApplicationSubmissionSuccessful"
  val BTIApplicationSubmissionFailed = "BindingTariffApplicationSubmissionFailed"
}
