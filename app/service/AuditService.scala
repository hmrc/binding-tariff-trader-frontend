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

import javax.inject.{Inject, Singleton}
import models.{Case, NewCaseRequest}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.audit.DefaultAuditConnector
import utils.JsonFormatters.{caseFormat, newCaseRequestFormat}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuditService @Inject()(auditConnector: DefaultAuditConnector) {

  def auditBTIApplicationSubmission(req: NewCaseRequest)(implicit hc: HeaderCarrier): Unit = {
    sendExplicitAuditEvent(AuditPayloadType.BTIApplicationSubmission, Json.toJson(req))
  }

  def auditBTIApplicationSubmissionSuccessful(c: Case)(implicit hc: HeaderCarrier): Unit = {
    sendExplicitAuditEvent(AuditPayloadType.BTIApplicationSubmissionSuccessful, Json.toJson(c))
  }

  def auditBTIApplicationSubmissionFailed(req: NewCaseRequest)(implicit hc: HeaderCarrier): Unit = {
    sendExplicitAuditEvent(AuditPayloadType.BTIApplicationSubmissionFailed, Json.toJson(req))
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
