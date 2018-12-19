package views

import org.scalatest.WordSpec
import uk.gov.hmrc.play.test.UnitSpec

class ViewUtilsSpec extends WordSpec with UnitSpec {

  "HumanReadableSize" must {

    "return zero size for 0 Bytes " in {

      ViewUtils.humanReadableSize(0) shouldBe "0.0 B"

    }

    "return Kilobytes size for 1000000 Bytes " in {

      ViewUtils.humanReadableSize(1000000L) shouldBe "976.6 KB"

    }

    "return Megabytes size for 1000000000 Bytes " in {

      ViewUtils.humanReadableSize(100000000L) shouldBe "95.4 MB"

    }

  }

}
