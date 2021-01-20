/*
 * Copyright 2021 HM Revenue & Customs
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
import models.response.FilestoreResponse
import models.{Attachment, FileAttachment}
import org.mockito.BDDMockito.given
import play.api.http.Status
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import models.requests.FileStoreInitiateRequest
import models.response.FileStoreInitiateResponse
import models.response.UpscanFormTemplate

class BindingTariffFilestoreConnectorSpec extends ConnectorTest {

  private def connector = new BindingTariffFilestoreConnector(wsClient, authenticatedHttpClient, metrics)(mockConfig, implicitly)

  private def withHeaderCarrier(key: String, value: String) = HeaderCarrier(extraHeaders = Seq(key -> value))

  "Connector" should {

    "Initiate" in {
      stubFor(
        post("/file/initiate")
          .willReturn(
            aResponse()
              .withStatus(Status.ACCEPTED)
              .withBody(fromResource("binding-tariff-filestore_initiate-response.json"))
          )
      )

      val initiateRequest = FileStoreInitiateRequest(maxFileSize = 0)

      await(connector.initiate(initiateRequest)) shouldBe FileStoreInitiateResponse(
        id = "id",
        upscanReference = "ref",
        uploadRequest = UpscanFormTemplate(
          "http://localhost:20001/upscan/upload",
          Map("key" -> "value")
        )
      )

      verify(
        postRequestedFor(urlEqualTo("/file/initiate"))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "Get" in {
      stubFor(
        get("/file/id")
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody(fromResource("binding-tariff-filestore_upload-response.json"))
          )
      )


      await(
        connector.get(FileAttachment("id", "name", "type", 0))(withHeaderCarrier("X-Api-Token", appConfig.apiToken))
      ) shouldBe FilestoreResponse(
        id = "id",
        fileName = "file-name.txt",
        mimeType = "text/plain"
      )


      verify(
        getRequestedFor(urlEqualTo("/file/id"))
          .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
      )
    }

    "Connector 'GET' one" should {
      "handle 404" in {
        val att = mock[Attachment]
        given(att.id) willReturn "id"

        stubFor(
          get("/file/id")
            .willReturn(aResponse().withStatus(Status.NOT_FOUND))
        )

        await(connector.get(att)(withHeaderCarrier("X-Api-Token", appConfig.apiToken))) shouldBe None

        verify(
          getRequestedFor(urlEqualTo("/file/id"))
            .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
        )
      }

      "handle response with mandatory fields only" in {
        val att = mock[Attachment]
        given(att.id) willReturn "id"

        stubFor(
          get("/file/id")
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(fromResource("single-file-response.json"))
            )
        )

        await(
          connector.get(att)(withHeaderCarrier("X-Api-Token", appConfig.apiToken))
        ) shouldBe Some(
          FilestoreResponse(
            id = "id",
            fileName = "name",
            mimeType = "text/plain",
            url = None,
            scanStatus = None
          )
        )

        verify(
          getRequestedFor(urlEqualTo("/file/id"))
            .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
        )
      }
    }

    "Publish" in {
      stubFor(
        post("/file/id/publish")
          .willReturn(
            aResponse()
              .withStatus(Status.ACCEPTED)
              .withBody(fromResource("binding-tariff-filestore_upload-response.json"))
          )
      )

      await(
        connector.publish(FileAttachment("id", "name", "type", 0))(withHeaderCarrier("X-Api-Token", appConfig.apiToken))
      ) shouldBe FilestoreResponse(
        id = "id",
        fileName = "file-name.txt",
        mimeType = "text/plain"
      )

      verify(
        getRequestedFor(urlEqualTo("/file/id"))
          .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
      )
    }

    "Get FileMetadata" in {
      stubFor(
        get("/file?id=id1&id=id2")
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody(fromResource("binding-tariff-filestore_filemetadata-response.json"))
          )
      )

      await(
        connector.getFileMetadata(Seq(Attachment("id1", false), Attachment("id2", false)))(withHeaderCarrier("X-Api-Token", appConfig.apiToken))
      ) shouldBe Seq(
        FilestoreResponse(
          id = "id1",
          fileName = "file-name1.txt",
          mimeType = "text/plain"),
        FilestoreResponse(
          id = "id2",
          fileName = "file-name2.txt",
          mimeType = "text/plain")
      )

      verify(
        getRequestedFor(urlEqualTo("/file?id=id1&id=id2"))
          .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
      )
    }

    "Get FileMetadata returns no data" in {
      await(connector.getFileMetadata(Seq.empty)) shouldBe Seq.empty
    }

  }

}
