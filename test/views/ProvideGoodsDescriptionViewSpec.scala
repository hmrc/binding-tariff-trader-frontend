/*
 * Copyright 2025 HM Revenue & Customs
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
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.provideGoodsDescription

class ProvideGoodsDescriptionViewSpec extends StringViewBehaviours {

  private val messageKeyPrefix: String = "provideGoodsDescription"

  private val goodsName: String = "goods Name"

  override protected val form: Form[String] = new ProvideGoodsDescriptionFormProvider()()

  private val formElementId: String = "goodsDescription"

  private val fakeGETRequest: FakeRequest[AnyContentAsEmpty.type] = fakeGETRequestWithCSRF

  private val provideGoodsDescriptionView: provideGoodsDescription = app.injector.instanceOf[provideGoodsDescription]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => provideGoodsDescriptionView(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => provideGoodsDescriptionView.render(frontendAppConfig, form, goodsName, NormalMode, fakeGETRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => provideGoodsDescriptionView.ref.f(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  private def createViewUsingForm: Form[String] => HtmlFormat.Appendable =
    (form: Form[String]) =>
      provideGoodsDescriptionView(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  "ProvideGoodsDescription view" when {
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
