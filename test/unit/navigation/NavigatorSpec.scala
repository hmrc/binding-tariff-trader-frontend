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

package navigation

import base.SpecBase
import controllers.routes
import models.ImportOrExport.{Advice, Import}
import models.{ImportOrExport, _}
import org.mockito.Mockito._
import pages._

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode)(mock[UserAnswers]) shouldBe routes.IndexController.getApplications()
      }

      "go to contactCustomsDutyLiabilityTeam page when Advice option is selected" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get[ImportOrExport](ImportOrExportPage)).thenReturn(Some(Advice))

        navigator.nextPage(ImportExportOrAdvicePage, NormalMode)(mockUserAnswers) shouldBe routes.ContactCustomsDutyLiabilityTeamController.onPageLoad()
      }

      "go to the next page (Information you need) when import or export option is selected" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get[ImportOrExport](ImportOrExportPage)).thenReturn(Some(Import))

        navigator.nextPage(ImportExportOrAdvicePage, NormalMode)(mockUserAnswers) shouldBe routes.BeforeYouStartController.onPageLoad()
      }

      "go to SupportingMaterialFileListController when no is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(false))

        navigator.nextPage(AddConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
      }

      //TODO: Add the correct controller under DIT-2268
      "go to ProvideConfidentialInformation when yes is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(true))

        navigator.nextPage(AddConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.DescribeYourItemController.onPageLoad(NormalMode)
      }
    }

    "in Check mode" must {

      "go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode)(mock[UserAnswers]) shouldBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
