package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class CommodityCodeBestMatchSpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues {

  "CommodityCodeBestMatch" must {

    "deserialise valid values" in {

      val gen = Gen.oneOf(CommodityCodeBestMatch.values.toSeq)

      forAll(gen) {
        commodityCodeBestMatch =>

          JsString(commodityCodeBestMatch.toString).validate[CommodityCodeBestMatch].asOpt.value mustEqual commodityCodeBestMatch
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!CommodityCodeBestMatch.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[CommodityCodeBestMatch] mustEqual JsError("Unknown commodityCodeBestMatch")
      }
    }

    "serialise" in {

      val gen = Gen.oneOf(CommodityCodeBestMatch.values.toSeq)

      forAll(gen) {
        commodityCodeBestMatch =>

          Json.toJson(commodityCodeBestMatch) mustEqual JsString(commodityCodeBestMatch.toString)
      }
    }
  }
}
