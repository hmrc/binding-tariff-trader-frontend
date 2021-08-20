/*
 * Copyright 2021 HM Revenue & Customs
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

package utils

import play.api.i18n.Messages

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

object Dates {

  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

  val pdfDateformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  def format(instant: Instant)(implicit messages: Messages) : String = {
    if(messages.lang.language == "cy") {
      val date = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
      val monthNum = date.getMonthValue
      val welshFormatter = DateTimeFormatter.ofPattern(s"""dd '${messages(s"site.month.$monthNum")}' YYYY""")
      date.format(welshFormatter)
    } else {
      formatter.format(LocalDateTime.ofInstant(instant, ZoneOffset.UTC))
    }
  }

  def format(instant: Option[Instant])(implicit messages: Messages) : String = {
    instant.map(format).getOrElse("None")
  }

  def formatForPdf(instant: Instant) : String = {
      pdfDateformatter.format(LocalDateTime.ofInstant(instant, ZoneOffset.UTC))
  }

}
