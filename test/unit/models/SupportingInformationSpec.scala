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

package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class SupportingInformationSpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues {

  "SupportingInformation" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(SupportingInformation.values.toSeq)

      forAll(gen) {
        supportingInformation =>

          JsString(supportingInformation.toString).validate[SupportingInformation].asOpt.value mustEqual supportingInformation
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!SupportingInformation.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[SupportingInformation] mustEqual JsError("Unknown supportingInformation")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(SupportingInformation.values.toSeq)

      forAll(gen) {
        supportingInformation =>

          Json.toJson(supportingInformation) mustEqual JsString(supportingInformation.toString)
      }
    }
  }
}
