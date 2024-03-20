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

import controllers.routes
import forms.LegalChallengeFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.legalChallenge

class LegalChallengeViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "legalChallenge"

  val form = new LegalChallengeFormProvider()()

  val goodsName = "wine"

  override protected def expectedLegend(messageKeyPrefix: String): String =
    messages(s"$messageKeyPrefix.heading", goodsName)

  val legalChallengeView: legalChallenge = app.injector.instanceOf[legalChallenge]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => legalChallengeView(frontendAppConfig, form, goodsName, NormalMode)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => legalChallengeView.render(frontendAppConfig, form, goodsName, NormalMode, fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => legalChallengeView.f(frontendAppConfig, form, goodsName, NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[Boolean] => HtmlFormat.Appendable =
    (form: Form[Boolean]) => legalChallengeView(frontendAppConfig, form, goodsName, NormalMode)(fakeRequest, messages)

  "LegalChallenge view" when {

    def test(method: String, view: () => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, goodsName)()

        behave like pageWithBackLink(view)
      }

    val input: Seq[(String, () => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.LegalChallengeController.onSubmit(NormalMode).url
    )
  }
}
