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
import forms.SimilarItemCommodityCodeFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.similarItemCommodityCode

class SimilarItemCommodityCodeViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix: String = "similarItemCommodityCode"

  val form: Form[Boolean] = new SimilarItemCommodityCodeFormProvider()()

  val similarItemCommodityCodeView: similarItemCommodityCode = app.injector.instanceOf[similarItemCommodityCode]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => similarItemCommodityCodeView(frontendAppConfig, form, NormalMode)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => similarItemCommodityCodeView.render(frontendAppConfig, form, NormalMode, fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => similarItemCommodityCodeView.ref.f(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[Boolean] => HtmlFormat.Appendable =
    (form: Form[Boolean]) => similarItemCommodityCodeView(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "SimilarItemCommodityCode view" when {
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

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.SimilarItemCommodityCodeController.onSubmit(NormalMode).url
    )
  }
}
