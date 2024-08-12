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

import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.fop.apps.{FOUserAgent, Fop, FopFactory}
import org.apache.xmlgraphics.util.MimeConstants
import play.api.{Environment, Logging}
import play.twirl.api.Html

import java.io.{File, StringReader}
import javax.inject.{Inject, Singleton}
import javax.xml.transform.sax.{SAXResult, SAXTransformerFactory}
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.{Source, TransformerFactory, URIResolver}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Using

@Singleton
class PdfGeneratorService @Inject() (fopFactory: FopFactory, environment: Environment)(implicit ec: ExecutionContext)
    extends Logging
    with URIResolver {

  override def resolve(href: String, base: String): Source = {
    val pathForEnv = href.replace("*/", "")

    val file: File = if (href.startsWith("*/")) {
      Option(new File(s"${environment.rootPath.toURI.getPath}conf/$pathForEnv"))
        .filter(_.exists)
        .getOrElse(new File(s"app/views/components/fop/$pathForEnv"))
    } else {
      new File(href)
    }

    new StreamSource(file)
  }

  def render(input: Html, xlsTransformer: String): Future[Array[Byte]] = Future {
    Using.resource(new ByteArrayOutputStream()) { out =>
      val userAgent: FOUserAgent = fopFactory.newFOUserAgent()
      userAgent.setAccessibility(true)

      val xslt: StreamSource = new StreamSource(new StringReader(xlsTransformer))
      val fop: Fop           = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, out)

      val view             = input.toString()
      val viewWithHtmlTags = "<html>" + view + "</html>"

      try {
        val source: StreamSource = new StreamSource(new StringReader(viewWithHtmlTags))
        val result               = new SAXResult(fop.getDefaultHandler)

        val transformerFactory = TransformerFactory.newInstance().asInstanceOf[SAXTransformerFactory]
        transformerFactory.setURIResolver(this)
        val transformer = transformerFactory.newTransformer(xslt)
        transformer.transform(source, result)

      } catch {
        case e: Exception =>
          throw new Exception(s"[PdfGeneratorService][render] Error rendering PDF: ${e.getMessage}")
      }

      out.toByteArray
    }
  }
}
