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

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import forms.MakeFileConfidentialFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.makeFileConfidential

import scala.concurrent.{ExecutionContext, Future}

class MakeFileConfidentialController @Inject()(
                                                appConfig: FrontendAppConfig,
                                                dataCacheConnector: DataCacheConnector,
                                                navigator: Navigator,
                                                identify: IdentifierAction,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                formProvider: MakeFileConfidentialFormProvider,
                                                cc: MessagesControllerComponents
                                              )(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport {

  val form = formProvider()

  def onPageLoad(fileId: String, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request => Ok(makeFileConfidential(appConfig, form, mode, fileId))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(makeFileConfidential(appConfig, formWithErrors, mode, formWithErrors.data("fileId")))),
        (fileConfidentiality) => {
          val existingAnswers = request.userAnswers.get(MakeFileConfidentialPage).getOrElse(Map.empty)
          val updatedAnswers = request.userAnswers.set(MakeFileConfidentialPage, existingAnswers + (fileConfidentiality.fileId -> fileConfidentiality.confidential))

          dataCacheConnector.save(updatedAnswers.cacheMap).map {
            _ => Redirect(navigator.nextPage(MakeFileConfidentialPage, mode)(updatedAnswers))
          }
        }
      )
  }
}
