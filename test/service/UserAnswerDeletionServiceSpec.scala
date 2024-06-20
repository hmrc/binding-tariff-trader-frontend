/*
 * Copyright 2024 HM Revenue & Customs
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

package service

import base.SpecBase
import models.UserAnswers
import models.cache.CacheMap
import pages._
import play.api.libs.json.JsValue

class UserAnswerDeletionServiceSpec extends SpecBase {

  val service = new UserAnswerDeletionService

  "UserAnswerDeletionServiceSpec" when {

    ".deleteAllUserAnswersExcept" when {

      "given some pages to exclude" should {

        "return the excluded useranswers" in {

          val data = Map[String, JsValue]()

          val cacheMapData = CacheMap("fake-id", data)

          val emptyUserAnswers: UserAnswers = UserAnswers(cacheMapData)

          val savedAnswers =
            emptyUserAnswers
              .set(AddAnotherRulingPage, true)
              .set(AddConfidentialInformationPage, false)
              .set(AddSupportingDocumentsPage, false)
              .set(PreviousBTIRulingPage, true)
              .set(ProvideConfidentialInformationPage, "some private info")

          val actual: UserAnswers =
            service.deleteAllUserAnswersExcept(
              userAnswers = savedAnswers,
              excludedPages =
                Seq(AddAnotherRulingPage, AddConfidentialInformationPage, ProvideConfidentialInformationPage)
            )

          val expected =
            emptyUserAnswers
              .set(AddAnotherRulingPage, true)
              .set(AddConfidentialInformationPage, false)
              .set(ProvideConfidentialInformationPage, "some private info")

          actual shouldBe expected
        }
      }

      "given No pages to exclude" should {

        "return empty useranswers" in {

          val data = Map[String, JsValue]()

          val cacheMapData = CacheMap("fake-id", data)

          val emptyUserAnswers: UserAnswers = UserAnswers(cacheMapData)

          val savedAnswers =
            emptyUserAnswers
              .set(AddAnotherRulingPage, true)
              .set(AddConfidentialInformationPage, false)
              .set(AddSupportingDocumentsPage, false)
              .set(PreviousBTIRulingPage, true)
              .set(ProvideConfidentialInformationPage, "some private info")

          val actual: UserAnswers =
            service.deleteAllUserAnswersExcept(
              userAnswers   = savedAnswers,
              excludedPages = Seq()
            )

          val expected = emptyUserAnswers

          actual shouldBe expected
        }
      }
    }
  }
}
