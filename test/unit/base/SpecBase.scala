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

package base

import com.codahale.metrics.SharedMetricRegistries
import config.FrontendAppConfig
import models.requests.OptionalDataRequest
import org.scalatestplus.play.PlaySpec
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.WithFakeApplication

trait SpecBase extends PlaySpec with WithFakeApplication {

  SharedMetricRegistries.clear()

  protected lazy val injector: Injector = fakeApplication.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def fakeRequest = FakeRequest()

  def fakeRequestWithEori = OptionalDataRequest(fakeRequest, "id", Some("eori-789012"), None)
  def fakeRequestWithoutEori = OptionalDataRequest(fakeRequest, "id", Some("eori-789012"), None)

  def messages: Messages = messagesApi.preferred(fakeRequest)
}
