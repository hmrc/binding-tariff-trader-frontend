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
import forms.AddConfidentialInformationFormProvider
import models.NormalMode
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.addConfidentialInformation

class AddConfidentialInformationViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "addConfidentialInformation"

  val form = new AddConfidentialInformationFormProvider()()

  val addConfidentialInformationView: addConfidentialInformation = injector.instanceOf[addConfidentialInformation]

  val fakeGETRequest: FakeRequest[AnyContentAsEmpty.type] = fakeGETRequestWithCSRF

  val goodsName: String = "goods name"

  val viewViaApply: () => HtmlFormat.Appendable =
    () => addConfidentialInformationView(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () =>
      addConfidentialInformationView.render(frontendAppConfig, form, goodsName, NormalMode, fakeGETRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => addConfidentialInformationView.f(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  def createViewUsingForm: Form[Boolean] => HtmlFormat.Appendable =
    (form: Form[Boolean]) =>
      addConfidentialInformationView(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  override def expectedLegend(messageKeyPrefix: String): String =
    messages(s"$messageKeyPrefix.heading", goodsName)

  "AddConfidentialInformation view" when {

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
      routes.AddConfidentialInformationController.onSubmit(NormalMode).url
    )
  }
}
