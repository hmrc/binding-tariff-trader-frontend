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
import models.response.FilestoreResponse
import models.{FileAttachment, PublishedFileAttachment, ScanStatus, UnpublishedFileAttachment}
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class FileServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  private val connector = mock[BindingTariffFilestoreConnector]
  private val service = new FileService(connector)
  private implicit val headers: HeaderCarrier = HeaderCarrier()

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(connector)
  }

  "Upload" should {
    val fileUploading = MultipartFormData.FilePart[TemporaryFile]("key", "filename", Some("type"), TemporaryFile())
    val connectorResponse = FilestoreResponse("id", "filename-updated", "type")
    val fileUploaded = FileAttachment("id", "filename-updated", "type", 0)

    "Delegate to connector" in {
      given(connector.upload(refEq(fileUploading))(any[HeaderCarrier])).willReturn(Future.successful(connectorResponse))

      await(service.upload(fileUploading)) shouldBe fileUploaded
    }
  }

  "Refresh" should {
    val outdatedFile = FileAttachment("id", "filename", "type", 0)
    val connectorResponse = FilestoreResponse("id", "filename-updated", "type")
    val fileUpdated = FileAttachment("id", "filename-updated", "type", 0)

    "Delegate to connector" in {
      given(connector.get(refEq(outdatedFile))(any[HeaderCarrier])).willReturn(Future.successful(connectorResponse))

      await(service.refresh(outdatedFile)) shouldBe fileUpdated
    }
  }

  "Publish" should {
    val filePublishing = FileAttachment("id", "filename", "type", 0)

    "Delegate to connector and return Published" in {
      val connectorResponse = FilestoreResponse("id", "filename-updated", "type", scanStatus = Some(ScanStatus.READY), url = Some("url"))
      given(connector.get(filePublishing)).willReturn(connectorResponse)
      given(connector.publish(refEq(filePublishing))(any[HeaderCarrier])).willReturn(Future.successful(connectorResponse))

      await(service.publish(filePublishing)) shouldBe PublishedFileAttachment("id", "filename-updated", "type", 0, "url")
    }

    "Delegate to connector and return Unpublished" in {
      val connectorResponse = FilestoreResponse("id", "filename-updated", "type")
      given(connector.get(filePublishing)).willReturn(connectorResponse)
      given(connector.publish(refEq(filePublishing))(any[HeaderCarrier])).willReturn(Future.successful(connectorResponse))

      await(service.publish(filePublishing)) shouldBe UnpublishedFileAttachment("id", "filename-updated", "type", 0, "Unscanned")
    }

    "Delegate to connector and return Quarantined" in {
      val connectorResponse = FilestoreResponse("id", "filename-updated", "type", scanStatus = Some(ScanStatus.FAILED))
      given(connector.get(filePublishing)).willReturn(connectorResponse)
      given(connector.publish(refEq(filePublishing))(any[HeaderCarrier])).willReturn(Future.successful(connectorResponse))

      await(service.publish(filePublishing)) shouldBe UnpublishedFileAttachment("id", "filename-updated", "type", 0, "Quarantined")
    }
  }

  "Publish multiple" should {
    val filePublishing1 = FileAttachment("id1", "filename1", "type", 0)
    val filePublishing2 = FileAttachment("id2", "filename2", "type", 0)

    "Handle Exceptions from Get" in {
      val connectorResponse = FilestoreResponse("id1", "filename1", "type", scanStatus = Some(ScanStatus.READY), url = Some("url"))
      given(connector.get(filePublishing1)).willReturn(Future.successful(connectorResponse))
      given(connector.get(filePublishing2)).willReturn(Future.failed(new RuntimeException("Some Error")))

      given(connector.publish(refEq(filePublishing1))(any[HeaderCarrier])).willReturn(Future.successful(connectorResponse))

      await(service.publish(Seq(filePublishing1, filePublishing2))) shouldBe Seq(
        PublishedFileAttachment("id1", "filename1", "type", 0, "url"),
        UnpublishedFileAttachment("id2", "filename2", "type", 0, "Some Error")
      )
    }

    "Handle Exceptions from Get where all fail" in {
      given(connector.get(filePublishing1)).willReturn(Future.failed(new RuntimeException("Some Error")))
      given(connector.get(filePublishing2)).willReturn(Future.failed(new RuntimeException("Some Error")))

      await(service.publish(Seq(filePublishing1, filePublishing2))) shouldBe Seq(
        UnpublishedFileAttachment("id1", "filename1", "type", 0, "Some Error"),
        UnpublishedFileAttachment("id2", "filename2", "type", 0, "Some Error")
      )
    }

    "Handle Exceptions from Publish" in {
      val connectorResponse1 = FilestoreResponse("id1", "filename1", "type", scanStatus = Some(ScanStatus.READY), url = Some("url"))
      val connectorResponse2 = FilestoreResponse("id2", "filename2", "type", scanStatus = Some(ScanStatus.READY), url = Some("url"))
      given(connector.get(filePublishing1)).willReturn(Future.successful(connectorResponse1))
      given(connector.get(filePublishing2)).willReturn(Future.successful(connectorResponse2))

      given(connector.publish(refEq(filePublishing1))(any[HeaderCarrier])).willReturn(Future.successful(connectorResponse1))
      given(connector.publish(refEq(filePublishing2))(any[HeaderCarrier])).willReturn(Future.failed(new RuntimeException("Some Error")))

      await(service.publish(Seq(filePublishing1, filePublishing2))) shouldBe Seq(
        PublishedFileAttachment("id1", "filename1", "type", 0, "url"),
        UnpublishedFileAttachment("id2", "filename2", "type", 0, "Some Error")
      )
    }

    "Handle Exceptions from Publish where all fail" in {
      val connectorResponse1 = FilestoreResponse("id1", "filename1", "type", scanStatus = Some(ScanStatus.READY), url = Some("url"))
      val connectorResponse2 = FilestoreResponse("id2", "filename2", "type", scanStatus = Some(ScanStatus.READY), url = Some("url"))
      given(connector.get(filePublishing1)).willReturn(Future.successful(connectorResponse1))
      given(connector.get(filePublishing2)).willReturn(Future.successful(connectorResponse2))

      given(connector.publish(refEq(filePublishing1))(any[HeaderCarrier])).willReturn(Future.failed(new RuntimeException("Some Error")))
      given(connector.publish(refEq(filePublishing2))(any[HeaderCarrier])).willReturn(Future.failed(new RuntimeException("Some Error")))

      await(service.publish(Seq(filePublishing1, filePublishing2))) shouldBe Seq(
        UnpublishedFileAttachment("id1", "filename1", "type", 0, "Some Error"),
        UnpublishedFileAttachment("id2", "filename2", "type", 0, "Some Error")
      )
    }
  }
}
