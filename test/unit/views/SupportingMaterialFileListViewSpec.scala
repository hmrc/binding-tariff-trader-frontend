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

package views

import forms.SupportingMaterialFileListFormProvider
import models.{FileAttachment, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.supportingMaterialFileList

class SupportingMaterialFileListViewSpec extends YesNoViewBehaviours {

  private lazy val messageKeyPrefix = "supportingMaterialFileList"

  override protected val form = new SupportingMaterialFileListFormProvider()()

  private def createView: () => HtmlFormat.Appendable = { () =>
    createViewWithForm(form)
  }

  private def createViewWithForm(f: Form[Boolean], files: Seq[FileAttachment] = Seq.empty): HtmlFormat.Appendable = {
    supportingMaterialFileList(frontendAppConfig, f, files, NormalMode)(fakeRequest, messages)
  }

  "SupportingMaterialFileList view" must {

    behave like normalPage(createView, messageKeyPrefix)()

    behave like pageWithBackLink(createView)

//    behave like yesNoPage() // TODO: not working at the moment as it has not been created as the other Yes/No pages

    "show the expected heading when no files have been uploaded" in {
      val htmlView = asDocument(createView())

      val headings = htmlView.getElementsByTag("h1")
      assert(headings.size() == 1)
      assert(headings.first().text() == "Do you want to upload any supporting documents?")
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
    val heading = headings.first().text()
    if (n == 1) {
      assert(heading == s"You have uploaded $n supporting document")
    } else {
      assert(heading == s"You have uploaded $n supporting documents")
    }
  }

  private def generateFiles: Int => Seq[FileAttachment] = { n =>

    def generateFile: Int => FileAttachment = { n =>
      FileAttachment(s"id$n", s"name$n", s"mime$n", 1L)
    }

    (1 to n) map generateFile
  }

}
