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
import forms.PreviousBTIRulingFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.previousBTIRuling

class PreviousBTIRulingViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "previousBTIRuling"
  val goodsName        = "some-goods-name"

  val form = new PreviousBTIRulingFormProvider()()

  val previousBTIRulingView: previousBTIRuling = app.injector.instanceOf[previousBTIRuling]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => previousBTIRulingView(frontendAppConfig, form, goodsName, NormalMode)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => previousBTIRulingView.render(frontendAppConfig, form, goodsName, NormalMode, fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => previousBTIRulingView.ref.f(frontendAppConfig, form, goodsName, NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[Boolean] => HtmlFormat.Appendable =
    (form: Form[Boolean]) =>
      previousBTIRulingView(frontendAppConfig, form, goodsName, NormalMode)(fakeRequest, messages)

  override protected def expectedLegend(messageKeyPrefix: String): String =
    messages(s"$messageKeyPrefix.heading", goodsName)

  "PreviousBTIRuling view" when {
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
      routes.PreviousBTIRulingController.onSubmit(NormalMode).url
    )
  }
}
