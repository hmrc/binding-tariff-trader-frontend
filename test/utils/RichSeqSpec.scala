/*
 * Copyright 2020 HM Revenue & Customs
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

package utils

import org.scalatest.{FunSuite, Matchers, WordSpec}
import CollectionUtils.RichSeq

class RichSeqSpec extends WordSpec with Matchers {

  "orIfEmpty" when {
    "called on an empty sequence" should {
      "result in a sequence containing the default supplied" in {
        Seq().orIfEmpty("some value") shouldBe Seq("some value")
      }
    }

    "called on a non-empty sequence" should {
      "result in an unchanged sequence, ignoring the default supplied" in {
        Seq("original", "elements").orIfEmpty("some value") shouldBe Seq("original", "elements")
      }
    }
  }
}
