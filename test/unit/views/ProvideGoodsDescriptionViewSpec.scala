/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.ProvideGoodsDescriptionFormProvider
import models.NormalMode
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.Html
import views.behaviours.StringViewBehaviours
import views.html.provideGoodsDescription

class ProvideGoodsDescriptionViewSpec extends StringViewBehaviours {

  private val messageKeyPrefix: String = "provideGoodsDescription"

  private val goodsName: String = "goods Name"

  override protected val form: Form[String] = new ProvideGoodsDescriptionFormProvider()()

  private val formElementId: String = "goodsDescription"

  private val fakeGETRequest: FakeRequest[AnyContentAsEmpty.type] = fakeGETRequestWithCSRF

  private val provideGoodsDescriptionView: provideGoodsDescription = app.injector.instanceOf[provideGoodsDescription]

  private def createView: () => Html =
    () => provideGoodsDescriptionView(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  private def createViewUsingForm: Form[String] => Html =
    (form: Form[String]) =>
      provideGoodsDescriptionView(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  "ProvideGoodsDescription view" must {
    behave like normalPage(createView, messageKeyPrefix, goodsName)()

    behave like pageWithBackLink(createView)

    behave like textAreaPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.ProvideGoodsDescriptionController.onSubmit(NormalMode).url,
      formElementId,
      List(goodsName)
    )

    "not allow unescaped HTML" in {
      val xss = "<script>alert('foo');</script>"
      val doc =
        asDocument(provideGoodsDescriptionView(frontendAppConfig, form, xss, NormalMode)(fakeGETRequest, messages))
      val scriptTag = doc.getElementsByAttributeValue("for", formElementId).select("script").first
      Option(scriptTag) shouldBe None
    }
  }
}
