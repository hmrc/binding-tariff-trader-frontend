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

import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import models.BinaryFile
import play.api.http.Status.OK
import play.api.libs.ws.WSClient
import play.twirl.api.Html

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PdfGeneratorServiceConnector @Inject()(configuration: FrontendAppConfig, ws: WSClient) {

  private lazy val url = s"${configuration.pdfGeneratorUrl}/pdf-generator-service/generate"

  def generatePdf(html: Html): Future[BinaryFile] = {
    ws.url(url).post(Map("html" -> Seq(html.toString))) map {
      response =>
        response.status match {
          case OK => BinaryFile(contentType = "application/pdf", content = response.bodyAsBytes.toArray)
          case _ => throw new Exception(s"Error calling PdfGeneratorService $response")
        }
    }
  }

}
