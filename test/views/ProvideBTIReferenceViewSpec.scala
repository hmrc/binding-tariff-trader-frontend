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

import forms.ProvideBTIReferenceFormProvider
import models.{BTIReference, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.provideBTIReference

class ProvideBTIReferenceViewSpec extends QuestionViewBehaviours[BTIReference] {

  val messageKeyPrefix = "provideBTIReference"

  override protected val form = new ProvideBTIReferenceFormProvider()()

  val previousBTIReferenceView: provideBTIReference = app.injector.instanceOf[provideBTIReference]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => previousBTIReferenceView(frontendAppConfig, form, NormalMode)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => previousBTIReferenceView.render(frontendAppConfig, form, NormalMode, fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => previousBTIReferenceView.f(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[BTIReference] => HtmlFormat.Appendable =
    (form: Form[BTIReference]) => previousBTIReferenceView(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "ProvideBTIReference view" when {

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

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      "btiReference"
    )
  }
}
