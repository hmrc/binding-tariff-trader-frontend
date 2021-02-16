/*
 * Copyright 2021 HM Revenue & Customs
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

package workers

import akka.actor.ActorSystem
import akka.stream.{ActorAttributes, Supervision}
import akka.stream.scaladsl.{Sink, Source}
import connectors.InjectAuthHeader
import config.FrontendAppConfig
import java.nio.file.{Files, StandardOpenOption}
import java.time.{Clock, ZonedDateTime}
import javax.inject.{Inject, Singleton}
import models._
import org.joda.time.Duration
import play.api.Logging
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.libs.Files.{TemporaryFile, TemporaryFileCreator}
import repositories.LockRepoProvider
import service.{CasesService, CountriesService, FileService, PdfService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.lock.ExclusiveTimePeriodLock
import viewmodels.{FileView, PdfViewModel}
import views.html.components.view_application_pdf

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class MigrationWorker @Inject() (
  appConfig: FrontendAppConfig,
  lockRepo: LockRepoProvider,
  casesService: CasesService,
  countriesService: CountriesService,
  fileService: FileService,
  pdfService: PdfService,
  messagesApi: MessagesApi,
  tempFileCreator: TemporaryFileCreator,
  clock: Clock
)(implicit system: ActorSystem)
    extends ExclusiveTimePeriodLock
    with InjectAuthHeader
    with Logging {

  implicit val ec: ExecutionContext = system.dispatchers.lookup("migration-dispatcher")
  implicit val hc: HeaderCarrier    = addAuth(appConfig, HeaderCarrier())
  implicit val messages: Messages   = messagesApi.preferred(Seq(Lang.defaultLang))

  val repo        = lockRepo.repo()
  val lockId      = "migration-lock"
  val holdLockFor = Duration.standardMinutes(2)

  val decider: Supervision.Decider = {
    case NonFatal(e) =>
      logger.error("Skipping case migration due to error", e)
      Supervision.resume
    case _ =>
      Supervision.stop
  }

  private val migrationSource = Source
    .unfoldAsync(SearchPagination()) { pagination =>
      tryToAcquireOrRenewLock {
        casesService.allCases(pagination, Sort())
      }.map { maybeCases =>
        logger.info("Acquired migration lock")
        maybeCases
          .filter(_.nonEmpty)
          .map(cases => (pagination.copy(page = pagination.page + 1), cases.results.toList))
      }
    }
    .flatMapConcat(cases => Source(cases.filter(_.application.applicationPdf.isEmpty)))
    .mapAsync(Runtime.getRuntime().availableProcessors())(regeneratePdf)
    .withAttributes(ActorAttributes.supervisionStrategy(decider))

  val runMigration =
    if (appConfig.migrationWorkerEnabled)
      migrationSource.runWith(Sink.ignore)
    else
      Future.successful(())

  private def noFileMetadata(cse: Case, att: Attachment) =
    new NoSuchElementException(s"Unable to find file metadata for attachment ${att.id} of case ${cse.reference}")

  private def getCountryName(code: String): Option[String] =
    countriesService.getAllCountriesById.get(code).map(_.countryName)

  def regeneratePdf(cse: Case): Future[Unit] =
    for {
      meta <- fileService.getAttachmentMetadata(cse)

      metaById = meta.map(m => (m.id, m)).toMap

      fileViews = cse.attachments.map { att =>
        val fileMeta = metaById.get(att.id).getOrElse(throw noFileMetadata(cse, att))
        FileView(att.id, fileMeta.fileName, !att.public)
      }

      pdfModel = PdfViewModel(cse, fileViews)

      _ = logger.info(s"Regenerating application PDF for case ${cse.reference}")

      pdfFile <- pdfService.generatePdf(view_application_pdf(appConfig, pdfModel, getCountryName))

      pdfStored <- fileService.uploadApplicationPdf(cse.reference, pdfFile.content)

      creationTime = ZonedDateTime.ofInstant(clock.instant(), clock.getZone())

      pdfAttachment = Attachment(pdfStored.id, false, creationTime)

      caseUpdate = CaseUpdate(
        Some(
          ApplicationUpdate(
            applicationPdf = SetValue(Some(pdfAttachment))
          )
        )
      )

      _ <- casesService.update(cse.reference, caseUpdate)

      _ = logger.info(s"Successfully regenerated application PDF for case ${cse.reference}")

    } yield ()
}
