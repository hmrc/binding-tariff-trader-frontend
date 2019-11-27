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

package service

import config.FrontendAppConfig
import connectors.BindingTariffFilestoreConnector
import models._
import models.response.FilestoreResponse
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import play.api.i18n.MessagesApi
import play.api.libs.Files.{SingletonTemporaryFileCreator, TemporaryFile}
import play.api.mvc.MultipartFormData
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future.{failed, successful}

class FileServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  private val connector = mock[BindingTariffFilestoreConnector]
  private val messagesApi = mock[MessagesApi]
  private val configuration = mock[FrontendAppConfig]

  private val service = new FileService(connector, messagesApi, configuration)
  private implicit val headers: HeaderCarrier = HeaderCarrier()

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(connector)
  }

  "Upload" should {
    val fileUploading = MultipartFormData.FilePart[TemporaryFile]("key", "filename", Some("type"), SingletonTemporaryFileCreator.create("A", "B"))
    val connectorResponse = FilestoreResponse("id", "filename-updated", "type")
    val fileUploaded = FileAttachment("id", "filename-updated", "type", 0)

    "Delegate to connector" in {
      given(connector.upload(refEq(fileUploading))(any[HeaderCarrier])).willReturn(successful(connectorResponse))

      await(service.upload(fileUploading)) shouldBe fileUploaded
    }
  }

  "Refresh" should {
    val outdatedFile = FileAttachment("id", "filename", "type", 0)
    val connectorResponse = FilestoreResponse("id", "filename-updated", "type")
    val fileUpdated = FileAttachment("id", "filename-updated", "type", 0)

    "Delegate to connector" in {
      given(connector.get(refEq(outdatedFile))(any[HeaderCarrier])).willReturn(successful(connectorResponse))

      await(service.refresh(outdatedFile)) shouldBe fileUpdated
    }
  }

  "Publish" should {
    val filePublishing = FileAttachment("id", "filename", "type", 0)

    "Delegate to connector and return Published" in {
      val connectorResponse = FilestoreResponse("id", "filename-updated", "type", scanStatus = Some(ScanStatus.READY), url = Some("url"))
      given(connector.get(filePublishing)).willReturn(connectorResponse)
      given(connector.publish(refEq(filePublishing))(any[HeaderCarrier])).willReturn(successful(connectorResponse))

      await(service.publish(filePublishing)) shouldBe PublishedFileAttachment("id", "filename-updated", "type", 0)
    }
  }

  "Service 'getLetterOfAuthority'" should {

    "Return Stored Attachment" in {
      val c = aCase(withLetterOfAuthWithId("1"))
      val expectedAtt = anAttachmentWithId("1")

      given(connector.get(any[Attachment])(any[HeaderCarrier])) willReturn successful(Some(someMetadataWithId("1")))

      await(service.getLetterOfAuthority(c)) shouldBe Some(someMetadataWithId("1"))
    }

    "Return None for missing letter" in {
      await(service.getLetterOfAuthority(aCase(withAgentDetails()))) shouldBe None
    }

    "Return None for missing agent" in {
      await(service.getLetterOfAuthority(aCase(withoutAgentDetails()))) shouldBe None
    }
  }

  "Publish multiple" should {
    val filePublishing1 = FileAttachment("id1", "filename1", "type", 0)
    val filePublishing2 = FileAttachment("id2", "filename2", "type", 0)

    "Handle Exceptions from Publish" in {
      val connectorResponse2 = FilestoreResponse("id2", "filename2", "type", scanStatus = Some(ScanStatus.READY), url = Some("url"))

      given(connector.publish(refEq(filePublishing1))(any[HeaderCarrier])).willReturn(failed(new RuntimeException("Some Error")))
      given(connector.publish(refEq(filePublishing2))(any[HeaderCarrier])).willReturn(successful(connectorResponse2))

      await(service.publish(Seq(filePublishing1, filePublishing2))) shouldBe Seq(
        PublishedFileAttachment("id2", "filename2", "type", 0)
      )
    }

    "Handle Exceptions from Publish where all fail" in {
      given(connector.publish(refEq(filePublishing1))(any[HeaderCarrier])).willReturn(failed(new RuntimeException("Some Error")))
      given(connector.publish(refEq(filePublishing2))(any[HeaderCarrier])).willReturn(failed(new RuntimeException("Some Error")))

      await(service.publish(Seq(filePublishing1, filePublishing2))) shouldBe Seq.empty
    }
  }

  "GetAttachmentMetadata" should {
    val c: Case = mock[Case]
    val connectorResponse = Seq(FilestoreResponse("id", "filename-updated", "type"))

    "Delegate to connector" in {
      given(connector.getFileMetadata(any[Seq[Attachment]])(any[HeaderCarrier])).willReturn(successful(connectorResponse))

      await(service.getAttachmentMetadata(c)) shouldBe connectorResponse
    }
  }

  private def aCase(modifiers: (Case => Case)*): Case = {
    var c = oCase.btiCaseExample
    modifiers.foreach(m => c = m(c))
    c
  }

  private def withAgentDetails(): Case => Case = c => {
    val details = AgentDetails(mock[EORIDetails], None)
    val app = c.application.copy(agent = Some(details))
    c.copy(application = app)
  }

  private def withoutAgentDetails(): Case => Case = c => {
    val app = c.application.copy(agent = None)
    c.copy(application = app)
  }

  private def withLetterOfAuthWithId(id: String): Case => Case = c => {
    val details = AgentDetails(mock[EORIDetails], Some(anAttachmentWithId(id)))
    val app = c.application.copy(agent = Some(details))
    c.copy(application = app)
  }

  private def anAttachmentWithId(id: String): Attachment = {
    Attachment(
      id = id,
      public = true
    )
  }

  private def someMetadataWithId(id: String): FilestoreResponse = {
    FilestoreResponse(
      id = id,
      fileName = s"name-$id",
      mimeType = s"type-$id",
      url = Some(s"url-$id"),
      scanStatus = Some(ScanStatus.READY)
    )
  }
}
