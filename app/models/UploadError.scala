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

package models

sealed abstract class UploadError(
  val errorCode: String,
  val errorMessageKey: String
) extends Product
    with Serializable

case object FileTooSmall extends UploadError("EntityTooSmall", "uploadSupportingMaterialMultiple.error.fileTooSmall")

case object FileTooLarge extends UploadError("EntityTooLarge", "uploadSupportingMaterialMultiple.error.fileTooLarge")

// This is extremely sketchy, but this is the error code that the Upscan stub returns now for no file selected
case object NoFileSelected extends UploadError("400", "uploadSupportingMaterialMultiple.error.noFileSelected")

case class Other(
  override val errorCode: String
) extends UploadError(errorCode, "uploadSupportingMaterialMultiple.error.uploadError")

object UploadError {

  private val knownErrors: Set[UploadError] = Set(FileTooSmall, FileTooLarge, NoFileSelected)

  def fromErrorCode(errorCode: String): UploadError =
    knownErrors.find(_.errorCode == errorCode).getOrElse(Other(errorCode))
}
