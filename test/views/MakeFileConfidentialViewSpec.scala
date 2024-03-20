/*
 * Copyright 2024 HM Revenue & Customs
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

import forms.MakeFileConfidentialFormProvider
import models.NormalMode
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import views.behaviours.BooleanViewBehaviours
import views.html.makeFileConfidential

class MakeFileConfidentialViewSpec extends BooleanViewBehaviours[(String, Boolean)] {

  val form                = new MakeFileConfidentialFormProvider()()
  private def onwardRoute = Call("GET", "/foo")

  private val messageKeyPrefix = "makeFileConfidential"
  private val fileId           = "a-file-id"

  val makeFileConfidentialView: makeFileConfidential = app.injector.instanceOf[makeFileConfidential]

  val viewViaApply: () => HtmlFormat.Appendable =
    () =>
      makeFileConfidentialView(frontendAppConfig, form, onwardRoute, NormalMode, fileId)(
        fakeGETRequestWithCSRF,
        messages
      )
  val viewViaRender: () => HtmlFormat.Appendable =
    () =>
      makeFileConfidentialView
        .render(frontendAppConfig, form, onwardRoute, NormalMode, fileId, fakeGETRequestWithCSRF, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () =>
      makeFileConfidentialView.f(frontendAppConfig, form, onwardRoute, NormalMode, fileId)(
        fakeGETRequestWithCSRF,
        messages
      )

  def createViewUsingForm: Form[(String, Boolean)] => HtmlFormat.Appendable =
    (form: Form[(String, Boolean)]) =>
      makeFileConfidentialView(frontendAppConfig, form, onwardRoute, NormalMode, fileId)(
        fakeGETRequestWithCSRF,
        messages
      )

  "makeFileConfidential view" must {
    def test(method: String, view: () => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix)()

        behave like pageWithBackLink(view)
      }

    val input: Seq[(String, () => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))

    behave like booleanPage(
      createViewUsingForm,
      _._2,
      messageKeyPrefix,
      "confidential"
    )((fileId, true), (fileId, false))
  }
}
