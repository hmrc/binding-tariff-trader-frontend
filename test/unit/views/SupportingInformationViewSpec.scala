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
import play.api.data.Form
import forms.SupportingInformationFormProvider
import models.NormalMode
import views.behaviours.{ViewBehaviours, YesNoViewBehaviours}
import views.html.supportingInformation

class SupportingInformationViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "supportingInformation"

  val form = new SupportingInformationFormProvider()()

  def createView = () => supportingInformation(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => supportingInformation(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "SupportingInformation view" must {
    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.SupportingInformationController.onSubmit(NormalMode).url)
  }

}
