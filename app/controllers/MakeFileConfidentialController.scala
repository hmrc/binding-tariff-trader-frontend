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
import models.{FileConfidentialityMapping, Mode}
import navigation.Navigator
import pages._
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class MakeFileConfidentialController @Inject()(appConfig: FrontendAppConfig,
                                               dataCacheConnector: DataCacheConnector,
                                               navigator: Navigator,
                                               identify: IdentifierAction,
                                               getData: DataRetrievalAction,
                                               requireData: DataRequiredAction,
                                               formProvider: MakeFileConfidentialFormProvider,
                                               val makeFileConfidential: views.html.makeFileConfidential,
                                               cc: MessagesControllerComponents
                                                    )(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val files = request.userAnswers.get(SupportingMaterialFileListPage).getOrElse(throw new IllegalStateException("No files found on user answers"))          //TODO: BT: factor out message
      val preparedForm = form.fill(FileConfidentialityMapping(fileId = files.last.id, confidential = false))

      Ok(makeFileConfidential(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (identify andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(makeFileConfidential(appConfig, formWithErrors, mode))),

        (value) => {
          val updatedAnswers = request.userAnswers.get(MakeFileConfidentialPage) match {
            case Some(first :+ last) if last.fileId == value.fileId =>
              request.userAnswers.set(MakeFileConfidentialPage, first :+ last.copy(confidential = value.confidential))
            case Some(list) =>
              request.userAnswers.set(MakeFileConfidentialPage, list :+ value)
            case None =>
              request.userAnswers.set(MakeFileConfidentialPage, Seq(value))
          }

          dataCacheConnector.save(updatedAnswers.cacheMap).map(
            _ =>
              Redirect(navigator.nextPage(MakeFileConfidentialPage, mode)(updatedAnswers))
          )
        }
      )
  }
}
