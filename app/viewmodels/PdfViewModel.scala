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

package viewmodels

import java.time.Instant

import models.{Case, Contact, EORIDetails}

case class PdfViewModel(
                         eori: String,
                         reference: String,
                         accountDetails: EORIDetails,
                         contact: Contact,
                         dateSubmitted: Instant,
                         goodsName: String,
                         goodsDetails: String,
                       )

object PdfViewModel{

  def apply(c: Case): PdfViewModel = new PdfViewModel(
    eori = c.application.holder.eori,
    reference = c.reference,
    accountDetails = c.application.holder,
    contact = c.application.contact,
    dateSubmitted = c.createdDate,
    c.application.goodName,
    c.application.goodDescription
  )
}
