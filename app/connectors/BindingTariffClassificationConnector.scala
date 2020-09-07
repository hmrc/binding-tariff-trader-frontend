/*
 * Copyright 2020 HM Revenue & Customs
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

package connectors

import com.google.inject.Inject
import com.kenshoo.play.metrics.Metrics
import config.FrontendAppConfig
import javax.inject.Singleton
import metrics.HasMetrics
import models.CaseStatus.CaseStatus
import models._
import models.requests.NewEventRequest
import scala.concurrent.{ ExecutionContext, Future }
import uk.gov.hmrc.http.HeaderCarrier
import utils.JsonFormatters._

@Singleton
class BindingTariffClassificationConnector @Inject()(
  client: AuthenticatedHttpClient,
  val metrics: Metrics
)(implicit configuration: FrontendAppConfig, ec: ExecutionContext) extends InjectAuthHeader with HasMetrics {

  def createCase(c: NewCaseRequest)(implicit hc: HeaderCarrier): Future[Case] =
    withMetricsTimerAsync("binding-tariff-classification.createCase") { _ =>
      val url = s"${configuration.bindingTariffClassificationUrl}/cases"
      client.POST[NewCaseRequest, Case](url = url, body = c)(implicitly, implicitly, addAuth, implicitly)
    }

  def findCase(reference: String)(implicit hc: HeaderCarrier): Future[Option[Case]] =
    withMetricsTimerAsync("binding-tariff-classification.findCase") { _ =>
      val url = s"${configuration.bindingTariffClassificationUrl}/cases/$reference"
      client.GET[Option[Case]](url)(implicitly, addAuth, implicitly)
    }

  def findCasesBy(eori: String, status: Set[CaseStatus], pagination: Pagination, sort: Sort)(implicit hc: HeaderCarrier): Future[Paged[Case]] =
    withMetricsTimerAsync("binding-tariff-classification.findCasesBy") { _ =>
      val url = s"${configuration.bindingTariffClassificationUrl}/cases" +
        s"?eori=$eori&status=${status.mkString(",")}" +
        s"&sort_by=${sort.field}&sort_direction=${sort.direction}" +
        s"&page=${pagination.page}&page_size=${pagination.pageSize}" +
        "&migrated=false"

      client.GET[Paged[Case]](url)(implicitly, addAuth, implicitly)
    }

  def createEvent(c: Case, e: NewEventRequest)(implicit hc: HeaderCarrier): Future[Event] =
    withMetricsTimerAsync("binding-tariff-classification.createEvent") { _ =>
      val url = s"${configuration.bindingTariffClassificationUrl}/cases/${c.reference}/events"
      client.POST[NewEventRequest, Event](url = url, body = e)(implicitly, implicitly, addAuth, implicitly)
    }

}
