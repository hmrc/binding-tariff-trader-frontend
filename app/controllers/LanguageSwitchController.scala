/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers

import com.google.inject.Inject
import config.FrontendAppConfig
import play.api.Configuration
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Flash}
import uk.gov.hmrc.play.language.{LanguageController, LanguageUtils}

// TODO: upstream this into the Play framework
class LanguageSwitchController @Inject() (
                                           configuration: Configuration,
                                           appConfig: FrontendAppConfig,
                                           val languageUtils: LanguageUtils,
                                           cc: ControllerComponents,
                                           override implicit val messagesApi: MessagesApi,
                                           implicit val lang: Lang
                                         ) extends LanguageController(configuration, languageUtils, cc) with I18nSupport {

  override def switchToLanguage(language: String): Action[AnyContent] = Action { implicit request =>
    val enabled = isWelshEnabled
    val targetLang = if (enabled) {
      languageMap.getOrElse(language, languageUtils.getCurrentLang)
    } else {
      Lang("en")
    }
    val redirectURL = request.headers.get(REFERER).getOrElse(fallbackURL)

    val SwitchIndicatorKey = "switching-language"
    val FlashWithSwitchIndicator = Flash(Map(SwitchIndicatorKey -> "true"))

    Redirect(redirectURL).withLang(Lang.apply(targetLang.code)).flashing(FlashWithSwitchIndicator)
  }

  private def isWelshEnabled: Boolean = {
    configuration.getOptional[Boolean]("microservice.services.features.welsh-translation").getOrElse(true)
  }

  override protected def fallbackURL: String = routes.IndexController.getApplications().url

  override protected def languageMap: Map[String, Lang] = appConfig.languageMap
}
