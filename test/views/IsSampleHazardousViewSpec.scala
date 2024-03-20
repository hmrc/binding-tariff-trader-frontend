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
import forms.IsSampleHazardousFormProvider
import models.NormalMode
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.isSampleHazardous

class IsSampleHazardousViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "isSampleHazardous"

  val form = new IsSampleHazardousFormProvider()()

  val isSampleHazardousView: isSampleHazardous = app.injector.instanceOf[isSampleHazardous]

  val fakeGETRequest: FakeRequest[AnyContentAsEmpty.type] = fakeGETRequestWithCSRF

  val viewViaApply: () => HtmlFormat.Appendable =
    () => isSampleHazardousView(frontendAppConfig, form, NormalMode)(fakeGETRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => isSampleHazardousView.render(frontendAppConfig, form, NormalMode, fakeGETRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => isSampleHazardousView.f(frontendAppConfig, form, NormalMode)(fakeGETRequest, messages)

  def createViewUsingForm: Form[Boolean] => HtmlFormat.Appendable =
    (form: Form[Boolean]) => isSampleHazardousView(frontendAppConfig, form, NormalMode)(fakeGETRequest, messages)

  "IsSampleHazardous view" when {
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

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.IsSampleHazardousController.onSubmit(NormalMode).url,
      "isSampleHazardous"
    )
  }
}
