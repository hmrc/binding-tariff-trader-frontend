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

package connectors

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import models.response.FilestoreResponse
import models.{Attachment, FileAttachment}
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class BindingTariffFilestoreConnector @Inject()(appConfig: FrontendAppConfig, ws: WSClient, http: AuthenticatedHttpClient) {

  def upload(file: MultipartFormData.FilePart[TemporaryFile])
            (implicit hc: HeaderCarrier): Future[FilestoreResponse] = {

    val filePart: MultipartFormData.Part[Source[ByteString, Future[IOResult]]] = FilePart(
      "file",
      file.filename,
      file.contentType,
      FileIO.fromPath(file.ref.path)
    )

    ws.url(s"${appConfig.bindingTariffFileStoreUrl}/file")
      .withHttpHeaders(hc.headers: _*)
      .withHttpHeaders(http.authHeaders(appConfig))
      .post(Source(List(filePart)))
      .map(response => Json.fromJson[FilestoreResponse](Json.parse(response.body)).get)

  }

  def get(file: FileAttachment)(implicit hc: HeaderCarrier): Future[FilestoreResponse] = {
    http.GET[FilestoreResponse](s"${appConfig.bindingTariffFileStoreUrl}/file/${file.id}")
  }

  def publish(file: FileAttachment)(implicit hc: HeaderCarrier): Future[FilestoreResponse] = {
    http.POSTEmpty[FilestoreResponse](s"${appConfig.bindingTariffFileStoreUrl}/file/${file.id}/publish")
  }

  def getFileMetadata(attachments: Seq[Attachment])(implicit headerCarrier: HeaderCarrier): Future[Seq[FilestoreResponse]] = {
    if (attachments.isEmpty) {
      Future.successful(Seq.empty)
    } else {
      val query = s"?${attachments.map(att => s"id=${att.id}").mkString("&")}"
      val url = s"${appConfig.bindingTariffFileStoreUrl}/file$query"
      http.GET[Seq[FilestoreResponse]](url)
    }
  }

  def get(attachment: Attachment)(implicit headerCarrier: HeaderCarrier): Future[Option[FilestoreResponse]] = {
    http.GET[Option[FilestoreResponse]](s"${appConfig.bindingTariffFileStoreUrl}/file/${attachment.id}")
  }

}
