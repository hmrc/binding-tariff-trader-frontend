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

package models

import play.api.libs.json.Json

/*
  All sizes are stored as KiloBytes
 */

case class FileAttachment(id: String, name: String, mimeType: String, size: Long)

object FileAttachment {
  implicit val format = Json.format[FileAttachment]
}


case class PublishedFileAttachment
(
  override val id: String,
  override val name: String,
  override val mimeType: String,
  override val size: Long,
  url: String
) extends SubmittedFileAttachment

case class UnpublishedFileAttachment
(
  override val id: String,
  override val name: String,
  override val mimeType: String,
  override val size: Long,
  reason: String
) extends SubmittedFileAttachment

sealed trait SubmittedFileAttachment {
  val id: String
  val name: String
  val mimeType: String
  val size: Long
}
