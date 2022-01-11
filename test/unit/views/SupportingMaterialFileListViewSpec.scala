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

import forms.SupportingMaterialFileListFormProvider
import models.{FileAttachment, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.supportingMaterialFileList
import viewmodels.FileView

class SupportingMaterialFileListViewSpec extends YesNoViewBehaviours {

  private lazy val messageKeyPrefix = "supportingMaterialFileList"
  private val goodsName = "some-goods-name"

  override protected val form = new SupportingMaterialFileListFormProvider()()

  val supportingMaterialFileListView: supportingMaterialFileList = app.injector.instanceOf[supportingMaterialFileList]

  private def createView: () => HtmlFormat.Appendable = { () => createViewWithForm(form) }

  private def createViewWithForm(f: Form[Boolean], files: Seq[FileView] = Seq.empty): HtmlFormat.Appendable =
    supportingMaterialFileListView(frontendAppConfig, f, goodsName, files, NormalMode)(fakeRequest, messages)

  "SupportingMaterialFileList view" must {


    behave like pageWithBackLink(createView)

    "show the expected heading when no files have been uploaded" in {
      assertHeading(0)
    }

    "show the expected heading when 1 file has been uploaded" in {
      assertHeading(1)
    }

    "show the expected heading when multiple file have been uploaded" in {
      assertHeading(2)
    }

  }

  private def assertHeading: Int => Unit = { n: Int =>
    val filesForm = new SupportingMaterialFileListFormProvider
    val htmlView = asDocument(createViewWithForm(filesForm(), generateFiles(n)))

    val headings = htmlView.getElementsByTag("h1")
    assert(headings.size() == 1)
    val heading = headings.first().ownText()

    if (n == 1) {
      assert(heading == messages(s"${messageKeyPrefix}.uploadFileCounter.singular", goodsName))
    } else if (n > 1) {
      assert(heading == messages(s"${messageKeyPrefix}.uploadFileCounter.plural", n, goodsName))
    } else {
      assert(heading == messages(s"${messageKeyPrefix}.heading", goodsName))
    }
  }

  private def generateFiles(number: Int): Seq[FileView] = {
    (1 to number).map { idx =>
      FileView(s"id$idx", s"name$idx", false)
    }
  }
}
