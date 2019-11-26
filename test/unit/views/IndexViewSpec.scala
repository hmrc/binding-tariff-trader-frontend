/*
 * Copyright 2019 HM Revenue & Customs
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

import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.index

class IndexViewSpec extends ViewBehaviours {

  val view = app.injector.instanceOf[index]


  def applicationView: () => HtmlFormat.Appendable = () =>
    view(CaseDetailTab.APPLICATION, Html.apply("expected-content"))(messages, fakeRequest)

  def rulingView: () => HtmlFormat.Appendable = () =>
    view(CaseDetailTab.RULING, Html.apply("expected-content"))(messages, fakeRequest)

  "Load application view" must {

    behave like normalPage(applicationView, "index")()

  }

  "Load rulings view" must {

    behave like normalPage(rulingView, "index")()

  }
}
