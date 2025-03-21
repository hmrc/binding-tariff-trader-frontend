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

package controllers

import com.google.inject.Inject
import config.FrontendAppConfig
import models.Languages.{English, Language}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

class LanguageSwitchController @Inject() (
  appConfig: FrontendAppConfig,
  cc: MessagesControllerComponents
) extends FrontendController(cc)
    with I18nSupport {

  def switchToLanguage(language: Language): Action[AnyContent] = Action { implicit request =>
    val redirectURL = request.headers.get(REFERER).getOrElse(fallbackURL)
    Redirect(redirectURL)
      .withLang(determineLanguageToUse(language).lang)
  }

  private def determineLanguageToUse(language: Language): Language =
    if (appConfig.languageTranslationEnabled) {
      language
    } else {
      English
    }

  private def fallbackURL: String = routes.IndexController.getApplicationsAndRulings(1, None, None).url
}
