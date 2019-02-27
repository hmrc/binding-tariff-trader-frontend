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
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.NormalMode
import navigation.Navigator
import pages.DeclarationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.check_your_answers

import scala.concurrent.Future

class CheckYourAnswersController @Inject()(appConfig: FrontendAppConfig,
                                           override val messagesApi: MessagesApi,
                                           navigator: Navigator,
                                           authenticate: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (authenticate andThen getData andThen requireData) { implicit request =>

    val checkYourAnswersHelper = new CheckYourAnswersHelper(request.userAnswers, messagesApi)

    val sections = Seq(
      AnswerSection(
        Some("checkYourAnswers.applicantRegisteredSection"),
        Seq(
          checkYourAnswersHelper.registeredAddressForEori,
          checkYourAnswersHelper.enterContactDetails,
          checkYourAnswersHelper.whichBestDescribesYou
        ).flatten
      ),
      AnswerSection(
        Some("checkYourAnswers.applicantOtherBusiness"),
        Seq(
          checkYourAnswersHelper.registerBusinessRepresenting,
          checkYourAnswersHelper.uploadWrittenAuthorisation
        ).flatten
      ),
      AnswerSection(
        Some("checkYourAnswers.informationAboutYourItemSection"),
        Seq(
          checkYourAnswersHelper.selectApplicationType,
          checkYourAnswersHelper.previousCommodityCode,
          checkYourAnswersHelper.informationAboutYourItem,
          checkYourAnswersHelper.confidentialInformation,
          checkYourAnswersHelper.describeYourItem,
          checkYourAnswersHelper.supportingMaterialFileList,
          checkYourAnswersHelper.commodityCodeBestMatch,
          checkYourAnswersHelper.commodityCodeDigits,
          checkYourAnswersHelper.whenToSendSample,
          checkYourAnswersHelper.returnSamples,
          checkYourAnswersHelper.similarItemCommodityCode,
          checkYourAnswersHelper.commodityCodeRulingReference,
          checkYourAnswersHelper.legalChallenge,
          checkYourAnswersHelper.legalChallengeDetails
        ).flatten
      ), AnswerSection(
        Some("checkYourAnswers.otherInformation"),
        Seq(
          checkYourAnswersHelper.supportingInformation,
          checkYourAnswersHelper.supportingInformationDetails
        ).flatten
      )
    )

    Ok(check_your_answers(appConfig, sections))
  }

  def onSubmit(): Action[AnyContent] = (authenticate andThen getData andThen requireData).async { implicit request =>
    Future.successful(
      Redirect(navigator.nextPage(DeclarationPage, NormalMode)(request.userAnswers))
    )
  }

}
