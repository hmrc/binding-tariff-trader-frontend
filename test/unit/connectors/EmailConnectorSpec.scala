/*
 * Copyright 2019 HM Revenue & Customs
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

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import models.{ApplicationSubmittedEmail, ApplicationSubmittedParameters}
import org.apache.http.HttpStatus
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.http.Upstream5xxResponse

class EmailConnectorSpec extends ConnectorTest with ScalaFutures{

  private val connector = new EmailConnector(appConfig, standardHttpClient)

  private val email = ApplicationSubmittedEmail(Seq("user@domain.com"), ApplicationSubmittedParameters("ref", "name"))

  "Connector 'Send'" should {

    "POST Email payload" in {
      stubFor(post(urlEqualTo("/hmrc/email"))
        .withRequestBody(new EqualToJsonPattern(fromResource("advice_request_email-request.json"), true, false))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_ACCEPTED))
      )

      whenReady(connector.send(email)){_ =>
          verify(
            postRequestedFor(urlEqualTo("/hmrc/email"))
              .withoutHeader("X-Api-Token")
          )
        }
    }

    "propagate errors" in {
      stubFor(post(urlEqualTo("/hmrc/email"))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_BAD_GATEWAY)
        )
      )

      whenReady(connector.send(email).failed) { ex =>
        ex mustBe a[Upstream5xxResponse]

        verify(
          postRequestedFor(urlEqualTo("/hmrc/email"))
            .withoutHeader("X-Api-Token")
        )
      }
    }
  }

}
