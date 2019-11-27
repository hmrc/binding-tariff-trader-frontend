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

package controllers

import java.io.File
import java.nio.file.Files

import config.FrontendAppConfig
import org.mockito.BDDMockito.given
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mockito.MockitoSugar
import play.api.i18n.MessagesApi
import play.api.inject.DefaultApplicationLifecycle
import play.api.libs.Files.{DefaultTemporaryFileCreator, DefaultTemporaryFileReaper, SingletonTemporaryFileCreator, TemporaryFile}
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import play.api.test.FakeRequest
import play.inject.ApplicationLifecycle

class FileValidatorSpec extends WordSpec with Matchers with MockitoSugar  {
  private val configuration = mock[FrontendAppConfig]
  private val messagesApi = mock[MessagesApi]
  private val fakeRequest = FakeRequest()
  private val fileSizeSmall = 10
  private val fileSizeMax = 1000
  private val fileSizeLarge = 1100

  private def createFileOfType(extension: String, mimeType: String) = {
    createFile(extension, mimeType, fileSizeSmall)
  }

  private def createFile(extension: String, mimeType: String, numBytes: Int) = {
    val filename = "example." + extension
    val file = createTemporaryFile(numBytes)
    val filePart = FilePart[TemporaryFile](key = "file-key", filename, contentType = Some(mimeType), ref = file)
    val form = MultipartFormData[TemporaryFile](dataParts = Map(), files = Seq(filePart), badParts = Seq.empty)
    form.file("file-key").get
  }

  private def createTemporaryFile(size: Int): TemporaryFile = {
    val temporaryFile = SingletonTemporaryFileCreator.create("test-", ".tmp")
    temporaryFile.deleteOnExit()
    Files.write(temporaryFile.toPath, Array.fill(size)('A'.toByte))
    temporaryFile
  }

  private def createFileOfSize(numBytes: Int) = {
    createFile("txt", "text/plain", numBytes)
  }

  "Validate file type" should {


    given(configuration.fileUploadMimeTypes).willReturn(Set("text/plain", "application/pdf", "image/png"))

    "Allow a valid text file" in {
      val file = createFileOfType("txt", "text/plain")
      val SUT = new FileValidator(configuration, messagesApi)
      SUT.validateFile(file, fakeRequest) shouldBe Right(file)
    }

    "Allow a valid pdf file" in {
      val file = createFileOfType("pdf", "application/pdf")
      val SUT = new FileValidator(configuration, messagesApi)
      SUT.validateFile(file, fakeRequest) shouldBe Right(file)
    }

    "Allow a valid png image file" in {
      val file = createFileOfType("xls", "image/png")
      val SUT = new FileValidator(configuration, messagesApi)
      SUT.validateFile(file, fakeRequest) shouldBe Right(file)
    }

    "Reject invalid file type" in {
      val file = createFileOfType("mp3", "audio/mpeg")
      given(messagesApi.apply("uploadWrittenAuthorisation.error.fileType")).willReturn("some error message")
      val SUT = new FileValidator(configuration, messagesApi)
      SUT.validateFile(file, fakeRequest) shouldBe Left("some error message")
    }

  }

  "Validate file size" should {

    given(configuration.fileUploadMaxSize).willReturn(fileSizeMax)

    "Allow a small file" in {
      val file = createFileOfSize(fileSizeSmall)
      val SUT = new FileValidator(configuration, messagesApi)
      SUT.validateFile(file, fakeRequest) shouldBe Right(file)
    }

    "Reject large file" in {
      val file = createFileOfSize(fileSizeLarge)
      given(messagesApi.apply("uploadWrittenAuthorisation.error.size")).willReturn("some error message")
      val SUT = new FileValidator(configuration, messagesApi)
      SUT.validateFile(file, fakeRequest) shouldBe Left("some error message")
    }
  }
}
