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

package connectors

import com.codahale.metrics.MetricRegistry
import config.FrontendAppConfig
import metrics.HasMetrics
import models.Email
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailConnector @Inject() (
  configuration: FrontendAppConfig,
  httpClient: HttpClientV2,
  val metrics: MetricRegistry
)(implicit ec: ExecutionContext)
    extends HasMetrics {

  def send[E >: Email[_]](e: E)(implicit hc: HeaderCarrier, writes: Writes[E]): Future[Unit] =
    withMetricsTimerAsync("send-email") { _ =>
      httpClient
        .post(url"${configuration.emailUrl}/hmrc/email")
        .withBody(Json.toJson(e))
        .execute[HttpResponse](throwOnFailure(readEitherOf(readRaw)), ec)
        .map(_ => ())
    }
}
