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

package viewmodels

sealed trait ConfirmationUrlViewModel {
  val messageKey: String
  val url: String
}

case object ConfirmationBTAUrlViewModel extends ConfirmationUrlViewModel {
  override val messageKey = "view.bta.home.link"
  override val url: String = controllers.routes.BTARedirectController.btaRedirect().url
}

case object ConfirmationHomeUrlViewModel extends ConfirmationUrlViewModel {
  override val messageKey = "view.application.home.Link"
  override val url: String = controllers.routes.IndexController.getApplicationsAndRulings(sortBy = None, order = None).url
}

object ConfirmationUrlViewModel {
  def apply(isBTAUser: Boolean): ConfirmationUrlViewModel = {
    if(isBTAUser) ConfirmationBTAUrlViewModel else ConfirmationHomeUrlViewModel
  }
}
