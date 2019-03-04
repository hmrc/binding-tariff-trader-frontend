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
import javax.inject.{Inject, Singleton}
import models.{Case, NewCaseRequest}
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

@Singleton
class CasesService @Inject()(connector: BindingTariffClassificationConnector) {

  def create(c: NewCaseRequest)(implicit hc: HeaderCarrier): Future[Case] = {
    connector.createCase(c)
  }

  def getCaseForUser(userEori: String, reference: String)(implicit hc: HeaderCarrier): Future[Option[Case]] = {

    connector.findCase(reference).map {
      case Some(c) if caseBelongsToUser(c, userEori) => Some(c)
      case _ => None
    }
  }

  private def caseBelongsToUser(c: Case, eoriNumber: String): Boolean = {
    c.application.agent.map(agent => agent.eoriDetails.eori == eoriNumber)
      .getOrElse(c.application.holder.eori == eoriNumber)
  }
}
