/*
 * Copyright 2025 HM Revenue & Customs
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

import models.requests.IdentifierRequest
import views.behaviours.ViewBehaviours
import views.html.beforeYouStart
import play.twirl.api.{Html, HtmlFormat}

class BeforeYouStartViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "beforeYouStart"

  val beforeYouStartView: beforeYouStart = app.injector.instanceOf[beforeYouStart]

  val viewViaApply: () => HtmlFormat.Appendable = () =>
    beforeYouStartView(frontendAppConfig)(IdentifierRequest(fakeRequest, "id", Some("eori")), messages)
  val viewViaRender: () => HtmlFormat.Appendable = () =>
    beforeYouStartView.render(frontendAppConfig, IdentifierRequest(fakeRequest, "id", Some("eori")), messages)
  val viewViaF: () => HtmlFormat.Appendable = () =>
    beforeYouStartView.ref.f(frontendAppConfig)(IdentifierRequest(fakeRequest, "id", Some("eori")), messages)

  "BeforeYouStart view" when {
    def test(method: String, view: () => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like pageWithBackLink(view)
        behave like normalPage(view, messageKeyPrefix)()
      }

    val input: Seq[(String, () => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => test.tupled(args))
  }
}
