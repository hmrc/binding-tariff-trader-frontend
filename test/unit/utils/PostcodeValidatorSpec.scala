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

package unit.utils

import base.SpecBase
import utils.PostcodeValidator

class PostcodeValidatorSpec extends SpecBase {

  val validPostcodes = Seq(
    "AA11 1AA",
    "AA1A 1AA",
    "AA1 1AA",
    "A1A 1AA",
    "A1 1AA",
    "GIR 0AA",
    "BN21FG",
    "aa11 1aa",
    "aa11 1AA",
    "aa111AA",
    "aa111aa",
    " aa111aa",
    "aa111aa ",
    " aa111aa "
  )

  val invalidPostcodes = Seq(
    "123456"
  )

  "PostcodeValidator" when {

    invalidPostcodes.foreach { postcode =>
      s"validating invalid post code '$postcode' should be false" in {
        PostcodeValidator.validate(postcode) shouldBe false
      }
    }

    validPostcodes.foreach { postcode =>
      s"validating valid post code '$postcode' should be true" in {
        PostcodeValidator.validate(postcode) shouldBe true
      }
    }

  }
}
