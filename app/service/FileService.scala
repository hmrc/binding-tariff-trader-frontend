/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.i18n.Lang
import play.api.libs.Files.TemporaryFile
import play.api.mvc.{MessagesControllerComponents, MultipartFormData}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.{sequence, successful}
import models.requests.FileStoreInitiateRequest
import models.response.FileStoreInitiateResponse
import play.api.libs.json.JsValue

@Singleton
class FileService @Inject()(
                             connector: BindingTariffFilestoreConnector,
                             cc: MessagesControllerComponents,
                             appConfig: FrontendAppConfig
                           ) {

  private val messagesApi = cc.messagesApi
  private implicit val lang = Lang.defaultLang

  def initiate(request: FileStoreInitiateRequest)(implicit hc: HeaderCarrier): Future[FileStoreInitiateResponse] = {
    connector.initiate(request)
  }

  def refresh(file: FileAttachment)(implicit hc: HeaderCarrier): Future[FileAttachment] = {
    connector.get(file).map(toFileAttachment(file.size))
  }

  private def toFileAttachment(size: Long): FilestoreResponse => FileAttachment = {
    r => FileAttachment(r.id, r.fileName, r.mimeType, size)
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

  def validate(file: MultipartFormData.FilePart[TemporaryFile]): Either[String, MultipartFormData.FilePart[TemporaryFile]] = {
    if (hasInvalidSize(file)) {
      Left(messagesApi("uploadWrittenAuthorisation.error.size"))
    } else if (hasInvalidContentType(file)) {
      Left(messagesApi("uploadWrittenAuthorisation.error.fileType"))
    } else {
      Right(file)
    }
  }

  private def hasInvalidSize: MultipartFormData.FilePart[TemporaryFile] => Boolean = {
    _.ref.path.toFile.length > appConfig.fileUploadMaxSize
  }

  private def hasInvalidContentType: MultipartFormData.FilePart[TemporaryFile] => Boolean = { f =>
    f.contentType match {
      case Some(c: String) if appConfig.fileUploadMimeTypes.contains(c) => false
      case _ => true
    }
  }

}
