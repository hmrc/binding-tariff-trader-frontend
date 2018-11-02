package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class ReturnSamplesSpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues {

  "ReturnSamples" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(ReturnSamples.values.toSeq)

      forAll(gen) {
        returnSamples =>

          JsString(returnSamples.toString).validate[ReturnSamples].asOpt.value mustEqual returnSamples
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!ReturnSamples.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[ReturnSamples] mustEqual JsError("Unknown returnSamples")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(ReturnSamples.values.toSeq)

      forAll(gen) {
        returnSamples =>

          Json.toJson(returnSamples) mustEqual JsString(returnSamples.toString)
      }
    }
  }
}
