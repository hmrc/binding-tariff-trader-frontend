/*
 * Copyright 2019 HM Revenue & Customs
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
import forms.CommodityCodeBestMatchFormProvider
import models.NormalMode
import play.api.data.Form
import views.behaviours.YesNoViewBehaviours
import views.html.commodityCodeBestMatch

class CommodityCodeBestMatchViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "commodityCodeBestMatch"

  private val view = app.injector.instanceOf[commodityCodeBestMatch]
  val form = new CommodityCodeBestMatchFormProvider()()

  def createView = () => view(form, NormalMode)(messages, fakeRequest)

  def createViewUsingForm = (form: Form[_]) => view(form, NormalMode)(messages, fakeRequest)

  "CommodityCodeBestMatch view" must {
    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.CommodityCodeBestMatchController.onSubmit(NormalMode).url)
  }
}
