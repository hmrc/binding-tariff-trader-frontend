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
import org.scalatest.{BeforeAndAfterAll, Suite}

trait WiremockTestServer extends BeforeAndAfterAll {
  self: Suite =>

  protected val wirePort = 20001
  protected val wireHost = "localhost"
  protected val wireMockUrl = s"http://$wireHost:$wirePort"
  protected val wireMockServer = new WireMockServer(wirePort)

  override def beforeAll(): Unit = {
    super.beforeAll()

    wireMockServer.start()
    configureFor(wireHost, wirePort)

    if (System.getProperty("os.name").toLowerCase().startsWith("mac os x"))
      Thread.sleep(10000)

  }

  override def afterAll(): Unit = {
    super.afterAll()

    wireMockServer.resetAll()
    wireMockServer.stop()
  }

}
