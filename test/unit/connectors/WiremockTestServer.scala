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

package connectors

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options


trait WiremockTestServer extends BeforeAndAfterAll {
  self: Suite =>

  protected val wireMockServer = new WireMockServer(options().dynamicPort())
  protected def wireMockUrl: String = wireMockServer.baseUrl()

  override def beforeAll(): Unit = {
    super.beforeAll()

    wireMockServer.start()

    Thread.sleep(10000)
  }

  override def afterAll(): Unit = {
    super.afterAll()

    wireMockServer.resetAll()

    wireMockServer.stop()
    wireMockServer.shutdown()
  }

  private def waitWireMockServerOrException(status: Boolean): Unit = {
    val microSleep = 100
    var times = 0
    while (wireMockServer.isRunning != status) {
      times = times + 1
      Thread.sleep(microSleep)

      if (times > microSleep / 2)
        throw new RuntimeException("Can't start wire mock please re-run tests!")
    }

    Thread.sleep(250)
  }
}
