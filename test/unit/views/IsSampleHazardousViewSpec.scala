/*
 * Copyright 2021 HM Revenue & Customs
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
import forms.IsSampleHazardousFormProvider
import models.NormalMode
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.Html
import views.behaviours.YesNoViewBehaviours
import views.html.isSampleHazardous

class IsSampleHazardousViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "isSampleHazardous"

  val form = new IsSampleHazardousFormProvider()()

  val isSampleHazardousView: isSampleHazardous = app.injector.instanceOf[isSampleHazardous]

  val fakeGETRequest: FakeRequest[AnyContentAsEmpty.type] = fakeGETRequestWithCSRF

  def createView: () => Html = () =>
    isSampleHazardousView(frontendAppConfig, form, NormalMode)(fakeGETRequest, messages)

  def createViewUsingForm: Form[_] => Html = (form: Form[_]) =>
    isSampleHazardousView(frontendAppConfig, form, NormalMode)(fakeGETRequest, messages)

  "IsSampleHazardous view" must {

    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm,
      messageKeyPrefix,
      routes.IsSampleHazardousController.onSubmit(NormalMode).url,
      "isSampleHazardous")
  }
}
