/*
 * Copyright 2026 HM Revenue & Customs
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

package models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SortSpec extends AnyWordSpec with Matchers {

  private val SortDirectionBinder = SortDirection.queryStringBindable
  private val sortFieldBinder     = SortField.queryStringBindable

  "SortDirection" when {
    "queryStringBindable" must {
      "bind when input is asc" in {
        SortDirectionBinder.bind("order", Map("order" -> Seq("asc"))) mustBe Some(Right(SortDirection.ASCENDING))
      }

      "bind when input is desc" in {
        SortDirectionBinder.bind("order", Map("order" -> Seq("desc"))) mustBe Some(Right(SortDirection.DESCENDING))
      }

      "fail to bind when input is invalid" in {
        SortDirectionBinder.bind("order", Map("order" -> Seq("invalid"))) mustBe Some(
          Left("Unable to bind a SortDirection")
        )
      }

      "unbind ASCENDING" in {
        SortDirectionBinder.unbind("order", SortDirection.ASCENDING) mustBe "order=asc"
      }

      "unbind DESCENDING" in {
        SortDirectionBinder.unbind("order", SortDirection.DESCENDING) mustBe "order=desc"
      }
    }
  }

  "SortField" when {
    "queryStringBindable" must {
      "bind when input is reference" in {
        sortFieldBinder.bind("sortBy", Map("sortBy" -> Seq("reference"))) mustBe Some(Right(SortField.REFERENCE))
      }

      "bind when input is status" in {
        sortFieldBinder.bind("sortBy", Map("sortBy" -> Seq("status"))) mustBe Some(Right(SortField.STATUS))
      }

      "fail to bind when input is invalid" in {
        sortFieldBinder.bind("sortBy", Map("sortBy" -> Seq("invalid"))) mustBe Some(Left("Unable to bind a SortField"))
      }

      "unbind REFERENCE" in {
        sortFieldBinder.unbind("sortBy", SortField.REFERENCE) mustBe "sortBy=reference"
      }

      "unbind STATUS" in {
        sortFieldBinder.unbind("sortBy", SortField.STATUS) mustBe "sortBy=status"
      }
    }
  }
}
