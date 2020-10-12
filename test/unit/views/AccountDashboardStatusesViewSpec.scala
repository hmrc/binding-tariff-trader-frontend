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

package unit.views

import models.{Case, Paged, oCase}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.account_dashboard_statuses

class AccountDashboardStatusesViewSpec extends ViewBehaviours {

  val emptyPaged = Paged.empty[Case]
  val paged = Paged(Seq(oCase.btiCaseExample))


  def applicationView(pagedCases: Paged[Case]): () => HtmlFormat.Appendable = () => account_dashboard_statuses(frontendAppConfig,
    pagedCases)(fakeRequest, messages)

  "no previous applications view" must {
    behave like normalPage(applicationView(emptyPaged), "index")()
  }

  "has previous applications view" must {
    behave like normalPage(applicationView(paged), "index")()
  }

  "Applications and ruling view" must {

    "show message to say no previous applications when there are none supplied" in {
      val doc = asDocument(applicationView(emptyPaged)())
      assertContainsText(doc, messages("index.noapplications"))
    }

    "show table containing previous applications when there are some" in {
      val doc = asDocument(applicationView(paged)())
      assert(!doc.html().contains(messages("index.noapplications")))
      assertRenderedById(doc,"applications-rulings-list")
    }
  }

}