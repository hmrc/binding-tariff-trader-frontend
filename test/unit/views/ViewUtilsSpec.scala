/*
 * Copyright 2022 HM Revenue & Customs
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

import base.SpecBase
import org.scalatest.wordspec.AnyWordSpec

class ViewUtilsSpec extends AnyWordSpec with SpecBase {

  "HumanReadableSize" must {

    "return zero size for 0 Bytes " in {

      ViewUtils.humanReadableSize(0) shouldBe "0.0 B"

    }

    "return Kilobytes size for 1000000 Bytes " in {

      val bytes: Long = 1000000L

      ViewUtils.humanReadableSize(bytes) shouldBe "976.6 KB"

    }

    "return Megabytes size for 1000000000 Bytes " in {

      val bytes: Long = 100000000L

      ViewUtils.humanReadableSize(bytes) shouldBe "95.4 MB"

    }

  }

  "confidentialityStatusLabel" must {

    "return expected string" in {
      ViewUtils.confidentialityStatusLabel(confidential = true)(messages) shouldBe "Keep confidential"
    }
  }

}
