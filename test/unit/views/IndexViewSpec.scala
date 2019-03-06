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

import play.twirl.api.Html
import views.behaviours.ViewBehaviours
import views.html.index

class IndexViewSpec extends ViewBehaviours {

  def applicationView = () => index(frontendAppConfig, CaseDetailTab.APPLICATION, Html.apply("expected-content"))(fakeRequest, messages)

  def rulingView = () => index(frontendAppConfig, CaseDetailTab.RULING, Html.apply("expected-content"))(fakeRequest, messages)

  "Load application view" must {

    behave like normalPage(applicationView, "index")()

  }

  "Load rulings view" must {

    behave like normalPage(rulingView, "index")()

  }
}
