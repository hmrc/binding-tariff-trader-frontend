/*
 * Copyright 2026 HM Revenue & Customs
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

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import models.*
import models.requests.NewEventRequest
import play.api.http.Status.{BAD_GATEWAY, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import utils.JsonFormatters.*

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global

class BindingTariffClassificationConnectorSpec extends ConnectorTest {

  trait Setup {
    val connector = new BindingTariffClassificationConnector(httpClient, metrics)(mockConfig, global)
  }

  private def withHeaderCarrier(value: String): HeaderCarrier =
    HeaderCarrier(otherHeaders = Seq("X-Api-Token" -> value))

  val btiCaseExample: Case = oCase.btiCaseExample
  val responseJSON: String = Json.toJson(btiCaseExample).toString()

  "Connector 'Create Case'" should {
    val request: NewCaseRequest = oCase.newBtiCaseExample
    val requestJSON: String     = Json.toJson(request).toString()

    "Create valid case with x-api-token" in new Setup {

      WireMock.stubFor(
        post(urlEqualTo("/cases"))
          .withRequestBody(equalToJson(requestJSON))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(responseJSON)
          )
      )

      await(connector.createCase(request)(withHeaderCarrier(mockConfig.apiToken))) shouldBe btiCaseExample

      WireMock.verify(
        postRequestedFor(urlEqualTo("/cases"))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "Find valid case with x-api-token" in new Setup {

      WireMock.stubFor(
        get(urlEqualTo("/cases/id")).willReturn(
          aResponse()
            .withStatus(OK)
            .withBody(responseJSON)
        )
      )

      await(connector.findCase("id")(withHeaderCarrier(mockConfig.apiToken))) shouldBe Some(btiCaseExample)

      WireMock.verify(
        getRequestedFor(urlEqualTo("/cases/id"))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "propagate errors with x-api-token" in new Setup {
      WireMock.stubFor(
        post(urlEqualTo("/cases"))
          .willReturn(
            aResponse()
              .withStatus(BAD_GATEWAY)
          )
      )

      intercept[UpstreamErrorResponse] {
        await(connector.createCase(request)(withHeaderCarrier(mockConfig.apiToken)))
      }

      WireMock.verify(
        postRequestedFor(urlEqualTo("/cases"))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "Create valid case without x-api-token" in new Setup {

      WireMock.stubFor(
        post(urlEqualTo("/cases"))
          .withRequestBody(equalToJson(requestJSON))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(responseJSON)
          )
      )

      await(connector.createCase(request)(hc)) shouldBe btiCaseExample

      WireMock.verify(
        postRequestedFor(urlEqualTo("/cases"))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "Find valid case without x-api-token" in new Setup {

      WireMock.stubFor(
        get(urlEqualTo("/cases/id"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(responseJSON)
          )
      )

      await(connector.findCase("id")(hc)) shouldBe Some(btiCaseExample)

      WireMock.verify(
        getRequestedFor(urlEqualTo("/cases/id"))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "propagate errors without x-api-token" in new Setup {
      WireMock.stubFor(
        post(urlEqualTo("/cases"))
          .willReturn(
            aResponse()
              .withStatus(BAD_GATEWAY)
          )
      )

      intercept[UpstreamErrorResponse] {
        await(connector.createCase(request)(hc))
      }

      WireMock.verify(
        postRequestedFor(urlEqualTo("/cases"))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }
  }

  "Connector 'Put Case'" should {
    "Execute update for existing case" in new Setup {
      val url: String = s"/cases/${btiCaseExample.reference}"

      WireMock.stubFor(
        put(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(CasePayloads.jsonOf(Some(btiCaseExample)))
          )
      )

      await(connector.putCase(btiCaseExample)(withHeaderCarrier(mockConfig.apiToken))) shouldBe btiCaseExample

      WireMock.verify(
        putRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "Return error for missing case" in new Setup {
      val url: String = s"/cases/${btiCaseExample.reference}"

      WireMock.stubFor(
        put(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
          )
      )

      intercept[UpstreamErrorResponse] {
        await(
          connector.putCase(btiCaseExample)(withHeaderCarrier(mockConfig.apiToken))
        )
      }

      WireMock.verify(
        putRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "Propagate errors" in new Setup {
      val url = s"/cases/${btiCaseExample.reference}"

      WireMock.stubFor(
        put(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(BAD_GATEWAY)
          )
      )

      intercept[UpstreamErrorResponse] {
        await(
          connector.putCase(btiCaseExample)(withHeaderCarrier(mockConfig.apiToken))
        )
      }

      WireMock.verify(
        putRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }
  }

  "Connector 'Update Case'" should {
    "Execute update for existing case" in new Setup {
      val caseRef: String = btiCaseExample.reference
      val url             = s"/cases/$caseRef"

      WireMock.stubFor(
        post(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(CasePayloads.jsonOf(Some(btiCaseExample)))
          )
      )

      await(connector.updateCase(caseRef, CaseUpdate())(withHeaderCarrier(mockConfig.apiToken))) shouldBe Some(
        btiCaseExample
      )

      WireMock.verify(
        postRequestedFor(urlEqualTo(url)).withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "Return empty response for missing case" in new Setup {
      val url = s"/cases/foo"

      WireMock.stubFor(
        post(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
          )
      )

      await(
        connector.updateCase("foo", CaseUpdate())(withHeaderCarrier(mockConfig.apiToken))
      ) shouldBe None

      WireMock.verify(
        postRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "Propagate errors" in new Setup {
      val url = "/cases/foo"

      WireMock.stubFor(
        post(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(BAD_GATEWAY)
          )
      )

      intercept[UpstreamErrorResponse] {
        await(
          connector.updateCase("foo", CaseUpdate())(withHeaderCarrier(mockConfig.apiToken))
        )
      }

      WireMock.verify(
        postRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }
  }

  "Connector 'All Cases'" should {
    val pageSize = 50

    "Find empty paged case" in new Setup {
      val page = 2
      val url =
        "/cases?sort_by=created-date&sort_direction=desc&page=2&page_size=50&application_type=BTI&migrated=false"

      WireMock.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(CasePayloads.pagedEmpty)
          )
      )

      await(
        connector.allCases(SearchPagination(page, pageSize), Sort())(withHeaderCarrier(mockConfig.apiToken))
      ) shouldBe Paged.empty[Case]

      WireMock.verify(
        getRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "Find valid paged case" in new Setup {
      val page = 2
      val url =
        "/cases?sort_by=created-date&sort_direction=desc&page=2&page_size=50&application_type=BTI&migrated=false"

      WireMock.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(CasePayloads.pagedGatewayCases)
          )
      )

      await(
        connector.allCases(SearchPagination(page, pageSize), Sort())(withHeaderCarrier(mockConfig.apiToken))
      ) shouldBe Paged(Seq(btiCaseExample))

      WireMock.verify(
        getRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "propagate errors" in new Setup {
      val page = 1
      val url =
        "/cases?sort_by=created-date&sort_direction=desc&page=1&page_size=50&application_type=BTI&migrated=false"

      WireMock.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(BAD_GATEWAY)
          )
      )

      intercept[UpstreamErrorResponse] {
        await(
          connector.allCases(SearchPagination(page, pageSize), Sort())(withHeaderCarrier(mockConfig.apiToken))
        )
      }

      WireMock.verify(
        getRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }
  }

  "Connector 'Find Cases By'" should {

    "Find empty paged case" in new Setup {
      val url =
        "/cases?eori=eori1234567&status=NEW,OPEN&sort_by=created-date&sort_direction=desc&page=2&page_size=50&migrated=false"

      WireMock.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(CasePayloads.pagedEmpty)
          )
      )

      await(
        connector.findCasesBy(
          "eori1234567",
          Set(CaseStatus.NEW, CaseStatus.OPEN),
          SearchPagination(2),
          Sort()
        )(withHeaderCarrier(mockConfig.apiToken))
      ) shouldBe Paged.empty[Case]

      WireMock.verify(
        getRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "Find valid paged case" in new Setup {
      val url =
        "/cases?eori=eori1234567&status=NEW,OPEN&sort_by=created-date&sort_direction=desc&page=2&page_size=50&migrated=false"

      WireMock.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(CasePayloads.pagedGatewayCases)
          )
      )

      await(
        connector.findCasesBy(
          "eori1234567",
          Set(CaseStatus.NEW, CaseStatus.OPEN),
          SearchPagination(2),
          Sort()
        )(withHeaderCarrier(mockConfig.apiToken))
      ) shouldBe Paged(Seq(btiCaseExample))

      WireMock.verify(
        getRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "propagate errors" in new Setup {
      val url =
        "/cases?eori=eori1234567&status=NEW&sort_by=created-date&sort_direction=desc&page=1&page_size=2147483647&migrated=false"

      WireMock.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(BAD_GATEWAY)
          )
      )

      intercept[UpstreamErrorResponse] {
        await(
          connector.findCasesBy(
            "eori1234567",
            Set(CaseStatus.NEW),
            NoPagination(),
            Sort()
          )(withHeaderCarrier(mockConfig.apiToken))
        )
      }

      WireMock.verify(
        getRequestedFor(urlEqualTo(url))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }
  }

  "Connector 'Create Event'" should {
    val event: Event =
      Event("id", CaseCreated("Case created"), Operator("", Some("user name")), "case-ref")
    val eventRequest: NewEventRequest =
      NewEventRequest(CaseCreated("comment"), Operator("", Some("user name")), Instant.now())

    "create event" in new Setup {
      val ref                                = "case-reference"
      val validCase: Case                    = btiCaseExample.copy(reference = ref)
      val validEventRequest: NewEventRequest = eventRequest
      val validEvent: Event                  = event.copy(caseReference = ref)
      val requestJson: String                = Json.toJson(validEventRequest).toString()
      val responseJson: String               = Json.toJson(validEvent).toString()

      WireMock.stubFor(
        post(urlEqualTo(s"/cases/$ref/events"))
          .withRequestBody(equalToJson(requestJson))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(responseJson)
          )
      )

      await(connector.createEvent(validCase, validEventRequest)) shouldBe validEvent

      WireMock.verify(
        postRequestedFor(urlEqualTo(s"/cases/$ref/events"))
          .withHeader("X-Api-Token", equalTo(fakeAuthToken))
      )
    }

    "create event with an unknown case reference" in new Setup {
      val ref                                = "unknown-reference"
      val validCase: Case                    = btiCaseExample.copy(reference = ref)
      val validEventRequest: NewEventRequest = eventRequest
      val requestJson: String                = Json.toJson(validEventRequest).toString()

      WireMock.stubFor(
        post(urlEqualTo(s"/cases/$ref/events"))
          .withRequestBody(equalToJson(requestJson))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
          )
      )

      intercept[UpstreamErrorResponse] {
        await(connector.createEvent(validCase, validEventRequest))
      }

      WireMock.verify(
        postRequestedFor(urlEqualTo(s"/cases/$ref/events"))
          .withHeader("X-Api-Token", equalTo(fakeAuthToken))
      )
    }

  }

}
