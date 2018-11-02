package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class WhenToSendSampleSpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues {

  "WhenToSendSample" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(WhenToSendSample.values.toSeq)

      forAll(gen) {
        whenToSendSample =>

          JsString(whenToSendSample.toString).validate[WhenToSendSample].asOpt.value mustEqual whenToSendSample
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!WhenToSendSample.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[WhenToSendSample] mustEqual JsError("Unknown whenToSendSample")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(WhenToSendSample.values.toSeq)

      forAll(gen) {
        whenToSendSample =>

          Json.toJson(whenToSendSample) mustEqual JsString(whenToSendSample.toString)
      }
    }
  }
}
