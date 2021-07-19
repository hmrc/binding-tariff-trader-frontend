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

import scala.language.implicitConversions

sealed abstract class NotificationType(val key: String)

object NotificationType extends Enumeration {
  object Success extends NotificationType("success")
}

object Notification {
  def success(key: String, args: Any*)(implicit messages: Messages): (NotificationType, String) =
    NotificationType.Success -> messages(key, args: _*)

  implicit def toFlash(value: (NotificationType, String)): (String, String) = (value._1.key, value._2)

}
