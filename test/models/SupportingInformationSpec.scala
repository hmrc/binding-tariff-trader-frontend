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
