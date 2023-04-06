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

package models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsError, Json}

class PagedSpec extends AnyWordSpec with Matchers {

  "Paged" when {
    "map" should {
      "produce correct mapped result" in {
        Paged(Seq("RUN", "WALK")).map(_.toLowerCase) shouldBe Paged(Seq("run", "walk"))
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(Paged(Seq("run", "walk"), 1, 2, 3)) shouldBe
          Json.parse(
            """
              |{
              |  "results": ["run", "walk"],
              |  "pageIndex": 1,
              |  "pageSize": 2,
              |  "resultCount": 3
              |}
          """.stripMargin
          )
      }
    }

    "read from valid JSON" should {
      "produce the expected Paged object" in {
        Json
          .parse(
            """
            |{
            |  "results": ["run", "walk"],
            |  "pageIndex": 1,
            |  "pageSize": 2,
            |  "resultCount": 3
            |}
          """.stripMargin
          )
          .as[Paged[String]] shouldBe Paged(Seq("run", "walk"), 1, 2, 3)
      }
    }

    "read from invalid JSON" should {
      "produce a JsError" in {
        Json
          .parse(
            """
            |{
            |  "result": "run",
            |  "pageIndex": 1,
            |  "pageSize": 2,
            |  "resultCount": 3
            |}
          """.stripMargin
          )
          .validate[Paged[String]] shouldBe a[JsError]
      }
    }
  }
}
