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
import models.Email
import play.api.libs.json.{ Reads, Writes }
import scala.concurrent.{ ExecutionContext, Future }
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient

@Singleton
class EmailConnector @Inject()(
  configuration: FrontendAppConfig,
  client: HttpClient,
  val metrics: Metrics
)(implicit ec: ExecutionContext) extends HasMetrics {

  def send[E >: Email[_]](e: E)(implicit hc: HeaderCarrier, writes: Writes[E], rds: Reads[E]): Future[Unit] =
    withMetricsTimerAsync("email.send") { _ =>
      val url = s"${configuration.emailUrl}/hmrc/email"
      client.POST(url = url, body = e).map(_ => ())
    }
}
