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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject() (
  val runModeConfiguration: Configuration,
  serviceConfig: ServicesConfig
) {

  private def loadConfig(key: String): String =
    runModeConfiguration.getOptional[String](key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  lazy val helpMakeGovUkBetterUrl: String     = runModeConfiguration.get[String]("urls.helpMakeGovUkBetterUrl")
  lazy val displayMakeGovUkBetterUrl: Boolean = runModeConfiguration.get[Boolean]("toggle.displayResearchBanner")

  lazy val loginUrl: String                       = loadConfig("urls.login")
  lazy val loginContinueUrl: String               = loadConfig("urls.loginContinue")
  lazy val bindingTariffClassificationUrl: String = serviceConfig.baseUrl("binding-tariff-classification")
  lazy val bindingTariffFileStoreUrl: String      = serviceConfig.baseUrl("binding-tariff-filestore")
  lazy val emailUrl: String                       = serviceConfig.baseUrl("email")

  lazy val fileUploadMaxFiles: Int          = loadConfig("fileupload.maxFiles").toInt
  lazy val fileUploadMaxSize: Int           = loadConfig("fileupload.maxSize").toInt
  lazy val fileUploadMimeTypes: Set[String] = loadConfig("fileupload.mimeTypes").split(",").map(_.trim).toSet

  lazy val languageTranslationEnabled: Boolean =
    runModeConfiguration.getOptional[Boolean]("microservice.services.features.welsh-translation").getOrElse(true)

  private lazy val eoriCommonComponentUrl = loadConfig("eori-common-component-frontend.host")
  lazy val eoriCommonComponentSubscribeUrl: String =
    s"$eoriCommonComponentUrl/customs-enrolment-services/atar/subscribe"

  private lazy val bindingTariffRulingsUrl     = loadConfig("binding-tariff-ruling-frontend.host")
  lazy val bindingTariffRulingsHomeUrl: String = s"$bindingTariffRulingsUrl/search-for-advance-tariff-rulings"

  private lazy val businessTaxAccountHost: String = loadConfig("business-tax-account.host")
  lazy val businessTaxAccountUrl: String          = s"$businessTaxAccountHost/business-account"

  private lazy val feedbackUrl: String         = loadConfig("feedback-frontend.host")
  private lazy val feedbackServiceName: String = "ABTIR"
  lazy val feedbackSurvey: String              = s"$feedbackUrl/feedback/$feedbackServiceName"

  private lazy val basGatewayBaseUrl: String = loadConfig("bas-gateway.host")
  lazy val signOutUrl: String                     = s"$basGatewayBaseUrl/bas-gateway/sign-out-without-state"

  lazy val apiToken: String = loadConfig("auth.api-token")

  lazy val aesKey: String = loadConfig("pdfService.aes-key")

  private val timeOutSecondsAlternative          = 780
  private val timeOutCountDownSecondsAlternative = 120

  lazy val timeOutSeconds: Int =
    runModeConfiguration.getOptional[Int]("timeoutDialog.timeoutSeconds").getOrElse(timeOutSecondsAlternative)
  lazy val timeOutCountDownSeconds: Int = runModeConfiguration
    .getOptional[Int]("timeoutDialog.time-out-countdown-seconds")
    .getOrElse(timeOutCountDownSecondsAlternative)
  lazy val extendedTimeOutInSeconds: Int = timeOutSeconds + timeOutCountDownSeconds + 60

  lazy val host: String = loadConfig("host")

  lazy val samplesToggle: Boolean =
    runModeConfiguration.getOptional[Boolean]("toggle.samplesNotAccepted").getOrElse(false)

  lazy val migrationWorkerEnabled: Boolean =
    runModeConfiguration.getOptional[Boolean]("migration-worker.enabled").getOrElse(false)
}
