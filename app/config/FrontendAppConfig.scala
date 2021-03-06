/*
 * Copyright 2021 HM Revenue & Customs
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
import models.Languages.English
import play.api.Configuration
import play.api.i18n.Lang
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject()(
                                   val runModeConfiguration: Configuration,
                                   serviceConfig: ServicesConfig
                                 ) {

  private def loadConfig(key: String): String = {
    runModeConfiguration.getOptional[String](key).getOrElse(throw new Exception(s"Missing configuration key: $key"))
  }

  private lazy val contactHost = runModeConfiguration.getOptional[String]("contact-frontend.host").getOrElse("")
  private val contactFormServiceIdentifier = "AdvanceTariffApplication"

  lazy val analyticsToken: String = loadConfig("google-analytics.token")
  lazy val analyticsHost: String = loadConfig("google-analytics.host")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  lazy val authUrl: String = serviceConfig.baseUrl("auth")
  lazy val loginUrl: String = loadConfig("urls.login")
  lazy val loginContinueUrl: String = loadConfig("urls.loginContinue")
  lazy val bindingTariffClassificationUrl: String = serviceConfig.baseUrl("binding-tariff-classification")
  lazy val bindingTariffFileStoreUrl: String = serviceConfig.baseUrl("binding-tariff-filestore")
  lazy val emailUrl: String = serviceConfig.baseUrl("email")
  lazy val pdfGeneratorUrl: String = serviceConfig.baseUrl("pdf-generator-service")

  lazy val accessibilityBaseUrl: String = loadConfig(s"accessibility-statement.baseUrl")
  lazy private val accessibilityRedirectUrl: String = loadConfig(s"accessibility-statement.redirectUrl")
  def accessibilityStatementUrl(referrer: String) =
    s"$accessibilityBaseUrl/accessibility-statement$accessibilityRedirectUrl?referrerUrl=${SafeRedirectUrl(
      accessibilityBaseUrl + referrer).encodedUrl}"

  lazy val fileUploadMaxFiles: Int = loadConfig("fileupload.maxFiles").toInt
  lazy val fileUploadMaxSize: Int = loadConfig("fileupload.maxSize").toInt
  lazy val fileUploadMimeTypes: Set[String] = loadConfig("fileupload.mimeTypes").split(",").map(_.trim).toSet

  lazy val languageTranslationEnabled: Boolean = runModeConfiguration.getOptional[Boolean]("microservice.services.features.welsh-translation").getOrElse(true)

  private lazy val cdsUrl: String = loadConfig("customs-frontend.host")
  lazy val cdsSubscribeUrl: String = s"$cdsUrl/customs/subscribe-for-cds"
  lazy val cdsRegisterUrl: String = s"$cdsUrl/customs/register-for-cds"

  private lazy val eoriCommonComponentUrl = loadConfig("eori-common-component-frontend.host")
  lazy val atarSubscribeUrl: String = s"$eoriCommonComponentUrl/customs-enrolment-services/atar/subscribe"

  private lazy val bindingTariffRulingsUrl = loadConfig("binding-tariff-ruling-frontend.host")
  lazy val bindingTariffRulingsHomeUrl: String = s"$bindingTariffRulingsUrl/search-for-advance-tariff-rulings"

  private lazy val feedbackUrl: String = loadConfig("feedback-frontend.host")
  private lazy val feedbackServiceName: String = "ABTIR"
  lazy val feedbackSurvey: String = s"$feedbackUrl/feedback/$feedbackServiceName"

  lazy val apiToken: String = loadConfig("auth.api-token")
  lazy val aesKey: String = loadConfig("auth.aes-key")

  lazy val timeOutSeconds: Int = runModeConfiguration.getOptional[Int]("timeoutDialog.timeoutSeconds").getOrElse(780)
  lazy val timeOutCountDownSeconds: Int = runModeConfiguration.getOptional[Int]("timeoutDialog.time-out-countdown-seconds").getOrElse(120)
  lazy val refreshInterval: Int = timeOutSeconds + 10
  lazy val enableRefresh: Boolean = runModeConfiguration.getOptional[Boolean]("timeoutDialog.enableRefresh").getOrElse(true)

  def defaultLang: Lang = English.lang

  lazy val host: String = loadConfig("host")

  lazy val assetsPrefix = runModeConfiguration.get[String](s"assets.url") + runModeConfiguration
    .get[String](s"assets.version") + '/'

  lazy val samplesToggle = runModeConfiguration.getOptional[Boolean]("toggle.samplesNotAccepted").getOrElse(false)

  lazy val migrationWorkerEnabled = runModeConfiguration.getOptional[Boolean]("migration-worker.enabled").getOrElse(false)
}
