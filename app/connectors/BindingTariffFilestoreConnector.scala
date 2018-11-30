/*
 * Copyright 2018 HM Revenue & Customs
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
import models.response.UploadFileResponse
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class BindingTariffFilestoreConnector @Inject()(configuration: FrontendAppConfig, ws: WSClient) {

  def upload(file: MultipartFormData.FilePart[TemporaryFile])
            (implicit hc: HeaderCarrier): Future[UploadFileResponse] = {
    val url = s"${configuration.bindingTariffFileStoreUrl}/binding-tariff-filestore/file"

    val filePart: MultipartFormData.Part[Source[ByteString, Future[IOResult]]] = FilePart(
      "file",
      file.filename,
      file.contentType,
      FileIO.fromPath(file.ref.file.toPath)
    )

    ws.url(url).post(Source(List(filePart)))
      .map(response => Json.fromJson[UploadFileResponse](Json.parse(response.body)).get)

  }

}
