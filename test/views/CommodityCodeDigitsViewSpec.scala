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

import forms.CommodityCodeDigitsFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.commodityCodeDigits

class CommodityCodeDigitsViewSpec extends StringViewBehaviours {

  val messageKeyPrefix: String = "commodityCodeDigits"

  val form: Form[String] = new CommodityCodeDigitsFormProvider()()

  val goodsName: String = "some goods"

  val commodityCodeView: commodityCodeDigits = app.injector.instanceOf[commodityCodeDigits]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => commodityCodeView(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => commodityCodeView(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => commodityCodeView(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)

  "CommodityCodeDigits view" when {
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

  }
}
