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

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.kenshoo.play.metrics.Metrics
import config.FrontendAppConfig
import metrics.HasMetrics
import models.requests.FileStoreInitiateRequest
import models.response.{FileStoreInitiateResponse, FilestoreResponse}
import models.{Attachment, FileAttachment}
import play.api.libs.json.{JsResult, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.{DataPart, FilePart}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BindingTariffFilestoreConnector @Inject() (
  ws: WSClient,
  client: AuthenticatedHttpClient,
  val metrics: Metrics
)(implicit appConfig: FrontendAppConfig, ec: ExecutionContext)
    extends InjectAuthHeader
    with HasMetrics {

  private val hcConfig = HeaderCarrier.Config.fromConfig(client.configuration)

  def get(file: FileAttachment)(implicit hc: HeaderCarrier): Future[FilestoreResponse] =
    withMetricsTimerAsync("get-file-by-id") { _ =>
      client
        .GET[FilestoreResponse](s"${appConfig.bindingTariffFileStoreUrl}/file/${file.id}", headers = addAuth(appConfig))
    }

  def initiate(request: FileStoreInitiateRequest)(implicit hc: HeaderCarrier): Future[FileStoreInitiateResponse] =
    withMetricsTimerAsync("initiate-file-upload") { _ =>
      client.POST[FileStoreInitiateRequest, FileStoreInitiateResponse](
        s"${appConfig.bindingTariffFileStoreUrl}/file/initiate",
        request,
        headers = addAuth(appConfig)
      )
    }

  def uploadApplicationPdf(reference: String, content: Array[Byte])(
    implicit hc: HeaderCarrier
  ): Future[FilestoreResponse] =
    withMetricsTimerAsync("upload-file") { _ =>
      val dataPart: MultipartFormData.DataPart = DataPart("publish", "true")
      val filePart: MultipartFormData.Part[Source[ByteString, NotUsed]] = FilePart(
        "file",
        s"ATaRApplication_$reference.pdf",
        Some("application/pdf"),
        Source.single(ByteString(content))
      )
      val url = s"${appConfig.bindingTariffFileStoreUrl}/file"
      ws.url(url)
        .addHttpHeaders(hc.withExtraHeaders(addAuth(appConfig): _*).headersForUrl(hcConfig)(url): _*)
        .post(Source(List(dataPart, filePart)))
        .flatMap { response =>
          Future.fromTry {
            JsResult.toTry(Json.fromJson[FilestoreResponse](Json.parse(response.body)))
          }
        }
    }

  def downloadFile(url: String)(implicit hc: HeaderCarrier): Future[Option[Source[ByteString, _]]] =
    withMetricsTimerAsync("download-file") { _ =>
      val fileStoreResponse = ws
        .url(url)
        .withHttpHeaders(hc.withExtraHeaders(addAuth(appConfig): _*).headersForUrl(hcConfig)(url): _*)
        .get()

      fileStoreResponse.flatMap { response =>
        if (response.status / 100 == 2) {
          Future.successful(Some(response.bodyAsSource))
        } else if (response.status / 100 > 4) {
          Future.failed(new RuntimeException("Unable to retrieve file from filestore"))
        } else {
          Future.successful(None)
        }
      }
    }

  def publish(file: FileAttachment)(implicit hc: HeaderCarrier): Future[FilestoreResponse] =
    withMetricsTimerAsync("publish-file") { _ =>
      client.POSTEmpty[FilestoreResponse](
        s"${appConfig.bindingTariffFileStoreUrl}/file/${file.id}/publish",
        headers = addAuth(appConfig)
      )
    }

  def getFileMetadata(
    attachments: Seq[Attachment]
  )(implicit headerCarrier: HeaderCarrier): Future[Seq[FilestoreResponse]] =
    withMetricsTimerAsync("get-file-metadata") { _ =>
      if (attachments.isEmpty) {
        Future.successful(Seq.empty)
      } else {
        val query = s"?${attachments.map(att => s"id=${att.id}").mkString("&")}"
        val url   = s"${appConfig.bindingTariffFileStoreUrl}/file$query"
        client.GET[Seq[FilestoreResponse]](url, headers = addAuth(appConfig))
      }
    }

  def get(attachment: Attachment)(implicit headerCarrier: HeaderCarrier): Future[Option[FilestoreResponse]] =
    withMetricsTimerAsync("get-file-by-id") { _ =>
      client.GET[Option[FilestoreResponse]](
        s"${appConfig.bindingTariffFileStoreUrl}/file/${attachment.id}",
        headers = addAuth(appConfig)
      )
    }
}
