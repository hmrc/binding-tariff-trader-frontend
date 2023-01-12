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

package forms.behaviours

import org.scalactic.anyvals.PosInt
import play.api.data.{Form, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

trait StringFieldBehaviours extends FieldBehaviours {

  private val minimumValue: PosInt = 100

  def fieldWithMaxLength(form: Form[_], fieldName: String, maxLength: Int, lengthError: FormError): Unit =
    s"not bind strings longer than $maxLength characters" in {

      forAll(stringsLongerThan(maxLength) -> "longString") { str: String =>
        val result = form.bind(Map(fieldName -> str)).apply(fieldName)
        result.errors should contain(lengthError)
      }
    }

  def fieldWithMinLength(form: Form[_], fieldName: String, minLength: Int, lengthError: FormError): Unit =
    s"not bind strings shorter than $minLength characters" in {

      forAll(stringsShorterThan(minLength) -> "shortString") { str: String =>
        val result = form.bind(Map(fieldName -> str)).apply(fieldName)
        result.errors should contain(lengthError)
      }
    }

  def fieldWithRegex(
    form: Form[_],
    fieldName: String,
    invalidString: String,
    maxLength: Int,
    error: FormError,
    regex: String
  ): Unit = {

    "not bind strings invalidated by regex" in {
      val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
      result.errors should contain(error)
    }

    "bind strings that pass regex" in {
      val gen = RegexpGen.from(regex)
      forAll(gen, minSuccessful(minimumValue)) { str: String =>
        whenever(str.length <= maxLength) {
          val result = form.bind(Map(fieldName -> str)).apply(fieldName)
          result.errors should be(empty)
        }
      }
    }
  }

  def fieldWithRegexAndMinTelLength(
    form: Form[_],
    fieldName: String,
    invalidString: String,
    minLength: Int,
    maxLength: Int,
    error: FormError,
    regex: String
  ): Unit = {

    "not bind strings invalidated by regex" in {
      val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
      result.errors should contain(error)
    }

    "bind strings that pass regex" in {
      val gen = RegexpGen.from(regex)
      forAll(gen, minSuccessful(minimumValue)) { str: String =>
        whenever(str.length <= maxLength && str.replaceAll("[^0-9]", "").length >= minLength) {
          val result = form.bind(Map(fieldName -> str)).apply(fieldName)
          result.errors should be(empty)
        }
      }
    }
  }
}
