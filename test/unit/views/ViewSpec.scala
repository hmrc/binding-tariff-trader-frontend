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

package views

import config.FrontendAppConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.twirl.api.Html
import uk.gov.hmrc.play.test.UnitSpec

abstract class ViewSpec extends UnitSpec with GuiceOneAppPerSuite {

  private def injector = app.injector

  implicit val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  protected def view(html: Html): Document = {
    Jsoup.parse(html.toString())
  }

}
