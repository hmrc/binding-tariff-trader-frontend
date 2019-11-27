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

package controllers

import config.FrontendAppConfig
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.Files.TemporaryFile
import play.api.mvc.{MultipartFormData, Request}

class FileValidator @Inject()(appConfig: FrontendAppConfig, val messagesApi: MessagesApi) extends I18nSupport {
  def validateFile(file: MultipartFormData.FilePart[TemporaryFile], request: Request[_])
                  : Either[String, MultipartFormData.FilePart[TemporaryFile]] = {

    def hasInvalidSize: MultipartFormData.FilePart[TemporaryFile] => Boolean = {
      _.ref.path.toFile.length > appConfig.fileUploadMaxSize
    }

    def hasInvalidContentType: MultipartFormData.FilePart[TemporaryFile] => Boolean = { f =>
      f.contentType match {
        case Some(c: String) if appConfig.fileUploadMimeTypes.contains(c) => false
        case _ => true
      }
    }

    if (hasInvalidSize(file)) {
      Left(request.messages(messagesApi).apply("uploadWrittenAuthorisation.error.size"))
    } else if (hasInvalidContentType(file)) {
      Left(request.messages(messagesApi).apply("uploadWrittenAuthorisation.error.fileType"))
    } else {
      Right(file)
    }
  }
}
