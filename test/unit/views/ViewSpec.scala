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
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.{FakeHeaders, FakeRequest}
import play.filters.csrf.CSRF.{Token, TokenProvider}
import play.twirl.api.Html
import uk.gov.hmrc.play.test.UnitSpec

abstract class ViewSpec extends UnitSpec with GuiceOneAppPerSuite {

  private def injector = app.injector

  implicit val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  private val tokenProvider: TokenProvider = app.injector.instanceOf[TokenProvider]
  private val csrfTags = Map(Token.NameRequestTag -> "csrfToken", Token.RequestTag -> tokenProvider.generateToken)

  private val request = FakeRequest("GET", "/", FakeHeaders(), AnyContentAsEmpty, tags = csrfTags)
//  protected val authenticatedOperator = Operator("operator-id")
//  implicit val authenticatedFakeRequest: AuthenticatedRequest[AnyContentAsEmpty.type] = AuthenticatedRequest(authenticatedOperator, request)
//
//  implicit val messages: Messages = injector.instanceOf[MessagesApi].preferred(authenticatedFakeRequest)

  protected def view(html: Html): Document = {
    Jsoup.parse(html.toString())
  }

}
