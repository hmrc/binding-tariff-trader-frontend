/*
 * Copyright 2020 HM Revenue & Customs
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
import forms.ProvideGoodsDescriptionFormProvider
import models.NormalMode
import play.api.data.Form
import views.behaviours.StringViewBehaviours
import views.html.provideGoodsDescription

class ProvideGoodsDescriptionViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "provideGoodsDescription"

  val goodsName = "goods Name"
  val form = new ProvideGoodsDescriptionFormProvider()()

  val fakeGETRequest = fakeGETRequestWithCSRF

  def provideGoodsDescriptionView: provideGoodsDescription = injector.instanceOf[provideGoodsDescription]


  def createView = () =>
    provideGoodsDescriptionView(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  def createViewUsingForm = (form: Form[String]) =>
    provideGoodsDescriptionView(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  "ProvideGoodsDescription view" must {
    behave like normalPage(createView, messageKeyPrefix, goodsName)()

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      routes.ProvideGoodsDescriptionController.onSubmit(NormalMode).url,
      "goodsDescription")
  }
}