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
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.index

class IndexViewSpec extends ViewBehaviours {

  val indexView: index = app.injector.instanceOf[index]

  def applicationView: () => HtmlFormat.Appendable = () => indexView(frontendAppConfig, CaseDetailTab.APPLICATION,
    Html.apply("expected-content"))(fakeRequest, messages)

  def rulingView: () => HtmlFormat.Appendable = () => indexView(frontendAppConfig, CaseDetailTab.RULING, Html.apply("expected-content"))(fakeRequest, messages)

  "Load application view" must {

    behave like normalPage(applicationView, "index")()

  }

  "Load rulings view" must {

    behave like normalPage(rulingView, "index")()

  }

  "IndexView" must {
    "contain a start button that redirects to Information you need Page" in {
      val doc = asDocument(applicationView())

      assertLinkContainsHref(doc, "start-application", routes.BeforeYouStartController.onPageLoad.url)
    }
  }
}
