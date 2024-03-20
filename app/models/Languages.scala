/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.i18n.Lang
import play.api.mvc.PathBindable

object Languages {

  sealed trait Language {
    val lang: Lang
  }

  case object Cymraeg extends WithName("cymraeg") with Language {
    override val lang: Lang = Lang("cy")
  }

  case object English extends WithName("english") with Language {
    override val lang: Lang = Lang("en")
  }

  implicit def pathBindable: PathBindable[Language] = new PathBindable[Language] {
    override def bind(key: String, value: String): Either[String, Language] =
      value match {
        case Cymraeg.toString => Right(Cymraeg)
        case English.toString => Right(English)
        case _                => Left("Invalid language")
      }

    override def unbind(key: String, value: Language): String =
      value.toString
  }
}
