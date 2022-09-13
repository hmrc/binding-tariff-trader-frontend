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

package unit.utils

import base.SpecBase
import play.api.i18n.{Lang, Messages}
import utils.Dates

import java.time.{Instant, LocalDateTime, Month, ZoneOffset}

class DatesSpec extends SpecBase {

  private def cyMessages: Messages = messagesApi.preferred(Seq(Lang("cy")))

  private val day: Int  = 10
  private val year: Int = 2021

  private val date: Instant = LocalDateTime.of(year, Month.AUGUST, day, 0, 0).toInstant(ZoneOffset.UTC)

  "format" should {
    "return the formatted date in the dd MMM YYYY fromat in english" in {
      Dates.format(date)(messages) shouldBe "10 Aug 2021"
    }

    "return the formatted date in the dd MMM YYYY fromat in welsh" in {
      Dates.format(date)(cyMessages) shouldBe "10 Awst 2021"
    }
  }

  "formatForPDF" should {
    "return the formatted date in the dd MMM YYYY format" in {
      Dates.formatForPdf(date) shouldBe "10/08/2021"
    }
  }

  //added to ensure each month in welsh is generated correctly
  "Have each month in welsh" which {
    "Jan should be Ion" in {
      cyMessages.apply("site.month.1") shouldBe "Ion"
    }

    "Feb should be Chwef" in {
      cyMessages.apply("site.month.2") shouldBe "Chwef"
    }

    "Mar should be Maw" in {
      cyMessages.apply("site.month.3") shouldBe "Maw"
    }

    "Apr should be Ebr" in {
      cyMessages.apply("site.month.4") shouldBe "Ebr"
    }

    "May should be Mai" in {
      cyMessages.apply("site.month.5") shouldBe "Mai"
    }

    "Jun should be Meh" in {
      cyMessages.apply("site.month.6") shouldBe "Meh"
    }

    "Jul should be Gorff" in {
      cyMessages.apply("site.month.7") shouldBe "Gorff"
    }

    "Aug should be Awst" in {
      cyMessages.apply("site.month.8") shouldBe "Awst"
    }

    "Sep should be Medi" in {
      cyMessages.apply("site.month.9") shouldBe "Medi"
    }

    "Oct should be Hyd" in {
      cyMessages.apply("site.month.10") shouldBe "Hyd"
    }

    "Nov should be Tach" in {
      cyMessages.apply("site.month.11") shouldBe "Tach"
    }

    "Dec should be Rhag" in {
      cyMessages.apply("site.month.12") shouldBe "Rhag"
    }
  }
}
