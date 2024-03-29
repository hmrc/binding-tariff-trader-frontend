/*
 * Copyright 2023 HM Revenue & Customs
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

package viewmodels

import utils.UnitSpec

class ConfirmationUrlViewModelSpec extends UnitSpec {

  val btaHostUrl = "testHost"

  "confirmationUrlViewModel" should {
    "create a ConfirmationBTAUrlViewModel" when {
      "isBTAUser is set to true" in {
        val isBTAUser                                           = true
        val expectedViewModel: ConfirmationBTAUrlViewModel.type = ConfirmationBTAUrlViewModel

        ConfirmationUrlViewModel(isBTAUser) shouldBe expectedViewModel
        expectedViewModel.messageKey        shouldBe "view.bta.home.link"
        expectedViewModel.url               shouldBe controllers.routes.BTARedirectController.redirectToBTA.url
      }
    }

    "create a ConfirmationHomeUrlViewModel" when {
      "isBTAUser is set to false" in {
        val isBTAUser                                            = false
        val expectedViewModel: ConfirmationHomeUrlViewModel.type = ConfirmationHomeUrlViewModel

        ConfirmationUrlViewModel(isBTAUser) shouldBe expectedViewModel
        expectedViewModel.messageKey        shouldBe "view.application.home.Link"
        expectedViewModel.url shouldBe controllers.routes.IndexController
          .getApplicationsAndRulings(sortBy = None, order = None)
          .url
      }
    }
  }
}
