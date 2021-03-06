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

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import com.kenshoo.play.metrics.Metrics
import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import metrics.HasMetrics
import models.response.FilestoreResponse
import models.{Attachment, FileAttachment}
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HeaderCarrier
import models.requests.FileStoreInitiateRequest
import models.response.FileStoreInitiateResponse
import play.api.mvc.MultipartFormData.DataPart
import play.api.libs.json.JsResult
import akka.NotUsed

@Singleton
class BindingTariffFilestoreConnector @Inject() (
  ws: WSClient,
  client: AuthenticatedHttpClient,
  val metrics: Metrics
)(implicit appConfig: FrontendAppConfig, ec: ExecutionContext)
    extends InjectAuthHeader
    with HasMetrics {

  def get(file: FileAttachment)(implicit hc: HeaderCarrier): Future[FilestoreResponse] =
    withMetricsTimerAsync("get-file-by-id") { _ =>
      client.GET[FilestoreResponse](s"${appConfig.bindingTariffFileStoreUrl}/file/${file.id}")(
        implicitly,
        addAuth,
        implicitly
      )
    }

  def initiate(request: FileStoreInitiateRequest)(implicit hc: HeaderCarrier): Future[FileStoreInitiateResponse] =
    withMetricsTimerAsync("initiate-file-upload") { _ =>
      client.POST[FileStoreInitiateRequest, FileStoreInitiateResponse](
        s"${appConfig.bindingTariffFileStoreUrl}/file/initiate",
        request
      )(implicitly, implicitly, addAuth, implicitly)
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
      ws.url(s"${appConfig.bindingTariffFileStoreUrl}/file")
        .addHttpHeaders(hc.headers: _*)
        .addHttpHeaders(authHeaders(appConfig.apiToken))
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
        .withHttpHeaders(hc.headers: _*)
        .withHttpHeaders(authHeaders(appConfig.apiToken))
        .get()

      fileStoreResponse.flatMap { response =>
        if (response.status / 100 == 2)
          Future.successful(Some(response.bodyAsSource))
        else if (response.status / 100 > 4)
          Future.failed(new RuntimeException(s"Unable to retrieve file $url from filestore"))
        else
          Future.successful(None)
      }
    }

  def publish(file: FileAttachment)(implicit hc: HeaderCarrier): Future[FilestoreResponse] =
    withMetricsTimerAsync("publish-file") { _ =>
      client.POSTEmpty[FilestoreResponse](s"${appConfig.bindingTariffFileStoreUrl}/file/${file.id}/publish")(
        implicitly,
        addAuth,
        implicitly
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
        client.GET[Seq[FilestoreResponse]](url)(implicitly, addAuth, implicitly)
      }
    }

  def get(attachment: Attachment)(implicit headerCarrier: HeaderCarrier): Future[Option[FilestoreResponse]] =
    withMetricsTimerAsync("get-file-by-id") { _ =>
      client.GET[Option[FilestoreResponse]](s"${appConfig.bindingTariffFileStoreUrl}/file/${attachment.id}")(
        implicitly,
        addAuth,
        implicitly
      )
    }
}
