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

package connectors

import com.kenshoo.play.metrics.Metrics
import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import metrics.HasMetrics
import models.PdfFile
import play.api.Logging
import play.api.http.Status
import play.api.libs.ws.WSClient
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class PdfGeneratorServiceConnector @Inject() (
  configuration: FrontendAppConfig,
  ws: WSClient,
  val metrics: Metrics
)(implicit ec: ExecutionContext)
    extends HasMetrics
    with Logging {

  private lazy val url = s"${configuration.pdfGeneratorUrl}/pdf-generator-service/generate"

  def generatePdf(html: Html): Future[PdfFile] =
    withMetricsTimerAsync("generate-pdf") { timer =>
      val pdfResult = ws.url(url).post(Map("html" -> Seq(html.toString))).flatMap { response =>
        response.status match {
          case Status.OK =>
            Future.successful(PdfFile(content = response.bodyAsBytes.toArray))
          case _ =>
            Future.failed(new RuntimeException(s"Error calling pdf-generator-service - ${response.body}"))
        }
      }
      pdfResult.failed.foreach {
        case NonFatal(e) =>
          logger.error(s"pdf generator failed after ${timer.completeWithFailure()}", e)
        case _ =>
          logger.error("Unexpected error occurred during PDF generation.")
      }
      pdfResult
    }
}
