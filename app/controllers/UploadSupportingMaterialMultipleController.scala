/*
 * Copyright 2018 HM Revenue & Customs
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
import forms.UploadSupportingMaterialMultipleFormProvider
import javax.inject.Inject
import models.response.UploadFileResponse
import models.{FileToSave, Mode, UserAnswers}
import navigation.Navigator
import pages.{CommodityCodeBestMatchPage, UploadSupportingMaterialMultiplePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.Files
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MultipartFormData}
import service.FileService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.uploadSupportingMaterialMultiple

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UploadSupportingMaterialMultipleController @Inject()(
                                                            appConfig: FrontendAppConfig,
                                                            override val messagesApi: MessagesApi,
                                                            dataCacheConnector: DataCacheConnector,
                                                            navigator: Navigator,
                                                            identify: IdentifierAction,
                                                            getData: DataRetrievalAction,
                                                            requireData: DataRequiredAction,
                                                            formProvider: UploadSupportingMaterialMultipleFormProvider,
                                                            fileService: FileService
                                                          ) extends FrontendController with I18nSupport {

  val form = formProvider()

  implicit val fileTosaveFormatter = Json.format[FileToSave]

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

//      val preparedForm = request.userAnswers.get(UploadSupportingMaterialMultiplePage) match {
//        case None => form
//        case Some(value) => form.fill(value)
//      }
//
    //  Ok(uploadSupportingMaterialMultiple(appConfig, preparedForm, mode))

      Ok(uploadSupportingMaterialMultiple(appConfig, form, mode))
  }


  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val multiPartFiles: Seq[MultipartFormData.FilePart[Files.TemporaryFile]] = request.body.asMultipartFormData.get.files

      val filesToPersist: Future[Seq[FileToSave]] = Future.sequence(multiPartFiles.map { f =>
        fileService.uploadFile(f)
      }).map { responses: Seq[UploadFileResponse] =>
        responses.map { r: UploadFileResponse =>
          FileToSave(r.id, r.fileName)
        }
      }

      Future.sequence(filesToPersist).map{


      }


      // Validation doesnt work
      // Future.successful(BadRequest(uploadSupportingMaterialMultiple(appConfig, formWithErrors, mode)))


      //      val results = for ( saved <- filesToPersist) yield saved

       val futures = filesToPersist.map { r: Seq[FileToSave] =>
        val existingFiles = request.userAnswers.get(UploadSupportingMaterialMultiplePage).getOrElse(Seq.empty)
        val updatedFiles = existingFiles :+ r

//        //request.userAnswers.set(UploadSupportingMaterialMultiplePage, r)
//
//        request.userAnswers.set(UploadSupportingMaterialMultiplePage, r)
      }

//      request.userAnswers.set(UploadSupportingMaterialMultiplePage, r)



      val updatedAnswers = request.userAnswers.set(UploadSupportingMaterialMultiplePage, Seq(FileToSave("Nothing","Yet")))

      dataCacheConnector.save(updatedAnswers.cacheMap).map(
        _ =>
          Redirect(navigator.nextPage(CommodityCodeBestMatchPage, mode)(updatedAnswers))
      )


  }
}
