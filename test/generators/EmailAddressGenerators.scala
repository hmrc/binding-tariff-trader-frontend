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

package generators

import org.scalacheck.Gen
import org.scalacheck.Gen._

trait EmailAddressGenerators extends Generators {

  override def nonEmptyString: Gen[String] = nonEmptyListOf(alphaChar).map(_.mkString).filter(_.length > 1)

  protected def validMailbox: Gen[String] = nonEmptyString

  protected def validDomain: Gen[String] = for {
    topLevelDomain <- nonEmptyString
    otherParts <- nonEmptyListOf(nonEmptyString)
    result <- (topLevelDomain :: otherParts).mkString(".")
  } yield result

  protected def validEmailAddressesLongerThan(maxLength: Int): Gen[String] =
    for {
    mailbox <- validMailbox
    domain <- validDomain
    email = s"$mailbox@$domain"
    g <- email.suchThat(_.length > maxLength)
  } yield g

}
