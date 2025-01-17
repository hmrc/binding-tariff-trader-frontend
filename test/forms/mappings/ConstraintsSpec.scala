/*
 * Copyright 2025 HM Revenue & Customs
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

package forms.mappings

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.data.validation.{Invalid, Valid}

class ConstraintsSpec extends AnyWordSpec with Matchers with Constraints {

  private val maximumValue: Int = 10
  private val minimumValue: Int = 10

  "firstError" must {

    "return Valid when all constraints pass" in {
      val result = firstError(maxLength(maximumValue, "error.length"), regexp("""^\w+$""", "error.regexp"))("foo")
      result mustEqual Valid
    }

    "return Invalid when the first constraint fails" in {
      val result = firstError(maxLength(maximumValue, "error.length"), regexp("""^\w+$""", "error.regexp"))("a" * 11)
      result mustEqual Invalid("error.length", maximumValue)
    }

    "return Invalid when the second constraint fails" in {
      val result = firstError(maxLength(maximumValue, "error.length"), regexp("""^\w+$""", "error.regexp"))("")
      result mustEqual Invalid("error.regexp")
    }

    "return Invalid for the first error when both constraints fail" in {
      val result = firstError(maxLength(-1, "error.length"), regexp("""^\w+$""", "error.regexp"))("")
      result mustEqual Invalid("error.length", -1)
    }
  }

  "minimumValue" must {

    "return Valid for a number greater than the threshold" in {
      val result = minimumValue(1, "error.min").apply(2)
      result mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      val result = minimumValue(1, "error.min").apply(1)
      result mustEqual Valid
    }

    "return Invalid for a number below the threshold" in {
      val result = minimumValue(1, "error.min").apply(0)
      result mustEqual Invalid("error.min", 1)
    }
  }

  "maximumValue" must {

    "return Valid for a number less than the threshold" in {
      val result = maximumValue(1, "error.max").apply(0)
      result mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      val result = maximumValue(1, "error.max").apply(1)
      result mustEqual Valid
    }

    "return Invalid for a number above the threshold" in {
      val result = maximumValue(1, "error.max").apply(2)
      result mustEqual Invalid("error.max", 1)
    }
  }

  "inRange" must {

    "return Valid for a number that is in the range" in {
      val result = inRange(1, maximumValue, "error.range").apply(2)
      result mustEqual Valid
    }

    "return Valid for a number is equal to the min range" in {
      val result = inRange(1, maximumValue, "error.range").apply(1)
      result mustEqual Valid
    }

    "return Valid for a number is equal to the max range" in {
      val result = inRange(1, maximumValue, "error.range").apply(maximumValue)
      result mustEqual Valid
    }

    "return Invalid for a number out of range" in {
      val result = inRange(1, maximumValue, "error.range").apply(maximumValue + 1)
      result mustEqual Invalid("error.range", 1, maximumValue)
    }
  }

  "regexp" must {

    "return Valid for an input that matches the expression" in {
      val result = regexp("""^\w+$""", "error.invalid")("foo")
      result mustEqual Valid
    }

    "return Invalid for an input that does not match the expression" in {
      val result = regexp("""^\d+$""", "error.invalid")("foo")
      result mustEqual Invalid("error.invalid")
    }
  }

  "minLength" must {

    "return Invalid for a string shorter than the allowed length" in {
      val result = minLength(minimumValue, "error.length")("a" * 9)
      result mustEqual Invalid("error.length", minimumValue)
    }

    "return Invalid for an empty string" in {
      val result = minLength(minimumValue, "error.length")("")
      result mustEqual Invalid("error.length", minimumValue)
    }

    "return Valid for a string equal to the allowed length" in {
      val result = minLength(minimumValue, "error.length")("a" * 10)
      result mustEqual Valid
    }

    "return Valid for a string longer than the allowed length" in {
      val result = minLength(minimumValue, "error.length")("a" * 11)
      result mustEqual Valid
    }
  }

  "maxLength" must {

    "return Valid for a string shorter than the allowed length" in {
      val result = maxLength(maximumValue, "error.length")("a" * 9)
      result mustEqual Valid
    }

    "return Valid for an empty string" in {
      val result = maxLength(maximumValue, "error.length")("")
      result mustEqual Valid
    }

    "return Valid for a string equal to the allowed length" in {
      val result = maxLength(maximumValue, "error.length")("a" * 10)
      result mustEqual Valid
    }

    "return Invalid for a string longer than the allowed length" in {
      val result = maxLength(maximumValue, "error.length")("a" * 11)
      result mustEqual Invalid("error.length", maximumValue)
    }
  }

  "optionalMaxLength" must {

    "return Valid for no input string" in {
      val result = optionalMaxLength(maximumValue, "error.length")(None)
      result mustEqual Valid
    }
  }
}
