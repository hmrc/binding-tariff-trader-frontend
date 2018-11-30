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

package service

import connectors.BindingTariffFilestoreConnector
import javax.inject.{Inject, Singleton}
import models._
import models.response.FilestoreResponse
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class FileService @Inject()(connector: BindingTariffFilestoreConnector) {

  def upload(f: MultipartFormData.FilePart[TemporaryFile])(implicit hc: HeaderCarrier): Future[FileAttachment] = {
    connector.upload(f).map(toFileAttachment(f.ref.file.length))
  }

  def refresh(file: FileAttachment)(implicit headerCarrier: HeaderCarrier): Future[FileAttachment] = {
    connector.get(file).map(toFileAttachment(file.size))
  }

  def publish(file: FileAttachment)(implicit headerCarrier: HeaderCarrier): Future[SubmittedFileAttachment] = {
    connector.get(file).flatMap { r =>
      r.scanStatus match {
        case Some(ScanStatus.READY) => connector.publish(file).map(toPublishedAttachment(file.size))
        case Some(ScanStatus.FAILED) =>  Future.successful(UnpublishedFileAttachment(r.id, r.fileName, r.mimeType, file.size, "Quarantined"))
        case _ => Future.successful(UnpublishedFileAttachment(r.id, r.fileName, r.mimeType, file.size, "Unscanned"))
      }
    }
  }

  def publish(files: Seq[FileAttachment])(implicit headerCarrier: HeaderCarrier): Future[Seq[SubmittedFileAttachment]] = {
    Future.sequence(files.map(publish(_)))
  }

  private def toFileAttachment(size: Long): FilestoreResponse => FileAttachment = {
    r => FileAttachment(r.id, r.fileName, r.mimeType, size)
  }

  private def toPublishedAttachment(size: Long): FilestoreResponse => SubmittedFileAttachment = {
    r => PublishedFileAttachment(r.id, r.fileName, r.mimeType, size, r.url.get)
  }

}
