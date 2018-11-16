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

package models


case class Application
(
  `type`: String = "BTI",
  holder: EORIDetails,
  contact: Contact,
  agent: Option[EORIDetails],
  offline: Boolean,
  goodName: String,
  goodDescription: String,
  confidentialInformation: Option[String],
  otherInformation: Option[String],
  reissuedBTIReference: Option[String],
  relatedBTIReference: Option[String],
  knownLegalProceedings: Option[String],
  envisagedCommodityCode: Option[String],
  sampleToBeProvided: Boolean,
  sampleToBeReturned: Boolean
)

case class EORIDetails
(
  eori: String,
  traderName: String,
  addressLine1: String,
  addressLine2: String,
  addressLine3: String,
  postcode: String,
  country: String
)

case class Contact
(
  name: String,
  email: String,
  phone: String
)
