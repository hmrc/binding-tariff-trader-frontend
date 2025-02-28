/*
 * Copyright 2025 HM Revenue & Customs
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

package forms

import forms.mappings.{Constraints, Mappings}
import models.EnterContactDetails
import play.api.data.Form
import play.api.data.Forms._

import javax.inject.Inject

class EnterContactDetailsFormProvider @Inject() extends Mappings with Constraints {

  val telephoneRegex                      = """^\+?[-0-9\s\(\)./]{1,20}$"""
  private val maximumValueForNameAndEmail = 100
  private val maximumValueForPhoneNumber  = 20
  private val minimumValueForPhoneNumber  = 10

  def apply(): Form[EnterContactDetails] = Form(
    mapping(
      "name" -> text("enterContactDetails.error.name.required")
        .verifying(maxLength(maximumValueForNameAndEmail, "enterContactDetails.error.name.length")),
      "email" -> text("enterContactDetails.error.email.required")
        .verifying(validEmailAddress("enterContactDetails.error.email.invalid"))
        .verifying(maxLength(maximumValueForNameAndEmail, "enterContactDetails.error.email.length")),
      "phoneNumber" -> text("enterContactDetails.error.phoneNumber.required")
        .verifying(maxLength(maximumValueForPhoneNumber, "enterContactDetails.error.phoneNumber.length"))
        .verifying(regexp(telephoneRegex, "enterContactDetails.error.phoneNumber.invalid"))
    )(EnterContactDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
  )

  def formWithMinTelNumber: Form[EnterContactDetails] = Form(
    mapping(
      "name" -> text("enterContactDetails.error.name.required")
        .verifying(maxLength(maximumValueForNameAndEmail, "enterContactDetails.error.name.length")),
      "email" -> text("enterContactDetails.error.email.required")
        .verifying(validEmailAddress("enterContactDetails.error.email.invalid"))
        .verifying(maxLength(maximumValueForNameAndEmail, "enterContactDetails.error.email.length")),
      "phoneNumber" -> text("enterContactDetails.error.phoneNumber.required")
        .verifying(maxLength(maximumValueForPhoneNumber, "enterContactDetails.error.phoneNumber.length"))
        .verifying(minNumberLength(minimumValueForPhoneNumber, "enterContactDetails.error.phoneNumber.minLength"))
        .verifying(regexp(telephoneRegex, "enterContactDetails.error.phoneNumber.invalid"))
    )(EnterContactDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
  )
}
