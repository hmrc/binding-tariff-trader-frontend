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

package views

import controllers.routes
import forms.UploadSupportingMaterialMultipleFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.FileUploadViewBehaviours
import views.html.uploadSupportingMaterialMultiple
import models.response.FileStoreInitiateResponse
import models.response.UpscanFormTemplate

class UploadSupportingMaterialMultipleViewSpec extends FileUploadViewBehaviours {

  val messageKeyPrefix = "uploadSupportingMaterialMultiple"

  val form = new UploadSupportingMaterialMultipleFormProvider()()

  val goodsName = "goose"

  val request = fakeGETRequestWithCSRF

  val initiateResponse = FileStoreInitiateResponse(
    id = "id",
    upscanReference = "ref",
    uploadRequest = UpscanFormTemplate(
      "http://localhost:20001/upscan/upload",
      Map("key" -> "value")
    )
  )

  def createView: () => HtmlFormat.Appendable = () =>
    uploadSupportingMaterialMultiple(frontendAppConfig, initiateResponse, form, goodsName, NormalMode)(request, messages)

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable =
    (form: Form[String]) => uploadSupportingMaterialMultiple(
      frontendAppConfig,
      initiateResponse,
      form,
      goodsName,
      NormalMode
    )(request, messages)

  "UploadSupportingMaterialMultiple view" must {
    behave like normalPage(createView, messageKeyPrefix, goodsName)()

    behave like pageWithBackLink(createView)

    behave like multipleFileUploadPage(createViewUsingForm, messageKeyPrefix, initiateResponse.uploadRequest.href)
  }

}
