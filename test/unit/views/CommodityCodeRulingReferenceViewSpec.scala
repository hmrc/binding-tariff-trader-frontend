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

import forms.CommodityCodeRulingReferenceFormProvider
import models.NormalMode
import play.api.data.Form
import views.behaviours.StringViewBehaviours
import views.html.commodityCodeRulingReference

class CommodityCodeRulingReferenceViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "commodityCodeRulingReference"

  val form = new CommodityCodeRulingReferenceFormProvider()()

  def createView = () => commodityCodeRulingReference(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => commodityCodeRulingReference(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "CommodityCodeRulingReference view" must {
    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    // TODO scaffold test cannot cope with text-area with no label
//    behave like textareaPage(createViewUsingForm, messageKeyPrefix, routes.CommodityCodeRulingReferenceController.onSubmit(NormalMode).url)
  }
}
