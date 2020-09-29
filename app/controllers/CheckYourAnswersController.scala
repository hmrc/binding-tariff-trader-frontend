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
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.check_your_answers

class CheckYourAnswersController @Inject()(
                                            appConfig: FrontendAppConfig,
                                            authenticate: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            countriesService: CountriesService,
                                            cc: MessagesControllerComponents
                                          ) extends FrontendController(cc) with I18nSupport {

  private implicit val lang: Lang = appConfig.defaultLang

  def onPageLoad(): Action[AnyContent] = (authenticate andThen getData andThen requireData) { implicit request =>

    val checkYourAnswersHelper = new CheckYourAnswersHelper(request.userAnswers, countriesService.getAllCountries, messagesApi, lang)

    val sections = Seq(
      AnswerSection(
        Some("checkYourAnswers.aboutTheGoodsSection"),
        Seq(
          checkYourAnswersHelper.provideGoodsName,
          checkYourAnswersHelper.provideGoodsDescription,
          checkYourAnswersHelper.addConfidentialInformation,
          checkYourAnswersHelper.provideConfidentialInformation,
          checkYourAnswersHelper.supportingMaterialFileListChoice,
          checkYourAnswersHelper.supportingMaterialFileList,
          checkYourAnswersHelper.whenToSendSample,
          checkYourAnswersHelper.returnSamples,
          checkYourAnswersHelper.commodityCodeBestMatch,
          checkYourAnswersHelper.commodityCodeDigits,
          checkYourAnswersHelper.legalChallenge,
          checkYourAnswersHelper.legalChallengeDetails,
          checkYourAnswersHelper.supportingInformation,
          checkYourAnswersHelper.supportingInformationDetails
        ).flatten
      ),
      AnswerSection(
        Some("checkYourAnswers.aboutOtherRulingsSection"),
        Seq(
          checkYourAnswersHelper.selectApplicationType,
          checkYourAnswersHelper.previousCommodityCode,
          checkYourAnswersHelper.similarItemCommodityCode,
          checkYourAnswersHelper.commodityCodeRulingReference
        ).flatten
      ),
      AnswerSection(
        Some("checkYourAnswers.aboutTheApplicantSection"),
        Seq(
          checkYourAnswersHelper.enterContactDetails
        ).flatten
      )
    )

    Ok(check_your_answers(appConfig, sections))
  }

}
