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

import views.behaviours.ViewBehaviours
import views.html.accessibilityView

class AccessibilityViewSpec extends ViewBehaviours{
  val messageKeyPrefix = "accessibility"
  override val expectTimeoutDialog = false
  def view = () => accessibilityView(frontendAppConfig)(fakeRequest, messages)

  "AccessibilityView view" must {

    behave like normalPage(view, messageKeyPrefix)()
    behave like pageWithBackLink(view)

    "contain a link to gov Accessibility Help page" in {
      val doc = asDocument(view())
      assertLinkContainsHref(doc, "govuk_accessibility_url", appConfig.govukAccessibilityUrl)
    }

    "contain a link to gov tax service Binding Tariff Information" in {
      val doc = asDocument(view())
      assertLinkContainsHref(doc, "govuk_taxservice_subdomain", appConfig.subdomainUrl)
    }

    "contain a link to Ability Net page" in {
      val doc = asDocument(view())
      assertLinkContainsHref(doc, "abilityNet_url", appConfig.abilityNetUrl)
    }

    "contain a link to Web Content Accessibility Guidelines page" in {
      val doc = asDocument(view())
      assertLinkContainsHref(doc, "webstandard_url", appConfig.webStandards)
    }

    "contain a link to email regarding Accessibility Problem" in {
      val doc = asDocument(view())
      assertLinkContainsHref(doc, "email_link", appConfig.reportEmail)
    }

    "contain a link to Equality Advisory and Support Service page" in {
      val doc = asDocument(view())
      assertLinkContainsHref(doc, "equality_advisory", appConfig.equalityadvisoryservice)
    }

    "contain a link to Equality Commission for Northern Ireland page" in {
      val doc = asDocument(view())
      assertLinkContainsHref(doc, "equality_commission", appConfig.equalityni)
    }

    "contain a link to Get help from HMRC" in {
      val doc = asDocument(view())
      assertLinkContainsHref(doc, "get_help", appConfig.extrasupport)
    }

    "contain a link to Digital Accessibility Centre" in {
      val doc = asDocument(view())
      assertLinkContainsHref(doc, "digital_centre", appConfig.digitalcentre)
    }

    "contain correct number of bullet points display on the page" in {
      val doc = asDocument(view())
      assert(doc.getElementsByClass("acceList_item").eachText().size() == 10 ,"expected list of elements is 10")
    }

  }

}
