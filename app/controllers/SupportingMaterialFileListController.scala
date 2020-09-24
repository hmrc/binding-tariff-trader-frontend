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
import forms.SupportingMaterialFileListFormProvider
import javax.inject.Inject
import models.requests.DataRequest
import models.{FileAttachment, Mode, UserAnswers}
import navigation.Navigator
import pages._
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.supportingMaterialFileList

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.successful
import utils.JsonFormatters._

class SupportingMaterialFileListController @Inject()(appConfig: FrontendAppConfig,
                                                     dataCacheConnector: DataCacheConnector,
                                                     navigator: Navigator,
                                                     identify: IdentifierAction,
                                                     getData: DataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     formProvider: SupportingMaterialFileListFormProvider,
                                                     cc: MessagesControllerComponents
                                                    ) extends FrontendController(cc) with I18nSupport {

  private lazy val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    Ok(supportingMaterialFileList(appConfig, form, existingFiles.fileAttachments, mode))
  }

  private def existingFiles(implicit request: DataRequest[AnyContent]): FileListAnswers = {
    request.userAnswers.get(SupportingMaterialFileListPage).getOrElse(FileListAnswers(false,Seq.empty))
  }

  def onRemove(fileId: String, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val removedFile = FileListAnswers(false, existingFiles.fileAttachments.filter(_.id != fileId).seq)
    val answers: UserAnswers = request.userAnswers.set(SupportingMaterialFileListPage, removedFile)
    dataCacheConnector.save(answers.cacheMap)
      .map(_ => Redirect(routes.SupportingMaterialFileListController.onPageLoad(mode)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    def defaultCachePageAndRedirect: Future[Result] = {

      val updatedAnswers: Future[UserAnswers] = request.userAnswers.get(SupportingMaterialFileListPage) match {
        case None =>
          val updatedAnswers = request.userAnswers.set(SupportingMaterialFileListPage, Seq.empty)
          dataCacheConnector.save(updatedAnswers.cacheMap).map(_ => updatedAnswers)
        case _ => successful(request.userAnswers)
      }

      updatedAnswers.map { userAnswers =>
        Redirect(navigator.nextPage(CommodityCodeBestMatchPage, mode)(userAnswers))
      }
    }

    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        successful(BadRequest(supportingMaterialFileList(appConfig, formWithErrors, existingFiles.fileAttachments, mode))),
      { selection =>

        val currentAnswers = request.userAnswers.get(SupportingMaterialFileListPage).map{
          answers => answers.copy(addAnotherDecision = selection, fileAttachments = answers.fileAttachments)
        }.getOrElse(FileListAnswers(selection, Seq.empty))
        val updatedAnswers = request.userAnswers.set(SupportingMaterialFileListPage, currentAnswers)
        dataCacheConnector.save(updatedAnswers.cacheMap).map(_ => updatedAnswers).map {
          savedAnswers => Redirect(navigator.nextPage(SupportingMaterialFileListPage,mode)(savedAnswers))
        }
      }
    )
  }

}
