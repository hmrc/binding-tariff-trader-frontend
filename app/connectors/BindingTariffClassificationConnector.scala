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

import com.google.inject.Inject
import config.FrontendAppConfig
import javax.inject.Singleton
import models.{Case, NewCaseRequest}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import utils.JsonFormatters.{caseFormat, newCaseRequestFormat}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class BindingTariffClassificationConnector @Inject()(configuration: FrontendAppConfig, client: HttpClient) {

  def createCase(c: NewCaseRequest)(implicit hc: HeaderCarrier): Future[Case] = {
    val url = s"${configuration.bindingTariffClassificationUrl}/cases"
    client.POST[NewCaseRequest, Case](url = url, body = c)
  }

}
