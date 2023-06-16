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
import forms.AreYouSendingSamplesFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.areYouSendingSamples

class AreYouSendingSamplesViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "areYouSendingSamples"

  val goodsName = "some-goods-name"

  val form = new AreYouSendingSamplesFormProvider()()

  val areYouSendingSamplesView: areYouSendingSamples = app.injector.instanceOf[areYouSendingSamples]

  def createView: () => HtmlFormat.Appendable =
    () => areYouSendingSamplesView(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable =
    (form: Form[_]) => areYouSendingSamplesView(frontendAppConfig, form, NormalMode, goodsName)(fakeRequest, messages)

  "AreYouSendingSamples view" must {
    behave like normalPage(createView, messageKeyPrefix, goodsName)()

    behave like pageWithBackLink(createView)

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.AreYouSendingSamplesController.onSubmit(NormalMode).url
    )

  }

  override protected def expectedLegend(messageKeyPrefix: String): String =
    messages(s"$messageKeyPrefix.heading", goodsName)
}
