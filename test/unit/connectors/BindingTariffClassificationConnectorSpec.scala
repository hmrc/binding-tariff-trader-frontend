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

import akka.actor.ActorSystem
import com.github.tomakehurst.wiremock.client.WireMock._
import config.FrontendAppConfig
import models.{CasePayloads, _}
import org.apache.http.HttpStatus
import org.mockito.BDDMockito._
import org.scalatest.mockito.MockitoSugar
import play.api.Environment
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.{HeaderCarrier, Upstream5xxResponse}
import uk.gov.hmrc.play.bootstrap.audit.DefaultAuditConnector
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import utils.JsonFormatters.{caseFormat, newCaseRequestFormat}

class BindingTariffClassificationConnectorSpec extends UnitSpec
  with WiremockTestServer with MockitoSugar with WithFakeApplication {

  private val configuration = mock[FrontendAppConfig]
  private val actorSystem = ActorSystem.create("test")
  private val wsClient: WSClient = fakeApplication.injector.instanceOf[WSClient]
  private val auditConnector = new DefaultAuditConnector(fakeApplication.configuration, fakeApplication.injector.instanceOf[Environment])
  private val client = new DefaultHttpClient(fakeApplication.configuration, auditConnector, wsClient, actorSystem)
  private implicit val hc = HeaderCarrier()

  private val connector = new BindingTariffClassificationConnector(configuration, client)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    given(configuration.bindingTariffClassificationUrl).willReturn(wireMockUrl)
  }

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

      await(connector.createCase(request)) shouldBe response
    }

    "propagate errors" in {
      stubFor(post(urlEqualTo("/cases"))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_BAD_GATEWAY)
        )
      )

      intercept[Upstream5xxResponse] {
        await(connector.createCase(request))
      }
    }
  }


  "Connector 'Find Cases By'" should {

    "Find empty paged case" in {

      stubFor(get(urlEqualTo("/cases?eori=eori1234567&status=NEW,OPEN&sort_by=created-date&sort_direction=desc&page=2&page_size=50"))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_OK)
          .withBody(CasePayloads.pagedEmpty)
        )
      )
      await(connector.findCasesBy("eori1234567", Set(CaseStatus.NEW, CaseStatus.OPEN), SearchPagination(2), Sort())) shouldBe Paged.empty[Case]
    }

    "Find valid paged case" in {

      stubFor(get(urlEqualTo("/cases?eori=eori1234567&status=NEW,OPEN&sort_by=created-date&sort_direction=desc&page=2&page_size=50"))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_OK)
          .withBody(CasePayloads.pagedGatewayCases)
        )
      )
      await(connector.findCasesBy("eori1234567", Set(CaseStatus.NEW, CaseStatus.OPEN), SearchPagination(2), Sort())) shouldBe Paged(Seq(oCase.btiCaseExample))
    }

    "propagate errors" in {
      stubFor(get(urlEqualTo("/cases?eori=eori1234567&status=NEW&sort_by=created-date&sort_direction=desc&page=1&page_size=2147483647"))
        .willReturn(aResponse()
          .withStatus(HttpStatus.SC_BAD_GATEWAY)
        )
      )

      intercept[Upstream5xxResponse] {
        await(connector.findCasesBy("eori1234567", Set(CaseStatus.NEW), NoPagination(), Sort()))
      }
    }
  }

}
