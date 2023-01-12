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
import play.api.data.Form
import forms.PreviousBTIRulingFormProvider
import models.NormalMode
import views.behaviours.ViewBehaviours
import views.html.previousBTIRuling
import views.behaviours.YesNoViewBehaviours

class PreviousBTIRulingViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "previousBTIRuling"
  val goodsName        = "some-goods-name"

  val form = new PreviousBTIRulingFormProvider()()

  val previousBTIRulingView: previousBTIRuling = app.injector.instanceOf[previousBTIRuling]

  def createView = () => previousBTIRulingView(frontendAppConfig, form, goodsName, NormalMode)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => previousBTIRulingView(frontendAppConfig, form, goodsName, NormalMode)(fakeRequest, messages)

  override protected def expectedLegend(messageKeyPrefix: String): String =
    messages(s"$messageKeyPrefix.heading", goodsName)

  "PreviousBTIRuling view" must {
    behave like normalPage(createView, messageKeyPrefix, goodsName)()

    behave like pageWithBackLink(createView)

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.PreviousBTIRulingController.onSubmit(NormalMode).url
    )
  }
}
