/*
 * Copyright 2022 HM Revenue & Customs
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

  val messageKeyPrefix = "commodityCodeRulingReference"
  private def onwardRoute = Call("GET", "/foo")

  val form = new CommodityCodeRulingReferenceFormProvider()()

  val commodityCodeRulingReferenceView: commodityCodeRulingReference = app.injector.instanceOf[commodityCodeRulingReference]

  def createView: () => HtmlFormat.Appendable = () => commodityCodeRulingReferenceView(frontendAppConfig, form, onwardRoute, NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable = (form: Form[String]) =>
    commodityCodeRulingReferenceView(frontendAppConfig, form, onwardRoute, NormalMode)(fakeRequest, messages)

  "CommodityCodeRulingReference view" must {
    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    behave like stringPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.CommodityCodeRulingReferenceController.onSubmit(NormalMode).url,
      expectedHintKey = Some(s"$messageKeyPrefix.hint")
    )
  }
}
