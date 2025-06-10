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
import models.requests.FileStoreInitiateRequest
import models.response.{FileStoreInitiateResponse, FilestoreResponse}
import models.{Attachment, FileAttachment}
import org.apache.pekko.NotUsed
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import play.api.libs.json.Json
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.{DataPart, FilePart}
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.client.readStreamHttpResponse
import play.api.libs.ws.bodyWritableOf_Multipart

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BindingTariffFilestoreConnector @Inject() (
  httpClient: HttpClientV2,
  val metrics: MetricRegistry
)(implicit appConfig: FrontendAppConfig, ec: ExecutionContext)
    extends HasMetrics
    with InjectAuthHeader {

  private val env: String = appConfig.bindingTariffFileStoreUrl

  def get(file: FileAttachment)(implicit hc: HeaderCarrier): Future[FilestoreResponse] =
    withMetricsTimerAsync("get-file-by-id") { _ =>
      httpClient.get(url"$env/file/${file.id}").setHeader(authHeaders*).execute[FilestoreResponse]
    }

  def initiate(request: FileStoreInitiateRequest)(implicit hc: HeaderCarrier): Future[FileStoreInitiateResponse] =
    withMetricsTimerAsync("initiate-file-upload") { _ =>
      httpClient
        .post(url"$env/file/initiate")
        .withBody(Json.toJson(request))
        .setHeader(authHeaders*)
        .execute[FileStoreInitiateResponse]
    }

  def uploadApplicationPdf(reference: String, content: Array[Byte])(implicit
    hc: HeaderCarrier
  ): Future[FilestoreResponse] =
    withMetricsTimerAsync("upload-file") { _ =>
      val dataPart: MultipartFormData.DataPart = DataPart("publish", "true")
      val filePart: MultipartFormData.Part[Source[ByteString, NotUsed]] = FilePart(
        "file",
        s"ATaRApplication_$reference.pdf",
        Some("application/pdf"),
        Source.single(ByteString(content))
      )
      httpClient
        .post(url"$env/file")
        .withBody(Source(List(dataPart, filePart)))
        .setHeader(authHeaders*)
        .execute[FilestoreResponse]
    }

  def downloadFile(url: String)(implicit hc: HeaderCarrier): Future[Option[Source[ByteString, ?]]] =
    withMetricsTimerAsync("download-file") { _ =>
      httpClient.get(url"$url").setHeader(authHeaders*).stream[HttpResponse].flatMap { response =>
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
      httpClient.post(url"$env/file/${file.id}/publish").setHeader(authHeaders*).execute[FilestoreResponse]
    }

  def getFileMetadata(
    attachments: Seq[Attachment]
  )(implicit headerCarrier: HeaderCarrier): Future[Seq[FilestoreResponse]] =
    withMetricsTimerAsync("get-file-metadata") { _ =>
      if (attachments.isEmpty) {
        Future.successful(Seq.empty)
      } else {
        val query = s"?${attachments.map(att => s"id=${att.id}").mkString("&")}"
        val url   = s"$env/file$query"
        httpClient.get(url"$url").setHeader(authHeaders*).execute[Seq[FilestoreResponse]]
      }
    }

  def get(attachment: Attachment)(implicit headerCarrier: HeaderCarrier): Future[Option[FilestoreResponse]] =
    withMetricsTimerAsync("get-file-by-id") { _ =>
      httpClient.get(url"$env/file/${attachment.id}").setHeader(authHeaders*).execute[Option[FilestoreResponse]]
    }
}
