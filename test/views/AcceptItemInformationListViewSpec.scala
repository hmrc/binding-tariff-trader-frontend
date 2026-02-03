/*
 * Copyright 2026 HM Revenue & Customs
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
import views.html.acceptItemInformationList

class AcceptItemInformationListViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "acceptItemInformationList"

  val acceptItemInformationListView: acceptItemInformationList = app.injector.instanceOf[acceptItemInformationList]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => acceptItemInformationListView(frontendAppConfig)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable = () =>
    acceptItemInformationListView.render(frontendAppConfig, fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => acceptItemInformationListView.ref.f(frontendAppConfig)(fakeRequest, messages)

  "AcceptItemInformationList view" when {
    def test(method: String, view: () => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix)()

        behave like pageWithBackLink(view)
      }

    val input: Seq[(String, () => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => test.tupled(args))
  }
}
