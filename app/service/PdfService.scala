/*
 * Copyright 2026 HM Revenue & Customs
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
import org.apache.commons.codec.binary.Base64
import play.api.Logging
import play.twirl.api.Html
import uk.gov.hmrc.crypto.{AesCrypto, Crypted, PlainText}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.io.Source
import scala.util.{Failure, Success, Try}

@Singleton
class PdfService @Inject() (
  pdfGeneratorService: PdfGeneratorService,
  appConfig: FrontendAppConfig
) extends Logging
    with AesCrypto {

  override protected val encryptionKey: String = appConfig.aesKey

  private def encrypt(string: String): String = encrypt(PlainText(string)).value

  private def encode(string: String): String = Base64.encodeBase64String(string.getBytes)

  private def decrypt(string: String): String = decrypt(Crypted(string)).value

  private def decode(string: String): String = new String(Base64.decodeBase64(string.getBytes))

  def encodeToken(eori: String): String =
    encode(encrypt(eori))

  def decodeToken(token: String): Option[String] =
    Try(decrypt(decode(token))) match {
      case Success(eori) if eori.nonEmpty =>
        Some(eori)

      case Failure(error) =>
        logger.debug("[PdfService][decodeToken] Bad Token", error)
        None

      case _ =>
        None
    }

  def generatePdf(htmlContent: Html): Future[Array[Byte]] = {
    val xlsTransformer = Source.fromResource("view_application.xml").mkString
    pdfGeneratorService.render(htmlContent, xlsTransformer)
  }

}
