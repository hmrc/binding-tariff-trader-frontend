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

package service

import akka.stream.scaladsl.Source
import akka.util.ByteString
import config.FrontendAppConfig
import connectors.BindingTariffFilestoreConnector
import models._
import models.requests.FileStoreInitiateRequest
import models.response.{FileStoreInitiateResponse, FilestoreResponse}
import play.api.Logging
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.Files.TemporaryFile
import play.api.mvc.{MessagesControllerComponents, MultipartFormData}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.{sequence, successful}

@Singleton
class FileService @Inject() (
  connector: BindingTariffFilestoreConnector,
  cc: MessagesControllerComponents,
  appConfig: FrontendAppConfig
) extends Logging {

  private val messagesApi: MessagesApi = cc.messagesApi
  private implicit val lang: Lang      = Lang.defaultLang

  def initiate(request: FileStoreInitiateRequest)(implicit hc: HeaderCarrier): Future[FileStoreInitiateResponse] =
    connector.initiate(request)

  def uploadApplicationPdf(reference: String, content: Array[Byte])(
    implicit hc: HeaderCarrier
  ): Future[FileAttachment] =
    connector.uploadApplicationPdf(reference, content).map(toFileAttachment(content.length.toLong))

  def downloadFile(url: String)(implicit hc: HeaderCarrier): Future[Option[Source[ByteString, _]]] =
    connector.downloadFile(url)

  def refresh(file: FileAttachment)(implicit hc: HeaderCarrier): Future[FileAttachment] =
    connector.get(file).map(toFileAttachment(file.size))

  private def toFileAttachment(size: Long): FilestoreResponse => FileAttachment = { r =>
    FileAttachment(r.id, r.fileName, r.mimeType, size)
  }

  def getAttachmentMetadata(att: Attachment)(implicit hc: HeaderCarrier): Future[Option[FilestoreResponse]] =
    connector.getFileMetadata(Seq(att)).map(_.headOption)

  def getAttachmentMetadata(c: Case)(implicit hc: HeaderCarrier): Future[Seq[FilestoreResponse]] =
    connector.getFileMetadata(c.attachments)

  def getLetterOfAuthority(c: Case)(implicit hc: HeaderCarrier): Future[Option[FilestoreResponse]] =
    c.application.agent.flatMap(_.letterOfAuthorisation) match {
      case Some(attachment: Attachment) => connector.get(attachment)
      case _                            => successful(None)
    }

  def publish(files: Seq[FileAttachment])(implicit headerCarrier: HeaderCarrier): Future[Seq[PublishedFileAttachment]] =
    sequence(
      files.map { f: FileAttachment =>
        publish(f).map(Option(_)).recover {
          case t: Throwable =>
            logger.error(s"Failed to publish file [${f.id}].", t)
            None
        }
      }
    ).map(_.filter(_.isDefined).map(_.get))

  def publish(file: FileAttachment)(implicit hc: HeaderCarrier): Future[PublishedFileAttachment] =
    connector.publish(file).map(toPublishedAttachment(file.size))

  private def toPublishedAttachment(size: Long): FilestoreResponse => PublishedFileAttachment = { r =>
    PublishedFileAttachment(r.id, r.fileName, r.mimeType, size)
  }

  def validate(
    file: MultipartFormData.FilePart[TemporaryFile]
  ): Either[String, MultipartFormData.FilePart[TemporaryFile]] =
    if (hasInvalidSize(file)) {
      Left(messagesApi("uploadWrittenAuthorisation.error.size"))
    } else if (hasInvalidContentType(file)) {
      Left(messagesApi("uploadWrittenAuthorisation.error.fileType"))
    } else {
      Right(file)
    }

  private def hasInvalidSize: MultipartFormData.FilePart[TemporaryFile] => Boolean =
    _.ref.path.toFile.length > appConfig.fileUploadMaxSize

  private def hasInvalidContentType: MultipartFormData.FilePart[TemporaryFile] => Boolean = { f =>
    f.contentType match {
      case Some(c: String) if appConfig.fileUploadMimeTypes.contains(c) => false
      case _                                                            => true
    }
  }
}
