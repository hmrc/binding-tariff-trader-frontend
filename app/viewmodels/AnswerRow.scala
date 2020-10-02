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

package viewmodels

import org.apache.commons.lang3.StringUtils

case class AnswerRow(label: String, answer: String, answerIsMessageKey: Boolean, changeUrl: String, answerMap: Map[String, String] = Map.empty[String, String]) {
  def this(label: String, answer: Seq[String], answerIsMessageKey: Boolean, changeUrl: String, answerMap: Map[String, String])
  = this(label, answer.filter(StringUtils.isNoneBlank(_)).reduce((a, b) => s"$a\n$b"), answerIsMessageKey, changeUrl, answerMap)
}

object AnswerRow {
  def apply(label: String, answer: Seq[String], answerIsMessageKey: Boolean, changeUrl: String): AnswerRow
   = new AnswerRow(label: String, answer: Seq[String], answerIsMessageKey: Boolean, changeUrl: String, Map.empty[String, String])

  def apply(label: String, answerMap: Map[String, String], answerIsMessageKey: Boolean, changeUrl: String): AnswerRow
  = new AnswerRow(label: String, Nil, answerIsMessageKey: Boolean, changeUrl: String, answerMap: Map[String, String])
}
