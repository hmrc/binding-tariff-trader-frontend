/*
 * Copyright 2023 HM Revenue & Customs
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
import views.PaginationUtil._

class PaginationUtilSpec extends SpecBase {

  // scalastyle:off magic.number
  "PaginationUtil" when {
    "surroundingPages" should {
      "return length of 1" in {
        surroundingPages(1, 1, 5).length shouldBe 1
      }

      "count upwards up to 5" in {
        surroundingPages(1, 2, 5) shouldBe Seq(1, 2)
        surroundingPages(1, 3, 5) shouldBe Seq(1, 2, 3)
        surroundingPages(1, 4, 5) shouldBe Seq(1, 2, 3, 4)
        surroundingPages(1, 5, 5) shouldBe Seq(1, 2, 3, 4, 5)
        surroundingPages(1, 6, 5) shouldBe Seq(1, 2, 3, 4, 5)
        surroundingPages(1, 7, 5) shouldBe Seq(1, 2, 3, 4, 5)
      }

      "count backwards up to index" in {
        surroundingPages(2, 2, 5) shouldBe Seq(1, 2)
        surroundingPages(3, 3, 5) shouldBe Seq(1, 2, 3)
        surroundingPages(4, 4, 5) shouldBe Seq(1, 2, 3, 4)
        surroundingPages(5, 5, 5) shouldBe Seq(1, 2, 3, 4, 5)
        surroundingPages(6, 6, 5) shouldBe Seq(2, 3, 4, 5, 6)
        surroundingPages(7, 7, 5) shouldBe Seq(3, 4, 5, 6, 7)
      }

      "center current index" in {
        surroundingPages(1, 7, 5) shouldBe Seq(1, 2, 3, 4, 5)
        surroundingPages(2, 7, 5) shouldBe Seq(1, 2, 3, 4, 5)
        surroundingPages(3, 7, 5) shouldBe Seq(1, 2, 3, 4, 5)
        surroundingPages(4, 7, 5) shouldBe Seq(2, 3, 4, 5, 6)
        surroundingPages(5, 7, 5) shouldBe Seq(3, 4, 5, 6, 7)
        surroundingPages(6, 7, 5) shouldBe Seq(3, 4, 5, 6, 7)
        surroundingPages(7, 7, 5) shouldBe Seq(3, 4, 5, 6, 7)
      }
    }
  }
  // scalastyle:on magic.number
}
