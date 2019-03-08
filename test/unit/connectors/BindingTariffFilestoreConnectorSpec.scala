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
import models.{Attachment, FileAttachment}
import models.response.FilestoreResponse
import play.api.http.Status
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData

class BindingTariffFilestoreConnectorSpec extends ConnectorTest {

  private val connector = new BindingTariffFilestoreConnector(appConfig, wsClient, authenticatedHttpClient)

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

      val file = MultipartFormData.FilePart[TemporaryFile]("file", "file-name", Some("text/plain"), TemporaryFile("file-name.txt"))

      await(connector.upload(file)) shouldBe FilestoreResponse(
        id = "id",
        fileName = "file-name.txt",
        mimeType = "text/plain"
      )

      verify(
        postRequestedFor(urlEqualTo("/file"))
          .withHeader("X-Api-Token", equalTo(appConfig.apiToken))
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

      await(connector.get(FileAttachment("id", "name", "type", 0))) shouldBe FilestoreResponse(
        id = "id",
        fileName = "file-name.txt",
        mimeType = "text/plain"
      )


      verify(
        getRequestedFor(urlEqualTo("/file/id"))
          .withHeader("X-Api-Token", equalTo(realConfig.apiToken))
      )
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

      await(connector.get(FileAttachment("id", "name", "type", 0))) shouldBe FilestoreResponse(
        id = "id",
        fileName = "file-name.txt",
        mimeType = "text/plain"
      )

      verify(
        getRequestedFor(urlEqualTo("/file/id"))
          .withHeader("X-Api-Token", equalTo(realConfig.apiToken))
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

      await(connector.getFileMetadata(Seq(Attachment("id1"), Attachment("id2")))) shouldBe Seq(
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

    "Get FileMetadata returns no data" in {
      await(connector.getFileMetadata(Seq.empty)) shouldBe Seq.empty
    }

  }

}
