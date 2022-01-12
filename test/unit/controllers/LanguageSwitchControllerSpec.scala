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

package unit.controllers

import controllers.routes
import models.Languages._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.OptionValues
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

class LanguageSwitchControllerSpec extends AnyWordSpec with Matchers with OptionValues with GuiceOneAppPerSuite with MockitoSugar  {

  "when translation is enabled switching language" should {
    "set the language to Cymraeg" in {
      val application = applicationBuilder(enableWelshTranslation = true)

      running(application) {
        val request = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(Cymraeg).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        cookies(result).get("PLAY_LANG").value.value shouldBe "cy"
      }
    }

    "set the language to English" in {
      val application = applicationBuilder(enableWelshTranslation = true)

      running(application) {
        val request = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(English).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        cookies(result).get("PLAY_LANG").value.value shouldBe "en"
      }
    }
  }

  "when translation is disabled  switching language" should {

    "should set the language to English regardless of what is requested" in {
      val application = applicationBuilder(enableWelshTranslation = false)

      running(application) {
        val cymraegRequest = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(Cymraeg).url)
        val englishRequest = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(English).url)

        val cymraegResult = route(application, cymraegRequest).value
        val englishResult = route(application, englishRequest).value

        status(cymraegResult) shouldBe SEE_OTHER
        cookies(cymraegResult).get("PLAY_LANG").value.value shouldBe "en"

        status(englishResult) shouldBe SEE_OTHER
        cookies(englishResult).get("PLAY_LANG").value.value shouldBe "en"
      }
    }
  }

  private def applicationBuilder(enableWelshTranslation: Boolean) = {
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false,
        "metrics.enabled" -> false,
        "microservice.services.features.welsh-translation" -> enableWelshTranslation
      )
      .build()
  }
}
