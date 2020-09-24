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
import models._
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

      "Go to Before you start from index when required" in {

        navigator.nextPage(IndexPage, NormalMode)(mock[UserAnswers]) shouldBe routes.BeforeYouStartController.onPageLoad()

      }


      "go to ProvideGoodsDescriptionPage after ProvideGoodsName page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(ProvideGoodsNamePage)).thenReturn(Some("goods name"))

        navigator.nextPage(ProvideGoodsNamePage, NormalMode)(mockUserAnswers) shouldBe routes.ProvideGoodsDescriptionController.onPageLoad(NormalMode)
      }

      "go to AddConfidentialInformationPage after ProvideGoodsDescriptionPage" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(ProvideGoodsDescriptionPage)).thenReturn(Some("goods description"))

        navigator.nextPage(ProvideGoodsDescriptionPage, NormalMode)(mockUserAnswers) shouldBe routes.AddConfidentialInformationController.onPageLoad(NormalMode)
      }


      "go to SupportingMaterialFileListController when no is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(false))

        navigator.nextPage(AddConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
      }

      "go to ProvideConfidentialInformation when yes is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(true))

        navigator.nextPage(AddConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.ProvideConfidentialInformationController.onPageLoad(NormalMode)
      }

      "go to SupportingMaterialFileListPage after entering confidential info in ProvideConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(ProvideConfidentialInformationPage)).thenReturn(Some("confidential information"))

        navigator.nextPage(ProvideConfidentialInformationPage, NormalMode)(mockUserAnswers) shouldBe
          routes.SupportingMaterialFileListController.onPageLoad(NormalMode)
      }

      "return to SupportingMaterialFileList page when file is removed" in {

      }
    }

    "in Check mode" must {

      "go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode)(mock[UserAnswers]) shouldBe routes.CheckYourAnswersController.onPageLoad()
      }

      "go to next page (ProvideConfidentialInformation) when yes is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(true))

        navigator.nextPage(AddConfidentialInformationPage, CheckMode)(mockUserAnswers) shouldBe
          routes.ProvideConfidentialInformationController.onPageLoad(CheckMode)
      }

      "return to CheckYourAnswers when no is selected in AddConfidentialInformation page" in {
        val mockUserAnswers = mock[UserAnswers]

        when(mockUserAnswers.get(AddConfidentialInformationPage)).thenReturn(Some(false))

        navigator.nextPage(AddConfidentialInformationPage, CheckMode)(mockUserAnswers) shouldBe
          routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
