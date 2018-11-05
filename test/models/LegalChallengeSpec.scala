package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class LegalChallengeSpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues {

  "LegalChallenge" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(LegalChallenge.values.toSeq)

      forAll(gen) {
        legalChallenge =>

          JsString(legalChallenge.toString).validate[LegalChallenge].asOpt.value mustEqual legalChallenge
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!LegalChallenge.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[LegalChallenge] mustEqual JsError("Unknown legalChallenge")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(LegalChallenge.values.toSeq)

      forAll(gen) {
        legalChallenge =>

          Json.toJson(legalChallenge) mustEqual JsString(legalChallenge.toString)
      }
    }
  }
}
