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

package unit.views

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

  def provideGoodsNameView: provideGoodsName = injector.instanceOf[provideGoodsName]

  def createView: () => HtmlFormat.Appendable = () => provideGoodsNameView(
    frontendAppConfig, form, NormalMode)(fakeGETRequest, messages)

  def createViewUsingForm: Form[String] => Html = (form: Form[String]) => provideGoodsNameView(
    frontendAppConfig, form, NormalMode)(fakeGETRequest, messages)

  "ProvideGoodsName view" must {

    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.ProvideGoodsNameController.onSubmit(NormalMode).url, forElement = "goodsName")
  }
}
