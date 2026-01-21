/*
 * Copyright 2026 HM Revenue & Customs
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

import forms.UploadSupportingMaterialMultipleFormProvider
import models.NormalMode
import models.response.{FileStoreInitiateResponse, UpscanFormTemplate}
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import views.behaviours.FileUploadViewBehaviours
import views.html.uploadSupportingMaterialMultiple

class UploadSupportingMaterialMultipleViewSpec extends FileUploadViewBehaviours {

  val messageKeyPrefix: String = "uploadSupportingMaterialMultiple"

  val form: Form[String] = new UploadSupportingMaterialMultipleFormProvider()()

  val goodsName: String = "goose"

  val request: FakeRequest[AnyContentAsEmpty.type] = fakeGETRequestWithCSRF

  val initiateResponse: FileStoreInitiateResponse = FileStoreInitiateResponse(
    id = "id",
    upscanReference = "ref",
    uploadRequest = UpscanFormTemplate(
      "http://localhost:20001/upscan/upload",
      Map("key" -> "value")
    )
  )

  val uploadSupportingMaterialMultipleView: uploadSupportingMaterialMultiple =
    app.injector.instanceOf[uploadSupportingMaterialMultiple]

  val viewViaApply: () => HtmlFormat.Appendable =
    () =>
      uploadSupportingMaterialMultipleView(frontendAppConfig, initiateResponse, form, goodsName, NormalMode)(
        request,
        messages
      )
  val viewViaRender: () => HtmlFormat.Appendable =
    () =>
      uploadSupportingMaterialMultipleView
        .render(frontendAppConfig, initiateResponse, form, goodsName, NormalMode, request, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () =>
      uploadSupportingMaterialMultipleView.ref.f(frontendAppConfig, initiateResponse, form, goodsName, NormalMode)(
        request,
        messages
      )

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable =
    (form: Form[String]) =>
      uploadSupportingMaterialMultipleView(
        frontendAppConfig,
        initiateResponse,
        form,
        goodsName,
        NormalMode
      )(request, messages)

  "UploadSupportingMaterialMultiple view" when {
    def test(method: String, view: () => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, goodsName)()

        behave like pageWithBackLink(view)
      }

    val input: Seq[(String, () => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => test.tupled(args))

    behave like multipleFileUploadPage(createViewUsingForm, messageKeyPrefix)
  }

}
