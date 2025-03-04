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
import forms.ProvideGoodsNameFormProvider
import models.NormalMode
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.StringViewBehaviours
import views.html.provideGoodsName

class ProvideGoodsNameViewSpec extends StringViewBehaviours {

  val messageKeyPrefix: String = "provideGoodsName"

  val form: Form[String] = new ProvideGoodsNameFormProvider()()

  val fakeGETRequest: FakeRequest[AnyContentAsEmpty.type] = fakeGETRequestWithCSRF

  val provideGoodsNameView: provideGoodsName = injector.instanceOf[provideGoodsName]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => provideGoodsNameView(frontendAppConfig, form, NormalMode)(fakeGETRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => provideGoodsNameView.render(frontendAppConfig, form, NormalMode, fakeGETRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => provideGoodsNameView.f(frontendAppConfig, form, NormalMode)(fakeGETRequest, messages)

  def createViewUsingForm: Form[String] => Html =
    (form: Form[String]) => provideGoodsNameView(frontendAppConfig, form, NormalMode)(fakeGETRequest, messages)

  "ProvideGoodsName view" when {

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

    behave like stringPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.ProvideGoodsNameController.onSubmit(NormalMode).url,
      forElement = "goodsName"
    )
  }
}
