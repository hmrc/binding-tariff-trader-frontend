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

package unit.views

import controllers.routes
import forms.{MakeFileConfidentialFormProvider, WhenToSendSampleFormProvider}
import models.{FileConfidentiality, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.{QuestionViewWithBooleanBehaviours, YesNoViewBehaviours}
import views.html.makeFileConfidential

class MakeFileConfidentialViewSpec extends QuestionViewWithBooleanBehaviours[FileConfidentiality] {

  val form = new MakeFileConfidentialFormProvider()()

  private val fakeGETRequest = fakeGETRequestWithCSRF
  private val messageKeyPrefix = "makeFileConfidential"
  private val fileId = "a-file-id"

  def createView = () => makeFileConfidential(frontendAppConfig, form, NormalMode, fileId)(fakeGETRequestWithCSRF, messages)

  def createViewUsingForm = (form: Form[_]) => makeFileConfidential(frontendAppConfig, form, NormalMode, fileId)(fakeGETRequestWithCSRF, messages)

  "makeFileConfidential view" must {
    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.MakeFileConfidentialController.onSubmit(NormalMode).url,
      "confidential"
    )
  }

  protected def subtypeSpecificTests(createView: Form[FileConfidentiality] => HtmlFormat.Appendable,
                                     messageKeyPrefix: String,
                                     expectedFormAction: String,
                                     elementIdPrefix: String = "value"): Unit = {
    "rendered with a value of true" must {
      behave like answeredYesNoPage(createView, FileConfidentiality(fileId, confidential = true), elementIdPrefix)
    }

    "rendered with a value of false" must {
      behave like answeredYesNoPage(createView, FileConfidentiality(fileId, confidential = false), elementIdPrefix)
    }
  }
}
