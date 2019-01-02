/*
 * Copyright 2019 HM Revenue & Customs
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
