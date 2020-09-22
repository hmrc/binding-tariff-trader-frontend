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
import forms.LegalChallengeDetailsFormProvider
import models.NormalMode
import play.api.data.Form
import views.behaviours.StringViewBehaviours
import views.html.legalChallengeDetails

class LegalChallengeDetailsViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "legalChallengeDetails"

  val form = new LegalChallengeDetailsFormProvider()()

  def createView = () => legalChallengeDetails(frontendAppConfig, form, NormalMode, "goodsName")(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => legalChallengeDetails(frontendAppConfig, form, NormalMode, "goodsName")(fakeRequest, messages)

  "LegalChallengeDetails view" must {
    behave like normalPage(createView, messageKeyPrefix, messageHeadingArgs = "goodsName")()

    behave like pageWithBackLink(createView)

    behave like textareaPage(
      createView = createViewUsingForm,
      messageKeyPrefix = messageKeyPrefix,
      expectedFormAction = routes.LegalChallengeDetailsController.onSubmit(NormalMode).url,
      expectedFormElement = "legalChallengeDetails",
      messageArgs = Seq("goodsName")
    )
  }
}
