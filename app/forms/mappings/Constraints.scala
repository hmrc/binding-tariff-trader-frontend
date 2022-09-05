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

package forms.mappings

import org.apache.commons.validator.routines.EmailValidator
import play.api.data.validation.{Constraint, Invalid, Valid}

trait Constraints {

  private val postCodeMaxLength = 19

  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint { input: A =>
      constraints
        .map(_.apply(input))
        .find(_ != Valid)
        .getOrElse(Valid)
    }

  protected def minimumValue[A](minimum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input: A =>
      import ev._

      if (input >= minimum) {
        Valid
      } else {
        Invalid(errorKey, minimum)
      }
    }

  protected def maximumValue[A](maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input: A =>
      import ev._

      if (input <= maximum) {
        Valid
      } else {
        Invalid(errorKey, maximum)
      }
    }

  protected def inRange[A](minimum: A, maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input: A =>
      import ev._

      if (input >= minimum && input <= maximum) {
        Valid
      } else {
        Invalid(errorKey, minimum, maximum)
      }
    }

  protected def regexp(regex: String, errorKey: String): Constraint[String] =
    Constraint {
      case str: String if str.matches(regex) => Valid
      case _                                 => Invalid(errorKey)
    }

  protected def maxLength(maximum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str: String if str.length <= maximum => Valid
      case _                                    => Invalid(errorKey, maximum)
    }

  protected def minLength(minimum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str: String if str.length >= minimum => Valid
      case _                                    => Invalid(errorKey, minimum)
    }

  protected def minNumberLength(minimum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str: String if str.replaceAll("[^0-9]", "").length >= minimum => Valid
      case _                                                             => Invalid(errorKey, minimum)
    }

  protected def optionalPostCodeMaxLength(errorKey: String): Constraint[Option[String]] =
    optionalMaxLength(postCodeMaxLength, errorKey)

  protected def optionalMaxLength(maximum: Int, errorKey: String): Constraint[Option[String]] =
    Constraint {
      case None                                       => Valid
      case Some(str: String) if str.length <= maximum => Valid
      case _                                          => Invalid(errorKey, maximum)
    }

  protected def validEmailAddress(errorKey: String): Constraint[String] =
    Constraint {
      case str: String if EmailValidator.getInstance().isValid(str) => Valid
      case _                                                        => Invalid(errorKey)
    }

}
