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

package forms.behaviours

import forms.FormSpec
import org.scalatest.prop.PropertyChecks
import play.api.data.{Form, FormError}

trait EmailFieldBehaviours extends FormSpec with PropertyChecks {

  protected def validEmailFieldWithMaxLength(form: Form[_],
                                             fieldName: String,
                                             maxLength: Int,
                                             lengthError: FormError,
                                             invalidEmailError: FormError): Unit = {

    s"not bind valid email addresses longer than $maxLength characters" in {

      // TODO: we should use ScalaCheck for testing this, but it is not trivial to generate valid email addresses

      val s = "a123456789"
      val usr = List.fill(6)(s).mkString
      val domain = s"${List.fill(3)(s).mkString}.me.you.us"
      val tooLongEmailAddress = s"$usr@$domain"
      tooLongEmailAddress.length shouldBe maxLength + 1

      val invalidEmailAddresses = List(tooLongEmailAddress)
      invalidEmailAddresses foreach { str: String =>
        val result = form.bind(Map(fieldName -> str)).apply(fieldName)
        result.errors shouldEqual Seq(lengthError)
      }
    }

    "not bind invalid email addresses" in {

      // TODO: we should use ScalaCheck for testing this

      val invalidEmailAddresses = List(
        s"${List.fill(65)('a').mkString}@email.me",
        s"frank@${List.fill(64)('c').mkString}.fr",
        "emilio@email.italy",
        ".",
        "jon@jon..com",
        "jon..smith@email.com",
        "emilio.smith",
        "jon@gmail",
        "jon@ gmail.com",
        "a@a.a",
        "people@-email.com",
        "people@email.-com"
      )

      invalidEmailAddresses foreach { str: String =>
        val result = form.bind(Map(fieldName -> str)).apply(fieldName)
        result.errors shouldEqual Seq(invalidEmailError)
      }
    }

  }

}
