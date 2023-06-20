/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.CommodityCodeRulingReferenceFormProvider
import models.NormalMode
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.commodityCodeRulingReference

class CommodityCodeRulingReferenceViewSpec extends StringViewBehaviours {

  val messageKeyPrefix    = "commodityCodeRulingReference"
  private def onwardRoute = Call("GET", "/foo")

  val form = new CommodityCodeRulingReferenceFormProvider()()

  val commodityCodeRulingReferenceView: commodityCodeRulingReference =
    app.injector.instanceOf[commodityCodeRulingReference]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => commodityCodeRulingReferenceView(frontendAppConfig, form, onwardRoute, NormalMode)(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () =>
      commodityCodeRulingReferenceView.render(frontendAppConfig, form, onwardRoute, NormalMode, fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => commodityCodeRulingReferenceView.f(frontendAppConfig, form, onwardRoute, NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable =
    (form: Form[String]) =>
      commodityCodeRulingReferenceView(frontendAppConfig, form, onwardRoute, NormalMode)(fakeRequest, messages)

  "CommodityCodeRulingReference view" when {
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

    behave like stringPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.CommodityCodeRulingReferenceController.onSubmit(NormalMode).url,
      expectedHintKey = Some(s"$messageKeyPrefix.hint")
    )
  }
}
