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
import forms.AddSupportingDocumentsFormProvider
import models.NormalMode
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.addSupportingDocuments

class AddSupportingDocumentsViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "addSupportingDocuments"

  val form = new AddSupportingDocumentsFormProvider()()

  val fakeGETRequest: FakeRequest[AnyContentAsEmpty.type] = fakeGETRequestWithCSRF

  val goodsName: String = "Pork futures"

  val addSupportingDocumentsView: addSupportingDocuments = app.injector.instanceOf[addSupportingDocuments]

  def createView: () => HtmlFormat.Appendable =
    () => addSupportingDocumentsView(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  def createViewUsingForm: Form[Boolean] => HtmlFormat.Appendable =
    (form: Form[Boolean]) =>
      addSupportingDocumentsView(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  "AddSupportingDocuments view" must {

    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.AddSupportingDocumentsController.onSubmit(NormalMode).url
    )
  }
}
