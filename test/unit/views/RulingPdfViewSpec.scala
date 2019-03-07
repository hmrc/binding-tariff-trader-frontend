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

import models.response.FilestoreResponse
import models.{Case, oCase}
import utils.Dates
import views.html.pdftemplates.rulingPdf

class RulingPdfViewSpec extends ViewSpecBase {

  private def createView(c: Case) = rulingPdf(frontendAppConfig, c, c.decision.get)(fakeRequest, messages)

  private val rulingCase = oCase.btiCaseWithRulingExample
  private val holder = rulingCase.application.holder
  private val ruling = rulingCase.decision.getOrElse(throw new Exception("Bad test data"))
  private val doc = asDocument(createView(rulingCase))

  "Ruling pdf holder section" must {

    "contain the holders name" in {
      assertSectionContains("section-holder", holder.businessName)
    }

    "contain the holders address" in {
      assertSectionContains("section-holder", holder.addressLine1)
      assertSectionContains("section-holder", holder.addressLine2)
      assertSectionContains("section-holder", holder.addressLine3)
      assertSectionContains("section-holder", holder.postcode)
      assertSectionContains("section-holder", holder.country)
    }

    "contain the holders EORI" in {
      assertSectionContains("section-holder", holder.eori)
    }
  }

  "Ruling pdf ruling section" must {

    "contain the binding commodity code" in {
      assertSectionContains("section-ruling", ruling.bindingCommodityCode )
    }

    "contain the case reference" in {
      assertSectionContains("section-ruling", rulingCase.reference )
    }


    "contain the ruling start data" in {
      assertSectionContains("section-ruling", Dates.format(ruling.effectiveStartDate.get))
    }
  }

  private def assertSectionContains(sectionId: String, text: String) = {
    assertElementHasText(doc.getElementById(sectionId), text)
  }
}
