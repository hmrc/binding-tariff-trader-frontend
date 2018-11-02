package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class SimilarItemCommodityCodeSpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues {

  "SimilarItemCommodityCode" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(SimilarItemCommodityCode.values.toSeq)

      forAll(gen) {
        similarItemCommodityCode =>

          JsString(similarItemCommodityCode.toString).validate[SimilarItemCommodityCode].asOpt.value mustEqual similarItemCommodityCode
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!SimilarItemCommodityCode.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[SimilarItemCommodityCode] mustEqual JsError("Unknown similarItemCommodityCode")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(SimilarItemCommodityCode.values.toSeq)

      forAll(gen) {
        similarItemCommodityCode =>

          Json.toJson(similarItemCommodityCode) mustEqual JsString(similarItemCommodityCode.toString)
      }
    }
  }
}
