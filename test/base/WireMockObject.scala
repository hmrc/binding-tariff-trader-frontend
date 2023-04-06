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

package base

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.core.WireMockConfiguration

//scalastyle:off magic.number
object WireMockObject {

  lazy val wireMockUrl = s"http://$wireHost:$wirePort"
  private val wirePort = 20001
  private val wireHost = "localhost"
  private val wireMockConfiguration: WireMockConfiguration = new WireMockConfiguration()
    .bindAddress(wireHost)
    .port(wirePort)
    .asynchronousResponseEnabled(true)
    .asynchronousResponseThreads(10)
    .jettyAcceptQueueSize(999)
    .jettyStopTimeout(999L)

  private val wireMockServer = new WireMockServer(wireMockConfiguration)

  def start(): Unit = {
    wireMockServer.resetAll()

    if (!wireMockServer.isRunning) {
      configureFor(wireHost, wirePort)
      wireMockServer.start()

      Thread.sleep(5000)
    }
  }
}
