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

package config

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class FrontendAppConfigSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private val configuration: Configuration         = Configuration.from(Map.empty)
  private val servicesConfig: ServicesConfig       = app.injector.instanceOf[ServicesConfig]
  private val frontendAppConfig: FrontendAppConfig = new FrontendAppConfig(configuration, servicesConfig)

  "FrontendAppConfig" must {
    "throw an exception with a message if configuration is not defined" in {
      intercept[Exception](frontendAppConfig.loginUrl).getMessage mustBe "Missing configuration key: urls.login"
    }
  }
}
