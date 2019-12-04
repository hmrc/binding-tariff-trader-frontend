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

package connectors

import config.FrontendAppConfig
import models.{ApplicationSubmittedEmail, ApplicationSubmittedParameters, Email}
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.{any, eq => equality}
import org.mockito.Mockito.{times, verify, when}
import uk.gov.hmrc.http.{BadGatewayException, HeaderCarrier, HttpReads, Upstream5xxResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import play.api.libs.json.{JsObject, Writes}

import scala.concurrent.{ExecutionContext, Future}

class EmailConnectorSpec extends WordSpec with ScalaFutures with MockitoSugar with ResourceFiles with MustMatchers {

  "Connector 'Send'" should {
    val appConfig = mock[FrontendAppConfig]
    when(appConfig.emailUrl).thenReturn("EmailService")
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val email = ApplicationSubmittedEmail(Seq("user@domain.com"), ApplicationSubmittedParameters("ref", "name"))


    "POST Email payload" in {
      val httpClient = mock[HttpClient]

      val ongoingStubbingForPOSTCall =  when(httpClient.POST(
        any[String],
        any[ApplicationSubmittedEmail],
        any[Seq[(String, String)]]
      )(
        any[Writes[ApplicationSubmittedEmail]],
        any[HttpReads[JsObject]],
        any[HeaderCarrier],
        any[ExecutionContext]))

      ongoingStubbingForPOSTCall.thenReturn(Future.successful(JsObject.empty))
      val SUT = new EmailConnector(appConfig, httpClient)

      whenReady(SUT.send(email)) { _ =>
        verify(httpClient, times(1)).POST(
          equality("EmailService/hmrc/email"),
          any[ApplicationSubmittedEmail],
          any[Seq[(String, String)]]
        )(
          any[Writes[ApplicationSubmittedEmail]],
          any[HttpReads[JsObject]],
          any[HeaderCarrier],
          any[ExecutionContext])
      }
    }

    "propogate errors" in {

      val httpClient = mock[HttpClient]

      val ongoingStubbingForPOSTCall =  when(httpClient.POST(
        any[String],
        any[ApplicationSubmittedEmail],
        any[Seq[(String, String)]]
      )(
        any[Writes[ApplicationSubmittedEmail]],
        any[HttpReads[JsObject]],
        any[HeaderCarrier],
        any[ExecutionContext]))

      ongoingStubbingForPOSTCall.thenReturn(Future.failed(Upstream5xxResponse("Test Exception", 500, 500)))
      val SUT = new EmailConnector(appConfig, httpClient)

      whenReady(SUT.send(email).failed){ ex =>
        ex mustBe a[Upstream5xxResponse]
      }
    }
  }
}
