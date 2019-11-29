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
import models.response.FilestoreResponse
import models.{Attachment, FileAttachment}
import org.mockito.BDDMockito.given
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status
import play.api.libs.Files.{DefaultTemporaryFileCreator, TemporaryFile}
import play.api.mvc.MultipartFormData
import play.libs.Files.TemporaryFileCreator

class BindingTariffFilestoreConnectorSpec extends ConnectorTest with ScalaFutures {

  private val connector = new BindingTariffFilestoreConnector(appConfig, wsClient, standardHttpClient)

  val temporaryFileCreator = app.injector.instanceOf[DefaultTemporaryFileCreator]


  "Connector" should {

    "Upload" in {
      stubFor(
        post("/file")
          .willReturn(
            aResponse()
              .withStatus(Status.ACCEPTED)
              .withBody(fromResource("binding-tariff-filestore_upload-response.json"))
          )
      )

      val file = MultipartFormData.FilePart[TemporaryFile]("file", "file-name", Some("text/plain"), temporaryFileCreator.create("file-name.txt"))

      whenReady(connector.upload(file)) { response =>
        response mustBe FilestoreResponse(
          id = "id",
          fileName = "file-name.txt",
          mimeType = "text/plain"
        )

        verify(
          postRequestedFor(urlEqualTo("/file"))
            .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
        )
      }
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

      whenReady(connector.get(FileAttachment("id", "name", "type", 0))) { result =>
        result mustBe FilestoreResponse(

          id = "id",
          fileName = "file-name.txt",
          mimeType = "text/plain"
        )


        verify(
          getRequestedFor(urlEqualTo("/file/id"))
            .withHeader("X-Api-Token", equalTo(realConfig.apiToken))
        )
      }
    }

    "Connector 'GET' one" should {
      "handle 404" in {
        val att = mock[Attachment]
        given(att.id) willReturn "id"

        stubFor(
          get("/file/id")
            .willReturn(aResponse().withStatus(Status.NOT_FOUND))
        )

        whenReady(connector.get(att)) { result =>
          result mustBe None

          verify(
            getRequestedFor(urlEqualTo("/file/id"))
              .withHeader("X-Api-Token", equalTo(realConfig.apiToken))
          )
        }
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

        whenReady(connector.get(att)) { result =>
          result mustBe Some(
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
              .withHeader("X-Api-Token", equalTo(realConfig.apiToken))
          )
        }
      }
    }

    "Publish" in {
      stubFor(
        get("/file/id")
          .willReturn(
            aResponse()
              .withStatus(Status.ACCEPTED)
              .withBody(fromResource("binding-tariff-filestore_upload-response.json"))
          )
      )

      whenReady(connector.get(FileAttachment("id", "name", "type", 0))) { result =>
        result mustBe FilestoreResponse(
          id = "id",
          fileName = "file-name.txt",
          mimeType = "text/plain"
        )

        verify(
          getRequestedFor(urlEqualTo("/file/id"))
            .withHeader("X-Api-Token", equalTo(realConfig.apiToken))
        )
      }
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

      whenReady(connector.getFileMetadata(Seq(Attachment("id1"), Attachment("id2")))) { result =>
        result mustBe Seq(
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
            .withHeader("X-Api-Token", equalTo(realConfig.apiToken))
        )
      }
    }

    "Get FileMetadata returns no data" in {
      whenReady(connector.getFileMetadata(Seq.empty)) { result =>
        result mustBe Seq.empty
      }
    }
  }
}
