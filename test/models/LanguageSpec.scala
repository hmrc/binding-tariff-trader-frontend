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

import models.Languages._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class LanguageSpec extends AnyWordSpec with Matchers {

  private val binder = pathBindable

  "Languages" when {
    "pathBindable" must {
      "bind the valid string value cymraeg" in {
        binder.bind("language", "cymraeg") mustBe Right(Cymraeg)
      }

      "bind the valid string value english" in {
        binder.bind("language", "english") mustBe Right(English)
      }

      "fail to bind an unknown value" in {
        binder.bind("language", "dutch") mustBe Left("Invalid language")
      }

      "unbind English" in {
        binder.unbind("language", English) mustBe "english"
      }

      "unbind Cymraeg" in {
        binder.unbind("language", Cymraeg) mustBe "cymraeg"
      }
    }
  }
}
