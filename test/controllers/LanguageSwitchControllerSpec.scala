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

package controllers

import base.SpecBase
import models.Languages._
import play.api.http.Status.SEE_OTHER
import play.api.test.Helpers.writeableOf_AnyContentAsEmpty
import play.api.test.{FakeRequest, Helpers}

class LanguageSwitchControllerSpec extends SpecBase {

  "when translation is enabled switching language" should {
    "set the language to Cymraeg" in {
      val request = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage(Cymraeg).url)

      val result = Helpers.route(appWithWelshTranslation, request).get

      status(result)             shouldBe SEE_OTHER
      getLanguageCookies(result) shouldBe "cy"
    }

    "set the language to English" in {
      val request = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage(English).url)

      val result = Helpers.route(appWithEnglishTranslation, request).get

      status(result)             shouldBe SEE_OTHER
      getLanguageCookies(result) shouldBe "en"
    }
  }

  "when translation is disabled  switching language" should {

    "should set the language to English regardless of what is requested" in {
      val cymraegRequest = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage(Cymraeg).url)
      val englishRequest = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage(English).url)

      val cymraegResult = Helpers.route(appWithEnglishTranslation, cymraegRequest).get
      val englishResult = Helpers.route(appWithEnglishTranslation, englishRequest).get

      status(cymraegResult)             shouldBe SEE_OTHER
      getLanguageCookies(cymraegResult) shouldBe "en"

      status(englishResult)             shouldBe SEE_OTHER
      getLanguageCookies(englishResult) shouldBe "en"
    }
  }

}
