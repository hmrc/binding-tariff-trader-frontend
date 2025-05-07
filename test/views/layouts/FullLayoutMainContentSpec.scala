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

package views.layouts

import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.layouts.FullLayoutMainContent

class FullLayoutMainContentSpec extends ViewBehaviours {

  val messageKeyPrefix = "acceptItemInformationList"

  val fullLayoutMainContentView: FullLayoutMainContent = app.injector.instanceOf[FullLayoutMainContent]

  val viewViaApply: () => HtmlFormat.Appendable  = () => fullLayoutMainContentView(Html("test"))
  val viewViaRender: () => HtmlFormat.Appendable = () => fullLayoutMainContentView.render(Html("test"))
  val viewViaF: () => HtmlFormat.Appendable      = () => fullLayoutMainContentView.ref.f(Html("test"))

  "FullLayoutMainContent view" when {
    def test(method: String, view: () => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like pageWithoutBackLink(view)
        behave like pageWithoutCancelApplicationLink(view)
      }

    val input: Seq[(String, () => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => test.tupled(args))
  }
}
