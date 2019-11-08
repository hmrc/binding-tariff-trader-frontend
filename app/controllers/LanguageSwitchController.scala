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

package controllers

import com.google.inject.Inject
import config.FrontendAppConfig
import play.api.{Application, Configuration}
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.play.language.LanguageController

// TODO: upstream this into the Play framework
class LanguageSwitchController @Inject() (
                                           configuration: Configuration,
                                           appConfig: FrontendAppConfig
                                         )(implicit val application: Application, override implicit val messagesApi: MessagesApi) extends LanguageController with I18nSupport {

  override def fallbackURL: String = routes.IndexController.getApplications().url

  override def languageMap: Map[String, Lang] = appConfig.languageMap

  private def isWelshEnabled: Boolean = {
    appConfig.config.getBoolean("microservice.services.features.welsh-translation")
  }

}
