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

package views

import play.api.data.Form
import forms.CommodityCodeDigitsFormProvider
import models.NormalMode
import views.behaviours.StringViewBehaviours
import views.html.commodityCodeDigits

class CommodityCodeDigitsViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "commodityCodeDigits"

  val form = new CommodityCodeDigitsFormProvider()()

  val goodsName = "some goods"

  def createView = () => commodityCodeDigits(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => commodityCodeDigits(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)

  "CommodityCodeDigits view" must {
    behave like normalPage(createView, messageKeyPrefix, "goods")()

    behave like pageWithBackLink(createView)

    // TODO scaffold test cannot cope with text field with no label
//    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.CommodityCodeDigitsController.onSubmit(NormalMode).url)
  }
}
