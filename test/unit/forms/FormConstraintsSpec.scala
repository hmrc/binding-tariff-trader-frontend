/*
 * Copyright 2022 HM Revenue & Customs
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

package unit.forms

import base.SpecBase
import forms.FormConstraints
import play.api.data.validation.{Invalid, Valid}

class FormConstraintsSpec extends SpecBase {

  "FormConstraints" when {
    "eoriCodeConstraint" should {
      "return Valid for an empty string" in {
        FormConstraints.eoriCodeConstraint("") shouldBe Valid
      }

      "return Valid for a non empty string that matches the regex" in {
        FormConstraints.eoriCodeConstraint("run") shouldBe Valid
      }

      "return Invalid with an error message for a non empty string that does not match the regex" in {
        FormConstraints.eoriCodeConstraint(";") shouldBe Invalid("registerBusinessRepresenting.error.eoriNumber.format")
      }
    }
  }
}
