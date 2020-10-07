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

import controllers.routes
import forms.ProvideCommodityCodeFormProvider
import models.NormalMode
import play.api.data.Form
import views.behaviours.StringViewBehaviours
import views.html.provideCommodityCode

class ProvideCommodityCodeViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "provideCommodityCode"

  val form = new ProvideCommodityCodeFormProvider()()

  val goodsName = "some goods"

  def createView = () => provideCommodityCode(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => provideCommodityCode(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)

  "ProvideCommodityCode view" must {
    behave like normalPage(createView, messageKeyPrefix, goodsName)()

    behave like pageWithBackLink(createView)

    behave like stringPage(
      createView = createViewUsingForm,
      messageKeyPrefix = messageKeyPrefix,
      expectedFormAction = routes.ProvideCommodityCodeController.onSubmit(NormalMode).url,
      messageArgs = Seq(goodsName)
    )
  }
}
