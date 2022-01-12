/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient

@Singleton
class AuthenticatedHttpClient @Inject()(
                                         httpAuditing: HttpAuditing,
                                         wsClient: WSClient,
                                         actorSystem: ActorSystem
                                       )(implicit val config: FrontendAppConfig)
  extends DefaultHttpClient(config.runModeConfiguration, httpAuditing, wsClient, actorSystem) {
}

trait InjectAuthHeader {

  def addAuth(config: FrontendAppConfig) (implicit hc: HeaderCarrier): Seq[(String, String)] = {

    val headerName: String = "X-Api-Token"

    hc.headers(Seq(headerName)) match {
      case header @ Seq(_) => header
      case _ => Seq(headerName -> config.apiToken)
    }
  }

}
