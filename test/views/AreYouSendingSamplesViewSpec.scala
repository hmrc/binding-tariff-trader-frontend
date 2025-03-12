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

import controllers.routes
import forms.AreYouSendingSamplesFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.areYouSendingSamples

class AreYouSendingSamplesViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "areYouSendingSamples"

  val goodsName = "some-goods-name"

  val form = new AreYouSendingSamplesFormProvider()()

  val areYouSendingSamplesView: areYouSendingSamples = app.injector.instanceOf[areYouSendingSamples]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => areYouSendingSamplesView(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => areYouSendingSamplesView.render(frontendAppConfig, form, NormalMode, goodsName, fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => areYouSendingSamplesView.f(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)

  def createViewUsingForm: Form[Boolean] => HtmlFormat.Appendable =
    (form: Form[Boolean]) =>
      areYouSendingSamplesView(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)

  "AreYouSendingSamples view" when {
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

    input.foreach(args => test.tupled(args))

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.AreYouSendingSamplesController.onSubmit(NormalMode).url
    )

  }

  override protected def expectedLegend(messageKeyPrefix: String): String =
    messages(s"$messageKeyPrefix.heading", goodsName)
}
