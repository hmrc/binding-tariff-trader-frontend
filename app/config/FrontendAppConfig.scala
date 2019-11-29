/*
 * Copyright 2019 HM Revenue & Customs
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
import controllers.routes
import play.api.Mode
import play.api.i18n.Lang
import play.api.mvc.Call
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject() (val runModeConfiguration: Configuration, environment: Environment, val config: ServicesConfig){

  protected def mode: Mode = environment.mode

  private def loadConfig(key: String) = config.getString(key)
  private lazy val contactHost = loadConfig("contact-frontend.host")

  private val contactFormServiceIdentifier = "BindingTariffApplication"

  lazy val analyticsToken: String = loadConfig("google-analytics.token")
  lazy val analyticsHost: String = loadConfig("google-analytics.host")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  lazy val reportAccessibilityProblemDefaultUrl = loadConfig("urls.reportAccessibilityProblemDefaultUrl")
  lazy val testedOnDate = loadConfig("accessibility.testedOnDate")
  lazy val preparedOnDate = loadConfig("accessibility.preparedOnDate")
  lazy val lastUpdatedDate = loadConfig("accessibility.lastUpdatedDate")
  lazy val govukAccessibilityUrl = controllers.routes.AccessibilityController.onPageLoad().url
  lazy val subdomainUrl = "https://www.tax.service.gov.uk/report-import-vat-parcels/"


  lazy val authUrl: String = config.baseUrl("auth")
  lazy val loginUrl: String = loadConfig("urls.login")
  lazy val loginContinueUrl: String = loadConfig("urls.loginContinue")
  lazy val bindingTariffClassificationUrl: String = config.baseUrl("binding-tariff-classification")
  lazy val bindingTariffFileStoreUrl: String = config.baseUrl("binding-tariff-filestore")
  lazy val emailUrl: String = config.baseUrl("email")
  lazy val pdfGeneratorUrl: String = config.baseUrl("pdf-generator-service")
  lazy val isCdsEnrolmentCheckEnabled: Boolean = config.getBoolean("cdsEnrolmentCheckEnabled")

  lazy val fileUploadMaxSize: Int = loadConfig("fileupload.maxSize").toInt
  lazy val fileUploadMimeTypes: Set[String] = loadConfig("fileupload.mimeTypes").split(",").map(_.trim).toSet

  lazy val languageTranslationEnabled: Boolean = config.getConfBool("microservice.services.features.welsh-translation", true)

  private lazy val cdsUrl: String = loadConfig("customs-frontend.host")
  lazy val cdsSubscribeUrl: String = s"$cdsUrl/customs/subscribe-for-cds"
  lazy val cdsRegisterUrl: String = s"$cdsUrl/customs/register-for-cds"

  private lazy val feedbackUrl: String = loadConfig("feedback-frontend.host")
  private lazy val feedbackServiceName : String = "ABTIR"
  lazy val feedbackSurvey: String = s"$feedbackUrl/feedback/$feedbackServiceName"

  lazy val apiToken: String = loadConfig("auth.api-token")
  lazy val aesKey: String = loadConfig("auth.aes-key")

  lazy val timeOutSeconds : Int = config.getConfInt("timeoutDialog.timeoutSeconds", 780)
  lazy val timeOutCountDownSeconds: Int = config.getConfInt("timeoutDialog.time-out-countdown-seconds",120)
  lazy val refreshInterval: Int = timeOutSeconds + 10
  lazy val enableRefresh: Boolean= config.getConfBool("timeoutDialog.enableRefresh", true)

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call = { lang: String =>
    routes.LanguageSwitchController.switchToLanguage(lang)
  }

}
