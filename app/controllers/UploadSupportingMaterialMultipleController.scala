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
import connectors.{ DataCacheConnector, UpscanConnector }
import controllers.actions._
import javax.inject.Inject
import models.{FileAttachment, Mode}
import pages._
import play.api.data.{ Form, Forms, FormError }
import play.api.data.format.Formats._
import play.api.i18n.{I18nSupport, Lang}
import play.api.libs.Files.TemporaryFile
import play.api.mvc._
import service.FileService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.uploadSupportingMaterialMultiple

import scala.concurrent.Future
import models.upscan.UploadSettings
import scala.concurrent.ExecutionContext
import forms.UploadSupportingMaterialMultipleFormProvider

class UploadSupportingMaterialMultipleController @Inject()(
                                                            appConfig: FrontendAppConfig,
                                                            dataCacheConnector: DataCacheConnector,
                                                            identify: IdentifierAction,
                                                            getData: DataRetrievalAction,
                                                            requireData: DataRequiredAction,
                                                            upscanConnector: UpscanConnector,
                                                            formProvider: UploadSupportingMaterialMultipleFormProvider,
                                                            fileService: FileService,
                                                            cc: MessagesControllerComponents
                                                          )(implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport {

  private lazy val form = formProvider()
  private implicit val lang: Lang = appConfig.defaultLang

  def onPageLoad(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      upscanConnector.initiate(UploadSettings(s"${appConfig.bindingTariffFileStoreUrl}/file")).map { initiateResponse =>
        Ok(uploadSupportingMaterialMultiple(appConfig, initiateResponse, form, mode))
      }
    }

  def onSubmit(mode: Mode): Action[MultipartFormData[TemporaryFile]] = 
    (identify andThen getData andThen requireData).async(parse.multipartFormData) { implicit request =>
      Future.successful(Ok)
    }
}
