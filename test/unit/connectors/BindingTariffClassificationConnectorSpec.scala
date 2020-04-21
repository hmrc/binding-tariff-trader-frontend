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

import com.github.tomakehurst.wiremock.client.WireMock._
import models._
import org.apache.http.HttpStatus
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, Upstream5xxResponse}
import utils.JsonFormatters.{caseFormat, newCaseRequestFormat}

class BindingTariffClassificationConnectorSpec extends ConnectorTest {

  private val connector = new BindingTariffClassificationConnector(authenticatedHttpClient)(mockConfig)

  private def withHeaderCarrier(key: String, value: String) = HeaderCarrier(extraHeaders = Seq(key -> value))

  "Connector 'Create Case'" should {
    val request = oCase.newBtiCaseExample
    val requestJSON = Json.toJson(request).toString()

    "Create valid case" in {
      val response = oCase.btiCaseExample
      val responseJSON = Json.toJson(response).toString()

      stubFor(post(urlEqualTo("/cases"))
        .withRequestBody(equalToJson(requestJSON))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_OK)
          .withBody(responseJSON)
        )
      )

      await(connector.createCase(request)(withHeaderCarrier("X-Api-Token", appConfig.apiToken))) shouldBe response

      verify(
        postRequestedFor(urlEqualTo("/cases"))
          .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
      )
    }

    "Find valid case" in {
      val responseJSON = Json.toJson(oCase.btiCaseExample).toString()

      stubFor(get(urlEqualTo("/cases/id"))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_OK)
          .withBody(responseJSON)
        )
      )

      await(
        connector.findCase("id")(withHeaderCarrier("X-Api-Token", appConfig.apiToken))
      ) shouldBe Some(oCase.btiCaseExample)

      verify(
        getRequestedFor(urlEqualTo("/cases/id"))
          .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
      )
    }

    "propagate errors" in {
      stubFor(post(urlEqualTo("/cases"))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_BAD_GATEWAY)
        )
      )

      intercept[Upstream5xxResponse] {
        await(connector.createCase(request)(withHeaderCarrier("X-Api-Token", appConfig.apiToken)))
      }

      verify(
        postRequestedFor(urlEqualTo("/cases"))
          .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
      )
    }
  }


  "Connector 'Find Cases By'" should {

    "Find empty paged case" in {
      val url = "/cases?eori=eori1234567&status=NEW,OPEN&sort_by=created-date&sort_direction=desc&page=2&page_size=50"

      stubFor(get(urlEqualTo(url))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_OK)
          .withBody(CasePayloads.pagedEmpty)
        )
      )

      await(
        connector.findCasesBy(
          "eori1234567",
          Set(CaseStatus.NEW,
            CaseStatus.OPEN),
          SearchPagination(2),
          Sort()
        )(withHeaderCarrier("X-Api-Token", appConfig.apiToken))
      ) shouldBe Paged.empty[Case]

      verify(
        getRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
      )
    }

    "Find valid paged case" in {
      val url = "/cases?eori=eori1234567&status=NEW,OPEN&sort_by=created-date&sort_direction=desc&page=2&page_size=50"

      stubFor(get(urlEqualTo(url))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_OK)
          .withBody(CasePayloads.pagedGatewayCases)
        )
      )

      await(
        connector.findCasesBy(
          "eori1234567",
          Set(CaseStatus.NEW,
            CaseStatus.OPEN),
          SearchPagination(2),
          Sort()
        )(withHeaderCarrier("X-Api-Token", appConfig.apiToken))
      ) shouldBe Paged(Seq(oCase.btiCaseExample))

      verify(
        getRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
      )
    }

    "propagate errors" in {
      val url = "/cases?eori=eori1234567&status=NEW&sort_by=created-date&sort_direction=desc&page=1&page_size=2147483647"

      stubFor(get(urlEqualTo(url))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_BAD_GATEWAY)
        )
      )

      intercept[Upstream5xxResponse] {
        await(
          connector.findCasesBy(
            "eori1234567",
            Set(CaseStatus.NEW),
            NoPagination(),
            Sort()
          )(withHeaderCarrier("X-Api-Token", appConfig.apiToken)))
      }

      verify(
        getRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
      )
    }

  }

}
