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
import models.oCase
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.twirl.api.Html
import viewmodels.PdfViewModel
import views.html.components.view_application

import java.nio.file.{Files, Paths}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

class PdfGeneratorServiceSpec extends SpecBase with ScalaFutures with IntegrationPatience {

  private val pdfGeneratorService: PdfGeneratorService = new PdfGeneratorService

  private val pdfViewModel: PdfViewModel          = oCase.pdf
  private val pdfViewNoSamplesModel: PdfViewModel = oCase.pdfNoSamples
  private val hazardousSamplePdf: PdfViewModel    = pdfViewModel.copy(hazardousSample = true)

  private val xlsTransformer = Source.fromResource("view_application.xml").mkString

  "PDFGeneratorService" must {
    def test(
      pdfType: String,
      pdfViewModel: PdfViewModel,
      visibleContent: Seq[String],
      hiddenContent: Seq[String]
    ): Unit = {
      s"render a PDF $pdfType" in {
        val pdfContent: Html = view_application(frontendAppConfig, pdfViewModel, _ => Some("country"))(messages)

        val result = pdfGeneratorService.render(pdfContent, xlsTransformer).futureValue

        val fileName = s"test/resources/fop/$pdfType-test.pdf"
        Files.write(Paths.get(fileName), result)
      }

      s"generate a valid $pdfType PDF file" in {

        val file: Array[Byte]    = Files.readAllBytes(Paths.get(s"test/resources/fop/$pdfType-test.pdf"))
        val document: PDDocument = PDDocument.load(file)

        try {
          val textStripper: PDFTextStripper = new PDFTextStripper
          val text: String                  = textStripper.getText(document)
          val lines: List[String]           = text.split("\n").toList.map(_.trim)

          lines(2) shouldBe "Your Advance Tariff Ruling application"
          lines    should contain allElementsOf visibleContent
          lines shouldNot contain allElementsOf hiddenContent
        } finally {
          document.close()
        }

      }
    }

    val headings: Seq[String] = Seq("For your records", "What happens next", "About the goods")

    val input: Seq[(String, PdfViewModel, Seq[String], Seq[String])] = Seq(
      (
        "withSamples",
        pdfViewModel,
        headings ++ Seq("Sending samples", "Send your samples to"),
        Seq("Hazardous samples? Yes")
      ),
      ("withoutSamples", pdfViewNoSamplesModel, headings ++ Seq("Sending samples? No"), Seq("Sending samples")),
      (
        "withHazardousSamples",
        hazardousSamplePdf,
        headings ++ Seq("Sending samples", "Hazardous samples? Yes"),
        Seq("Send your samples to")
      )
    )

    input.foreach(args => (test _).tupled(args))
  }

  "log an error when provided HTML is invalid and the PDF cannot be generated" in {

    val result = intercept[Exception] {
      pdfGeneratorService.render(Html("invalid html"), xlsTransformer).futureValue
    }

    result.getMessage should include("Error rendering PDF")
    result.getMessage should include("is missing child elements")

  }

}
