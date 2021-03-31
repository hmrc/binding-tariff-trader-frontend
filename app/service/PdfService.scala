/*
 * Copyright 2021 HM Revenue & Customs
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

import connectors.PdfGeneratorServiceConnector
import javax.inject.{Inject, Singleton}
import models.PdfFile
import org.apache.commons.codec.binary.Base64
import play.api.{Logger, Logging}
import play.twirl.api.Html
import uk.gov.hmrc.crypto.{CompositeSymmetricCrypto, Crypted, PlainText}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class PdfService @Inject()(connector: PdfGeneratorServiceConnector, crypto: CompositeSymmetricCrypto) extends Logging{

  private def encrypt(string: String): String = crypto.encrypt(PlainText(string)).value

  private def encode(string: String): String = Base64.encodeBase64String(string.getBytes)

  private def decrypt(string: String): String = crypto.decrypt(Crypted(string)).value

  private def decode(string: String): String = new String(Base64.decodeBase64(string.getBytes))

  def encodeToken(eori: String): String = {
    encode(encrypt(eori))
  }

  def decodeToken(token: String): Option[String] = {
    Try(decrypt(decode(token))) match {
      case Success(eori) if eori.nonEmpty =>
        Some(eori)

      case Failure(error) =>
        Logger.debug("Bad Token", error)
        None

      case _ =>
        None
    }
  }

  def generatePdf(htmlContent: Html): Future[PdfFile] = {
    val pdf = connector.generatePdf(htmlContent)
    pdf
  }

}
