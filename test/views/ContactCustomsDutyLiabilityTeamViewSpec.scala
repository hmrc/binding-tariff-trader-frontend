/*
 * Copyright 2024 HM Revenue & Customs
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

import models.NormalMode
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.contactCustomsDutyLiabilityTeam

class ContactCustomsDutyLiabilityTeamViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "contactCustomsDutyLiabilityTeam"

  val contactCustomsDutyLiabilityTeamView: contactCustomsDutyLiabilityTeam =
    app.injector.instanceOf[contactCustomsDutyLiabilityTeam]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => contactCustomsDutyLiabilityTeamView(frontendAppConfig, NormalMode)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => contactCustomsDutyLiabilityTeamView.render(frontendAppConfig, NormalMode, fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => contactCustomsDutyLiabilityTeamView.f(frontendAppConfig, NormalMode)(fakeRequest, messages)

  "BeforeYouStart view" when {
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

    input.foreach(args => (test _).tupled(args))

  }

}
