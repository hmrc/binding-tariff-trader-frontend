/*
 * Copyright 2025 HM Revenue & Customs
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

import com.codahale.metrics.MetricRegistry
import config.FrontendAppConfig
import metrics.HasMetrics
import models.CaseStatus.CaseStatus
import models._
import models.requests.NewEventRequest
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import utils.JsonFormatters._
import play.api.libs.ws.writeableOf_JsValue

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BindingTariffClassificationConnector @Inject() (
  httpClient: HttpClientV2,
  val metrics: MetricRegistry
)(implicit appConfig: FrontendAppConfig, ec: ExecutionContext)
    extends HasMetrics
    with InjectAuthHeader {

  private val env: String = appConfig.bindingTariffClassificationUrl

  def createCase(`case`: NewCaseRequest)(implicit hc: HeaderCarrier): Future[Case] =
    withMetricsTimerAsync("create-case") { _ =>
      httpClient.post(url"$env/cases").withBody(Json.toJson(`case`)).setHeader(authHeaders*).execute[Case]
    }

  def putCase(`case`: Case)(implicit hc: HeaderCarrier): Future[Case] =
    withMetricsTimerAsync("put-case") { _ =>
      httpClient
        .put(url"$env/cases/${`case`.reference}")
        .withBody(Json.toJson(`case`))
        .setHeader(authHeaders*)
        .execute[Case]
    }

  def updateCase(reference: String, update: CaseUpdate)(implicit hc: HeaderCarrier): Future[Option[Case]] =
    withMetricsTimerAsync("update-case") { _ =>
      httpClient
        .post(url"$env/cases/$reference")
        .withBody(Json.toJson(update))
        .setHeader(authHeaders*)
        .execute[Option[Case]]
    }

  def findCase(reference: String)(implicit hc: HeaderCarrier): Future[Option[Case]] =
    withMetricsTimerAsync("get-case-by-reference") { _ =>
      httpClient.get(url"$env/cases/$reference").setHeader(authHeaders*).execute[Option[Case]]
    }

  def allCases(pagination: Pagination, sort: Sort)(implicit hc: HeaderCarrier): Future[Paged[Case]] =
    withMetricsTimerAsync("all-cases") { _ =>
      val queryUrl = s"$env/cases" +
        s"?sort_by=${sort.field}&sort_direction=${sort.direction}" +
        s"&page=${pagination.page}&page_size=${pagination.pageSize}" +
        "&application_type=BTI&migrated=false"

      httpClient.get(url"$queryUrl").setHeader(authHeaders*).execute[Paged[Case]]
    }

  def findCasesBy(eori: String, status: Set[CaseStatus], pagination: Pagination, sort: Sort)(implicit
    hc: HeaderCarrier
  ): Future[Paged[Case]] =
    withMetricsTimerAsync("search-cases") { _ =>
      val queryUrl = s"$env/cases" +
        s"?eori=$eori&status=${status.mkString(",")}" +
        s"&sort_by=${sort.field}&sort_direction=${sort.direction}" +
        s"&page=${pagination.page}&page_size=${pagination.pageSize}" +
        "&migrated=false"

      httpClient.get(url"$queryUrl").setHeader(authHeaders*).execute[Paged[Case]]
    }

  def createEvent(`case`: Case, event: NewEventRequest)(implicit hc: HeaderCarrier): Future[Event] =
    withMetricsTimerAsync("create-event") { _ =>
      httpClient
        .post(url"$env/cases/${`case`.reference}/events")
        .withBody(Json.toJson(event))
        .setHeader(authHeaders*)
        .execute[Event]
    }

}
