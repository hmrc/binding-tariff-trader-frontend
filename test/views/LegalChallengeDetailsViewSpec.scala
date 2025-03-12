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
import forms.LegalChallengeDetailsFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.legalChallengeDetails

class LegalChallengeDetailsViewSpec extends StringViewBehaviours {

  private val messageKeyPrefix: String = "legalChallengeDetails"

  private val formElementId: String = "legalChallengeDetails"

  override protected val form: Form[String] = new LegalChallengeDetailsFormProvider()()

  private val legalChallengeDetailsView: legalChallengeDetails = app.injector.instanceOf[legalChallengeDetails]

  val viewViaApply: () => HtmlFormat.Appendable =
    () => legalChallengeDetailsView(frontendAppConfig, form, NormalMode, "goodsName")(fakeRequest, messages)
  val viewViaRender: () => HtmlFormat.Appendable =
    () => legalChallengeDetailsView.render(frontendAppConfig, form, NormalMode, "goodsName", fakeRequest, messages)
  val viewViaF: () => HtmlFormat.Appendable =
    () => legalChallengeDetailsView.f(frontendAppConfig, form, NormalMode, "goodsName")(fakeRequest, messages)

  private def createViewUsingForm: Form[String] => HtmlFormat.Appendable =
    (form: Form[String]) =>
      legalChallengeDetailsView(frontendAppConfig, form, NormalMode, "goodsName")(fakeRequest, messages)

  "LegalChallengeDetails view" when {
    def test(method: String, view: () => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, messageHeadingArgs = "goodsName")()

        behave like pageWithBackLink(view)
      }

    val input: Seq[(String, () => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => test.tupled(args))

    behave like textAreaPage(
      createView = createViewUsingForm,
      messageKeyPrefix = messageKeyPrefix,
      expectedFormAction = routes.LegalChallengeDetailsController.onSubmit(NormalMode).url,
      expectedFormElementId = formElementId,
      messageArgs = Seq("goodsName")
    )

    "not allow unescaped HTML" in {
      val xss = "<script>alert('foo');</script>"
      val doc = asDocument(legalChallengeDetailsView(frontendAppConfig, form, NormalMode, xss)(fakeRequest, messages))
      val scriptTag = doc.getElementsByAttributeValue("for", formElementId).select("script").first
      Option(scriptTag) shouldBe None
    }
  }
}
