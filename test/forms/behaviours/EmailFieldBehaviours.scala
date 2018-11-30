/*
 * Copyright 2018 HM Revenue & Customs
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
import generators.EmailAddressGenerators
import org.scalatest.prop.PropertyChecks
import play.api.data.{Form, FormError}

trait EmailFieldBehaviours extends FormSpec with PropertyChecks with EmailAddressGenerators {

  protected def emailFieldWithMaxLength(form: Form[_],
                                        fieldName: String,
                                        maxLength: Int,
                                        lengthError: FormError): Unit = {

    s"not bind email addresses longer than $maxLength characters" in {

      forAll(validEmailAddressesLongerThan(maxLength) -> "longEmail") { str: String =>
        val result = form.bind(Map(fieldName -> str)).apply(fieldName)
        result.errors shouldEqual Seq(lengthError)
      }

    }

    "not bind email addresses without `.` and `@` characters" in {
      // TODO
    }

  }

}
