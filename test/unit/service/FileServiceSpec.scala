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

package service

import java.io.File
import java.nio.file.Files

import base.SpecBase
import config.FrontendAppConfig
import connectors.BindingTariffFilestoreConnector
import models._
import models.response.FilestoreResponse
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfterEach
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future.{failed, successful}
import models.requests.FileStoreInitiateRequest
import models.response.FileStoreInitiateResponse
import models.response.UpscanFormTemplate

class FileServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val connector = mock[BindingTariffFilestoreConnector]
  private val configuration = mock[FrontendAppConfig]

  private val fileSizeSmall = 10
  private val fileSizeMax = 1000
  private val fileSizeLarge = 1100

  private val service = new FileService(connector, cc, configuration)
  private implicit val headers: HeaderCarrier = HeaderCarrier()

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(connector)
  }

  private def createFileOfType(extension: String, mimeType: String) = {
    createFile(extension, mimeType, fileSizeSmall)
  }

  "Initiate" should {
    val initiateRequest = FileStoreInitiateRequest()

    val initiateResponse = FileStoreInitiateResponse(
      id = "id",
      upscanReference = "ref",
      uploadRequest = UpscanFormTemplate(
        "http://localhost:20001/upscan/upload",
        Map("key" -> "value")
      )
    )

    "Delegate to connector" in {
      given(connector.initiate(initiateRequest)).willReturn(successful(initiateResponse))

      await(service.initiate(initiateRequest)) shouldBe initiateResponse
    }
  }

  "Upload" should {
    val fileUploading = MultipartFormData.FilePart[TemporaryFile]("key", "filename", Some("type"), tempFileCreator.create())
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
      val expectedAtt = Some(someMetadataWithId("1"))

      given(connector.get(any[Attachment])(any[HeaderCarrier])) willReturn successful(expectedAtt)

      await(service.getLetterOfAuthority(c)) shouldBe expectedAtt
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


  "Validate file type" should {

    given(configuration.fileUploadMimeTypes).willReturn(Set("text/plain", "application/pdf", "image/png"))

    "Allow a valid text file" in {
      val file = createFileOfType("txt", "text/plain")
      service.validate(file) shouldBe Right(file)
    }

    "Allow a valid pdf file" in {
      val file = createFileOfType("pdf", "application/pdf")
      service.validate(file) shouldBe Right(file)
    }

    "Allow a valid png image file" in {
      val file = createFileOfType("xls", "image/png")
      service.validate(file) shouldBe Right(file)
    }

    "Reject invalid file type" in {
      val file = createFileOfType("invalid", "audio/mpeg")
      service.validate(file) shouldBe Left(messagesApi("uploadWrittenAuthorisation.error.fileType"))
    }

  }

  "Validate file size" should {

    given(configuration.fileUploadMaxSize).willReturn(fileSizeMax)

    "Allow a small file" in {
      val file = createFileOfSize(fileSizeSmall)
      service.validate(file) shouldBe Right(file)
    }

    "Reject large file" in {
      val file = createFileOfSize(fileSizeLarge)
      service.validate(file) shouldBe Left(messagesApi("uploadWrittenAuthorisation.error.size"))
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

  private def createFileOfSize(numBytes: Int) = {
    createFile("txt", "text/plain", numBytes)
  }

  private def createFile(extension: String, mimeType: String, numBytes: Int) = {
    val filename = "example." + extension
    val tempFile = createTemporaryFile(numBytes).toPath
    val file = tempFileCreator.create(tempFile)
    val filePart = FilePart[TemporaryFile](key = "file-key", filename, contentType = Some(mimeType), ref = file)
    val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq(filePart), badParts = Seq.empty)
    form.file("file-key").get
  }

  private def createTemporaryFile(size: Int): File = {
    val temporaryFile: File = File.createTempFile("test-", ".tmp")
    temporaryFile.deleteOnExit()
    Files.write(temporaryFile.toPath, Array.fill(size)('A'.toByte))
    temporaryFile
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
