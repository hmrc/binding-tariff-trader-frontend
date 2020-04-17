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

package connectors

import akka.actor.ActorSystem
import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Writes
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthenticatedHttpClient @Inject()(
                                         httpAuditing: HttpAuditing,
                                         wsClient: WSClient,
                                         actorSystem: ActorSystem
                                       )(implicit val config: FrontendAppConfig)
  extends DefaultHttpClient(config.runModeConfiguration, httpAuditing, wsClient, actorSystem)
    with InjectAuthHeader {
}

trait InjectAuthHeader {

  private val headerName: String = "X-Api-Token"

  def addAuth(implicit config: FrontendAppConfig, hc: HeaderCarrier): HeaderCarrier = {
    hc.headers.toMap.get(headerName) match {
      case Some(_) => hc
      case _ => hc.withExtraHeaders(authHeaders(config.apiToken))
    }
  }

  def authHeaders(apiToken: String): (String, String) = {
    headerName -> apiToken
  }

}
