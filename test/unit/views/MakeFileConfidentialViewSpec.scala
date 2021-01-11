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
import forms.MakeFileConfidentialFormProvider
import models.NormalMode
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import views.behaviours.BooleanViewBehaviours
import views.html.makeFileConfidential

class MakeFileConfidentialViewSpec extends BooleanViewBehaviours[(String, Boolean)] {

  val form = new MakeFileConfidentialFormProvider()()
  private def onwardRoute = Call("GET", "/foo")

  private val fakeGETRequest = fakeGETRequestWithCSRF
  private val messageKeyPrefix = "makeFileConfidential"
  private val fileId = "a-file-id"

  def createView = () => makeFileConfidential(frontendAppConfig, form, onwardRoute, NormalMode, fileId)(fakeGETRequestWithCSRF, messages)

  def createViewUsingForm = (form: Form[_]) => makeFileConfidential(frontendAppConfig, form, onwardRoute, NormalMode, fileId)(fakeGETRequestWithCSRF, messages)

  "makeFileConfidential view" must {
    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

    behave like booleanPage(
      createViewUsingForm,
      _._2,
      messageKeyPrefix,
      routes.MakeFileConfidentialController.onSubmit(NormalMode).url,
      "confidential"
    )((fileId, true), (fileId, false))
  }
}
