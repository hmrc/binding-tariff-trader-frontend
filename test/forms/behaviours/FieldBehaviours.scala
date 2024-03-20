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

package forms.behaviours

import forms.FormSpec
import generators.Generators
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.data.{Form, FormError}

trait FieldBehaviours extends FormSpec with ScalaCheckDrivenPropertyChecks with Generators {

  def fieldThatBindsValidData(form: Form[_], fieldName: String, validDataGenerator: Gen[String]): Unit =
    "bind valid data" in {

      forAll(validDataGenerator -> "validDataItem") { dataItem: String =>
        val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
        result.value.value shouldBe dataItem
      }
    }

  def mandatoryField(form: Form[_], fieldName: String, requiredError: FormError): Unit = {

    "not bind when key is not present at all" in {
      val result = form.bind(emptyForm).apply(fieldName)
      result.errors shouldEqual Seq(requiredError)
    }

    "not bind blank values" in {
      val result = form.bind(Map(fieldName -> "")).apply(fieldName)
      result.errors shouldEqual Seq(requiredError)
    }
  }

  def postcodeField(
    form: Form[_],
    fieldName: String,
    emptyPostcodeErrorKey: Seq[FormError],
    notValidPostcodeErrorKey: Seq[FormError],
    tooLongPostcodeErrorKey: Seq[FormError]
  ): Unit = {

    "bind when key is not present at all and country is not GB" in {
      val result = form.bind(emptyForm).apply(fieldName)
      result.errors shouldEqual Seq()
    }

    "bind when key is present and country is not GB" in {
      val result = form.bind(Map(fieldName -> "", "country" -> "MD")).apply(fieldName)

      result.errors shouldEqual Seq()
    }

    "bind blank values and country is not GB" in {
      val result = form.bind(Map(fieldName -> "")).apply(fieldName)
      result.errors shouldEqual Seq()
    }

    "bind when key is valid post code and country is GB" in {
      val result = form.bind(Map(fieldName -> "AA1 2BB", "country" -> "GB")).apply(fieldName)
      result.errors shouldEqual Seq()
    }

    "not bind when key is invalid post code and country is GB" in {
      val result = form.bind(Map(fieldName -> "postcode", "country" -> "GB")).apply(fieldName)
      result.errors shouldEqual notValidPostcodeErrorKey
    }

    "not bind when key is empty post code and country is GB" in {
      val result = form.bind(Map(fieldName -> "", "country" -> "GB")).apply(fieldName)
      result.errors shouldEqual emptyPostcodeErrorKey
    }

    "not bind when key is valid post code and country is not GB but is too long" in {
      val result = form.bind(Map(fieldName -> "12345123451234512345", "country" -> "MD")).apply(fieldName)
      result.errors shouldEqual tooLongPostcodeErrorKey
    }
  }

  def commodityCodeField(
    form: Form[_],
    fieldName: String,
    requiredErrorKey: FormError,
    notNumericTypeErrorKey: FormError,
    maxLengthErrorKey: FormError,
    minLengthErrorKey: FormError
  ): Unit = {
    "not bind when commodity code field not present" in {
      val result = form.bind(emptyForm).apply(fieldName)
      result.errors shouldEqual Seq(requiredErrorKey)
    }

    "not bind commodity code blank values" in {
      val result = form.bind(Map(fieldName -> "")).apply(fieldName)
      result.errors shouldEqual Seq(requiredErrorKey)
    }

    "not bind min length not met" in {
      val result = form.bind(Map(fieldName -> "1")).apply(fieldName)
      result.errors shouldEqual Seq(minLengthErrorKey)
    }

    "not bind non-numeric values" in {
      val result = form.bind(Map(fieldName -> "122jh12")).apply(fieldName)
      result.errors.map(_.message) shouldEqual Seq(notNumericTypeErrorKey.message)
    }

    "not bind max length exceeded" in {
      val result = form.bind(Map(fieldName -> "1" * 26)).apply(fieldName)
      result.errors shouldEqual Seq(maxLengthErrorKey)
    }
  }

}
