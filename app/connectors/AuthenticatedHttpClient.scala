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

import akka.actor.ActorSystem
import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Writes
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthenticatedHttpClient @Inject()(auditConnector: HttpAuditing, wsClient: WSClient, actorSystem: ActorSystem)
                                       (implicit val config: FrontendAppConfig)
  extends DefaultHttpClient(config.runModeConfiguration, auditConnector, wsClient, actorSystem)
    with InjectAuthHeader {

    def doGet(url: String)
                    (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    super.doGet(url)(addAuth,ec)
  }

  override def doPost[A](url: String, body: A, headers: Seq[(String, String)])
                        (implicit rds: Writes[A], hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    super.doPost(url, body, headers)(rds, addAuth, ec)
  }

   def doFormPost(url: String, body: Map[String, Seq[String]])
                         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    super.doFormPost(url, body)(addAuth,ec)
  }

  override def doPostString(url: String, body: String, headers: Seq[(String, String)])
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    super.doPostString(url, body, headers)(addAuth, ec)
  }

   def doEmptyPost[A](url: String)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    super.doEmptyPost(url)(addAuth,ec)
  }

   def doPut[A](url: String, body: A)
                       (implicit rds: Writes[A], hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    super.doPut(url, body)(rds, addAuth,ec)
  }

   def doDelete(url: String)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    super.doDelete(url)(addAuth,ec)
  }

   def doPatch[A](url: String, body: A)
                         (implicit rds: Writes[A], hc: HeaderCarrier, ec: ExecutionContext) :Future[HttpResponse] = {
    super.doPatch(url, body)(rds, addAuth,ec)
  }

}

trait InjectAuthHeader {

  private val headerName: String = "X-Api-Token"

  def addAuth(implicit config: FrontendAppConfig, hc: HeaderCarrier): HeaderCarrier = {
    hc.headers.toMap.get(headerName) match {
      case Some(_) => hc
      case _ => hc.withExtraHeaders(authHeaders)
    }
  }

  def authHeaders(implicit config: FrontendAppConfig ): (String, String) = {
    headerName -> config.apiToken
  }

}
