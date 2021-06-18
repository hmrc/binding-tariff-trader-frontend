/*
 * Copyright 2021 HM Revenue & Customs
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
import metrics.HasMetrics
import models.CaseStatus.CaseStatus
import models._
import models.requests.NewEventRequest
import uk.gov.hmrc.http.HeaderCarrier
import utils.JsonFormatters._

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HttpReads.Implicits._

@Singleton
class BindingTariffClassificationConnector @Inject()(
                                                      client: AuthenticatedHttpClient,
                                                      val metrics: Metrics
                                                    )(implicit appConfig: FrontendAppConfig, ec: ExecutionContext) extends InjectAuthHeader with HasMetrics {

  def createCase(c: NewCaseRequest)(implicit hc: HeaderCarrier): Future[Case] =
    withMetricsTimerAsync("create-case") { _ =>
      val url = s"${appConfig.bindingTariffClassificationUrl}/cases"
      client.POST[NewCaseRequest, Case](url = url, body = c, headers = addAuth(appConfig))
    }

  def putCase(c: Case)(implicit hc: HeaderCarrier): Future[Case] =
    withMetricsTimerAsync("put-case") { _ =>
      val url = s"${appConfig.bindingTariffClassificationUrl}/cases/${c.reference}"
      client.PUT[Case, Case](url = url, body = c, headers = addAuth(appConfig))
    }

  def updateCase(reference: String, update: CaseUpdate)(implicit hc: HeaderCarrier): Future[Option[Case]] =
    withMetricsTimerAsync("update-case") { _ =>
      val url = s"${appConfig.bindingTariffClassificationUrl}/cases/$reference"
      client.POST[CaseUpdate, Option[Case]](url, update, headers = addAuth(appConfig))
    }

  def findCase(reference: String)(implicit hc: HeaderCarrier): Future[Option[Case]] =
    withMetricsTimerAsync("get-case-by-reference") { _ =>
      val url = s"${appConfig.bindingTariffClassificationUrl}/cases/$reference"
      client.GET[Option[Case]](url, headers = addAuth(appConfig))
    }

  def allCases(pagination: Pagination, sort: Sort)(implicit hc: HeaderCarrier): Future[Paged[Case]] =
    withMetricsTimerAsync("all-cases") { _ =>
      val url = s"${appConfig.bindingTariffClassificationUrl}/cases" +
        s"?sort_by=${sort.field}&sort_direction=${sort.direction}" +
        s"&page=${pagination.page}&page_size=${pagination.pageSize}" +
        "&application_type=BTI&migrated=false"

      client.GET[Paged[Case]](url, headers = addAuth(appConfig))
    }

  def findCasesBy(eori: String, status: Set[CaseStatus], pagination: Pagination, sort: Sort)(implicit hc: HeaderCarrier): Future[Paged[Case]] =
    withMetricsTimerAsync("search-cases") { _ =>
      val url = s"${appConfig.bindingTariffClassificationUrl}/cases" +
        s"?eori=$eori&status=${status.mkString(",")}" +
        s"&sort_by=${sort.field}&sort_direction=${sort.direction}" +
        s"&page=${pagination.page}&page_size=${pagination.pageSize}" +
        "&migrated=false"

      client.GET[Paged[Case]](url, headers = addAuth(appConfig))
    }

  def createEvent(c: Case, e: NewEventRequest)(implicit hc: HeaderCarrier): Future[Event] =
    withMetricsTimerAsync("create-event") { _ =>
      val url = s"${appConfig.bindingTariffClassificationUrl}/cases/${c.reference}/events"
      client.POST[NewEventRequest, Event](url = url, body = e, headers = addAuth(appConfig))
    }

}
