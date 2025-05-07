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

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.session_expired

class SessionExpiredViewSpec extends ViewBehaviours {

  val sessionExpiredView: session_expired = app.injector.instanceOf[session_expired]

  val viewViaApply: () => HtmlFormat.Appendable = () => sessionExpiredView(frontendAppConfig)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable = () =>
    sessionExpiredView.render(frontendAppConfig, fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable = () => sessionExpiredView.ref.f(frontendAppConfig)(fakeRequest, messages)

  override val expectTimeoutDialog: Boolean = false

  "Session Expired view" when {

    def test(method: String, view: () => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, "session_expired")("guidance")
      }

    val input: Seq[(String, () => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => test.tupled(args))

  }
}
