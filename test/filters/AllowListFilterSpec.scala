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

package filters

import akka.stream.Materializer
import com.typesafe.config.ConfigException
import generators.Generators
import org.mockito.MockitoSugar
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.Configuration
import play.api.mvc.Call
import utils.UnitSpec

class AllowListFilterSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with MockitoSugar with Generators {

  val mockMaterializer = mock[Materializer]

  val otherConfigGen = Gen.mapOf[String, String](
    for {
      key   <- Gen.alphaNumStr suchThat (_.nonEmpty)
      value <- arbitrary[String]
    } yield (key, value)
  )

  "the list of allowed IP addresses" should {

    "must throw an exception" should {

      "when the underlying config value is not there" in {

        forAll(otherConfigGen, arbitrary[String], arbitrary[String]) { (otherConfig, destination, excluded) =>
          whenever(!otherConfig.contains("filters.allowlist.ips")) {

            val config = Configuration(
              (otherConfig +
                ("filters.allowlist.destination" -> destination) +
                ("filters.allowlist.excluded"    -> excluded)).toSeq: _*
            )

            assertThrows[ConfigException] {
              new AllowListFilter(config, mockMaterializer)
            }
          }
        }
      }
    }

    "must be empty" should {

      "when the underlying config value is empty" in {

        forAll(otherConfigGen, arbitrary[String], arbitrary[String]) { (otherConfig, destination, excluded) =>
          val config = Configuration(
            (otherConfig +
              ("filters.allowlist.destination" -> destination) +
              ("filters.allowlist.excluded"    -> excluded) +
              ("filters.allowlist.ips"         -> "")).toSeq: _*
          )

          val allowedlistFilter = new AllowListFilter(config, mockMaterializer)
          allowedlistFilter.allowlist shouldBe empty
        }
      }
    }

    "must contain all of the values" should {

      "when given a comma-separated list of values" in {

        val gen = Gen.nonEmptyListOf(Gen.alphaNumStr suchThat (_.nonEmpty))

        forAll(gen, otherConfigGen, arbitrary[String], arbitrary[String]) { (ips, otherConfig, destination, excluded) =>
          val ipString = ips.mkString(",")

          val config = Configuration(
            (otherConfig +
              ("filters.allowlist.destination" -> destination) +
              ("filters.allowlist.excluded"    -> excluded) +
              ("filters.allowlist.ips"         -> ipString)).toSeq: _*
          )

          val allowedlistFilter = new AllowListFilter(config, mockMaterializer)
          allowedlistFilter.allowlist should contain theSameElementsAs ips
        }
      }
    }
  }

  "the destination for non-allowed visitors" should {

    "must throw an exception" should {

      "when the underlying config value is not there" in {

        forAll(otherConfigGen, arbitrary[String], arbitrary[String]) { (otherConfig, ips, excluded) =>
          whenever(!otherConfig.contains("filters.allowlist.destination")) {

            val config = Configuration(
              (otherConfig +
                ("filters.allowlist.ips"      -> ips) +
                ("filters.allowlist.excluded" -> excluded)).toSeq: _*
            )

            assertThrows[ConfigException] {
              new AllowListFilter(config, mockMaterializer)
            }
          }
        }
      }
    }

    "must return a Call to the destination" in {

      forAll(otherConfigGen, arbitrary[String], arbitrary[String], arbitrary[String]) {
        (otherConfig, ips, destination, excluded) =>
          val config = Configuration(
            (otherConfig +
              ("filters.allowlist.ips"         -> ips) +
              ("filters.allowlist.excluded"    -> excluded) +
              ("filters.allowlist.destination" -> destination)).toSeq: _*
          )

          val allowedlistFilter = new AllowListFilter(config, mockMaterializer)
          allowedlistFilter.destination shouldBe Call("GET", destination)
      }
    }
  }

  "the list of excluded paths" should {

    "must throw an exception" should {

      "when the underlying config value is not there" in {

        forAll(otherConfigGen, arbitrary[String], arbitrary[String]) { (otherConfig, destination, ips) =>
          whenever(!otherConfig.contains("filters.allowlist.excluded")) {

            val config = Configuration(
              (otherConfig +
                ("filters.allowlist.destination" -> destination) +
                ("filters.allowlist.ips"         -> ips)).toSeq: _*
            )

            assertThrows[ConfigException] {
              new AllowListFilter(config, mockMaterializer)
            }
          }
        }
      }
    }

    "must return Calls to all of the values" should {

      "when given a comma-separated list of values" in {

        val gen = Gen.nonEmptyListOf(Gen.alphaNumStr suchThat (_.nonEmpty))

        forAll(gen, otherConfigGen, arbitrary[String], arbitrary[String]) {
          (excludedPaths, otherConfig, destination, ips) =>
            val excludedPathString = excludedPaths.mkString(",")

            val config = Configuration(
              (otherConfig +
                ("filters.allowlist.destination" -> destination) +
                ("filters.allowlist.excluded"    -> excludedPathString) +
                ("filters.allowlist.ips"         -> ips)).toSeq: _*
            )

            val expectedCalls = excludedPaths.map(Call("GET", _))

            val allowedlistFilter = new AllowListFilter(config, mockMaterializer)
            allowedlistFilter.excludedPaths should contain theSameElementsAs expectedCalls
        }
      }
    }
  }
}
