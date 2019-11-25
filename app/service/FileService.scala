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

package service

import config.FrontendAppConfig
import connectors.BindingTariffFilestoreConnector
import javax.inject.{Inject, Singleton}
import models._
import models.response.FilestoreResponse
import play.api.Logger
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.{sequence, successful}

@Singleton
class FileService @Inject()(connector: BindingTariffFilestoreConnector, messagesApi: MessagesApi, configuration: FrontendAppConfig){

  def upload(f: MultipartFormData.FilePart[TemporaryFile])(implicit hc: HeaderCarrier): Future[FileAttachment] = {
    connector.upload(f).map(toFileAttachment(f.ref.path.toFile.length))
  }

  private def toFileAttachment(size: Long): FilestoreResponse => FileAttachment = {
    r => FileAttachment(r.id, r.fileName, r.mimeType, size)
  }

  def refresh(file: FileAttachment)(implicit hc: HeaderCarrier): Future[FileAttachment] = {
    connector.get(file).map(toFileAttachment(file.size))
  }

  def getAttachmentMetadata(c: Case)(implicit hc: HeaderCarrier): Future[Seq[FilestoreResponse]] = {
    connector.getFileMetadata(c.attachments)
  }

  def getLetterOfAuthority(c: Case)(implicit hc: HeaderCarrier): Future[Option[FilestoreResponse]] = {
    c.application.agent.flatMap(_.letterOfAuthorisation) match {
      case Some(attachment: Attachment) => connector.get(attachment)
      case _ => successful(None)
    }
  }

  def publish(files: Seq[FileAttachment])(implicit headerCarrier: HeaderCarrier): Future[Seq[PublishedFileAttachment]] = {
    sequence(
      files.map { f: FileAttachment =>
        publish(f).map(Option(_)).recover {
          case t: Throwable =>
            Logger.error(s"Failed to publish file [${f.id}].", t)
            None
        }
      }
    ).map(_.filter(_.isDefined).map(_.get))
  }

  def publish(file: FileAttachment)(implicit hc: HeaderCarrier): Future[PublishedFileAttachment] = {
    connector.publish(file).map(toPublishedAttachment(file.size))
  }

  private def toPublishedAttachment(size: Long): FilestoreResponse => PublishedFileAttachment = {
    r => PublishedFileAttachment(r.id, r.fileName, r.mimeType, size)
  }


}
