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

package views

import play.api.data.Form
import play.api.i18n.Messages

import scala.annotation.tailrec

object ViewUtils {

  def errorPrefix(form: Form[_])(implicit messages: Messages): String =
    if (form.hasErrors || form.hasGlobalErrors) messages("error.browser.title.prefix") else ""

  def humanReadableSize(bytes: Long): String = {

    val (baseValue, unitStrings) = (1024, Vector("B", "KB", "MB"))

    @tailrec
    def getExponent(curBytes: Long, baseValue: Int, curExponent: Int = 0): Int =
      if (curBytes < baseValue) {
        curExponent
      } else {
        val newExponent = 1 + curExponent
        getExponent(curBytes / (baseValue * newExponent), baseValue, newExponent)
      }

    val exponent   = getExponent(bytes, baseValue)
    val divisor    = Math.pow(baseValue, exponent)
    val unitString = unitStrings(exponent)

    f"${bytes / divisor}%.1f $unitString"
  }

  def confidentialityStatusLabel(confidential: Boolean)(implicit messages: Messages): String =
    if (confidential) messages("site.keep_confidential") else ""
}
