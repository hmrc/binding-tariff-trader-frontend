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

package connectors

import java.nio.charset.StandardCharsets

import akka.util.ByteString
import com.github.tomakehurst.wiremock.client.WireMock._
import models.response.FilestoreResponse
import models.{Attachment, FileAttachment}
import org.mockito.BDDMockito.given
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import models.requests.FileStoreInitiateRequest
import models.response.FileStoreInitiateResponse
import models.response.UpscanFormTemplate
import unit.base.WireMockObject.wireMockUrl

import scala.concurrent.Future

class BindingTariffFilestoreConnectorSpec extends ConnectorTest {

  private def connector =
    new BindingTariffFilestoreConnector(wsClient, authenticatedHttpClient, metrics)(mockConfig, implicitly)

  private def withHeaderCarrier(value: String): HeaderCarrier =
    HeaderCarrier(extraHeaders = Seq("X-Api-Token" -> value))

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
        id              = "id",
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
        connector.get(FileAttachment("id", "name", "type", 0))(withHeaderCarrier(appConfig.apiToken))
      ) shouldBe FilestoreResponse(
        id       = "id",
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

        await(connector.get(att)(withHeaderCarrier(appConfig.apiToken))) shouldBe None

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
          connector.get(att)(withHeaderCarrier(appConfig.apiToken))
        ) shouldBe Some(
          FilestoreResponse(
            id         = "id",
            fileName   = "name",
            mimeType   = "text/plain",
            url        = None,
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
        connector.publish(FileAttachment("id", "name", "type", 0))(withHeaderCarrier(appConfig.apiToken))
      ) shouldBe FilestoreResponse(
        id       = "id",
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
        connector.getFileMetadata(Seq(Attachment(id = "id1", public = false), Attachment(id = "id2", public = false)))(
          withHeaderCarrier(appConfig.apiToken)
        )
      ) shouldBe Seq(
        FilestoreResponse(id = "id1", fileName = "file-name1.txt", mimeType = "text/plain"),
        FilestoreResponse(id = "id2", fileName = "file-name2.txt", mimeType = "text/plain")
      )

      verify(
        getRequestedFor(urlEqualTo("/file?id=id1&id=id2"))
          .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
      )
    }

    "Get FileMetadata returns no data" in {
      await(connector.getFileMetadata(Seq.empty)) shouldBe Seq.empty
    }

    "Upload Application PDF" in {
      stubFor(
        post("/file")
          .willReturn(
            aResponse()
              .withStatus(Status.ACCEPTED)
              .withBody(fromResource("binding-tariff-filestore_upload-response.json"))
          )
      )

      await(connector.uploadApplicationPdf("ref", "content".getBytes(StandardCharsets.UTF_8))) shouldBe
        FilestoreResponse("id", "file-name.txt", "text/plain")

      verify(
        postRequestedFor(urlEqualTo("/file"))
          .withHeader("X-Api-Token", equalTo(mockConfig.apiToken))
      )
    }

    "Download file from a URL" in {
      val content = "content".getBytes(StandardCharsets.UTF_8)

      stubFor(
        get(urlEqualTo("/digital-tariffs-local/a1e8057e-fbbc-47a8-a8b4-78d9f015c253"))
          .willReturn(
            aResponse()
              .withBody(content)
          )
      )

      await(
        connector
          .downloadFile(s"$wireMockUrl/digital-tariffs-local/a1e8057e-fbbc-47a8-a8b4-78d9f015c253")
          .flatMap(maybeSource =>
            maybeSource.fold(Future.successful(ByteString.empty)) { source =>
              source.runFold(ByteString.empty) {
                case (bytes, nextBytes) => bytes ++ nextBytes
              }
            }
          )
      ) shouldBe ByteString(content)
    }

    "Fail to download file from a URL" in {

      stubFor(
        get(urlEqualTo("/digital-tariffs-local/717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"))
          .willReturn(
            aResponse()
              .withStatus(Status.INTERNAL_SERVER_ERROR)
          )
      )

      intercept[RuntimeException] {
        await(connector.downloadFile(s"$wireMockUrl/digital-tariffs-local/717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"))
      }.getMessage shouldBe "Unable to retrieve file from filestore"
    }

    "Handle 404" when {
      "downloading a file from a URL" in {

        stubFor(
          get(urlEqualTo("/digital-tariffs-local/c432a56d-e811-474c-a26a-76fc3bcaefe5"))
            .willReturn(
              aResponse()
                .withStatus(Status.NOT_FOUND)
            )
        )

        await(connector.downloadFile(s"$wireMockUrl/digital-tariffs-local/c432a56d-e811-474c-a26a-76fc3bcaefe5")) shouldBe None
      }
    }
  }
}
