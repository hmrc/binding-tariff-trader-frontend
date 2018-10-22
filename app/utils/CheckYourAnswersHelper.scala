/*
 * Copyright 2018 HM Revenue & Customs
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

package utils

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages._
import viewmodels.{AnswerRow, RepeaterAnswerRow, RepeaterAnswerSection}

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def registered_address_for_eori: Option[AnswerRow] = userAnswers.get(registered_address_for_eoriPage) map {
    x => AnswerRow("registered_address_for_eori.checkYourAnswersLabel", s"${x.field1} ${x.field2}", false, routes.registered_address_for_eoriController.onPageLoad(CheckMode).url)
  }

  def registered_address_for_eori: Option[AnswerRow] = userAnswers.get(registered_address_for_eoriPage) map {
    x => AnswerRow("registered_address_for_eori.checkYourAnswersLabel", s"${x.field1} ${x.field2}", false, routes.registered_address_for_eoriController.onPageLoad(CheckMode).url)
  }
}
