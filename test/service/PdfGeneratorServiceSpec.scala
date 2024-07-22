/*
 * Copyright 2024 HM Revenue & Customs
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

import base.SpecBase
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

import java.nio.file.{Files, Paths}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

class PdfGeneratorServiceSpec extends SpecBase with ScalaFutures with IntegrationPatience {
  private val pdfGeneratorService: PdfGeneratorService = new PdfGeneratorService

  "PDFGeneratorService" should {
    "have a render method" in {
      val input  = Source.fromResource("applicationView.xml").mkString
      val result = pdfGeneratorService.render(input).futureValue

      val fileName = "test/resources/fop/test.pdf"
      Files.write(Paths.get(fileName), result)
    }
    "generate a test PDF" in {

      val file: Array[Byte] = Files.readAllBytes(Paths.get("test/resources/fop/test.pdf"))
      val document: PDDocument = PDDocument.load(file)

      val textStripper: PDFTextStripper = new PDFTextStripper
      val text: String        = textStripper.getText(document)
      val lines: List[String] = text.split("\n").toList.map(_.trim)

      lines(2) shouldBe "Your Advanced Tariff Ruling application"
    }
  }

}
