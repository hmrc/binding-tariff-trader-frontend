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

import play.api.libs.json._
import uk.gov.hmrc.play.json.Union

sealed trait Email[T] {
  val to: Seq[String]
  val templateId: String
  val parameters: T // Must render to JSON as a Map[String, String]
  val force: Boolean = false
  val eventUrl: Option[String] = None
  val onSendUrl: Option[String] = None
}

object Email {
  implicit val format: Format[Email[_]] =
    Union.from[Email[_]]("templateId")
    .and[ApplicationSubmittedEmail](EmailType.APPLICATION_SUBMITTED.toString)
    .format
}

case class ApplicationSubmittedEmail
(
  override val to: Seq[String],
  override val parameters: ApplicationSubmittedParameters
) extends Email[ApplicationSubmittedParameters] {
  override val templateId: String = EmailType.APPLICATION_SUBMITTED.toString
}

object ApplicationSubmittedEmail {
  implicit val format: OFormat[ApplicationSubmittedEmail] = Json.format[ApplicationSubmittedEmail]
}

case class ApplicationSubmittedParameters
(
  recipientName_line1: String, // The full name of the recipient. Must match hrmc-email-renderer SalutationHelper param name.
  reference: String
)

object ApplicationSubmittedParameters {
  implicit val format: OFormat[ApplicationSubmittedParameters] = Json.format[ApplicationSubmittedParameters]
}


object EmailType extends Enumeration {
  type EmailType = Value
  val APPLICATION_SUBMITTED: EmailType.Value = Value("digital_tariffs_application_submitted")
  implicit val format: Format[EmailType.Value] = Format(Reads.enumNameReads(EmailType), Writes.enumNameWrites)
}