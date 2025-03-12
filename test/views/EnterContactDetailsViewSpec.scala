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

import forms.EnterContactDetailsFormProvider
import models.{EnterContactDetails, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.enterContactDetails

class EnterContactDetailsViewSpec extends QuestionViewBehaviours[EnterContactDetails] {

  val messageKeyPrefix = "enterContactDetails"

  override protected val form = new EnterContactDetailsFormProvider()()

  val enterContactDetailsView: enterContactDetails = app.injector.instanceOf[enterContactDetails]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => enterContactDetailsView(frontendAppConfig, form, NormalMode)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => enterContactDetailsView.render(frontendAppConfig, form, NormalMode, fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => enterContactDetailsView.f(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[EnterContactDetails] => HtmlFormat.Appendable =
    (form: Form[EnterContactDetails]) =>
      enterContactDetailsView(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "EnterContactDetails view" when {

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

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      "name",
      "email",
      "phoneNumber"
    )
  }

}
