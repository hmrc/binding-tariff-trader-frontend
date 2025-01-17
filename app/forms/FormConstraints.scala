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

import play.api.data.validation.{Constraint, Invalid, Valid}

object FormConstraints {

  private val eoriCodeRegex = "^[a-zA-Z0-9]{1,17}"
  private val eoriCodeError = "registerBusinessRepresenting.error.eoriNumber.format"

  val eoriCodeConstraint: Constraint[String] = Constraint("constraints.eoriFormat") {
    case s: String if s.isEmpty                => Valid
    case s: String if s.matches(eoriCodeRegex) => Valid
    case _: String                             => Invalid(eoriCodeError)
  }
}
