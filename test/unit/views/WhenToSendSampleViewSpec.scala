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
import play.api.data.Form
import forms.WhenToSendSampleFormProvider
import models.NormalMode
import views.behaviours.YesNoViewBehaviours
import views.html.whenToSendSample

class WhenToSendSampleViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "whenToSendSample"

  private val view = app.injector.instanceOf[whenToSendSample]
  val form = new WhenToSendSampleFormProvider()()

  def createView = () => view(form, NormalMode)(messages, fakeRequest)

  def createViewUsingForm = (form: Form[_]) => view(form, NormalMode)(messages, fakeRequest)

  "WhenToSendSample view" must {
    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.WhenToSendSampleController.onSubmit(NormalMode).url)

  }

}
