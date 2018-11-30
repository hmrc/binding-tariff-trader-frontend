/*
 * Copyright 2018 HM Revenue & Customs
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

package service

import connectors.BindingTariffFilestoreConnector
import models.FileAttachment
import models.response.FilestoreResponse
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class FileServiceSpec extends UnitSpec with MockitoSugar {

  private val connector = mock[BindingTariffFilestoreConnector]
  private val service = new FileService(connector)
  private implicit val headers: HeaderCarrier = HeaderCarrier()

  "Upload" should {
    val fileUploading = MultipartFormData.FilePart[TemporaryFile]("key", "filename", Some("type"), TemporaryFile())
    val connectorResponse = FilestoreResponse("id", "filename-updated", "type")
    val fileUploaded = FileAttachment("id", "filename-updated", 0)

    "Delegate to connector" in {
      given(connector.upload(refEq(fileUploading))(any[HeaderCarrier])).willReturn(Future.successful(connectorResponse))

      await(service.upload(fileUploading)) shouldBe fileUploaded
    }
  }

  "Refresh" should {
    val outdatedFile = FileAttachment("id", "filename", 0)
    val connectorResponse = FilestoreResponse("id", "filename-updated", "type")
    val fileUpdated = FileAttachment("id", "filename-updated", 0)

    "Delegate to connector" in {
      given(connector.get(refEq(outdatedFile))(any[HeaderCarrier])).willReturn(Future.successful(connectorResponse))

      await(service.refresh(outdatedFile)) shouldBe fileUpdated
    }
  }

  "Publish" should {
    val filePublishing = FileAttachment("id", "filename", 0)
    val connectorResponse = FilestoreResponse("id", "filename-updated", "type")
    val filePublished = FileAttachment("id", "filename-updated", 0)

    "Delegate to connector" in {
      given(connector.publish(refEq(filePublishing))(any[HeaderCarrier])).willReturn(Future.successful(connectorResponse))

      await(service.publish(filePublishing)) shouldBe filePublished
    }
  }

}
