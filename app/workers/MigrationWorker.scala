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

package workers

import config.FrontendAppConfig
import models._
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.{Sink, Source}
import org.apache.pekko.stream.{ActorAttributes, Supervision}
import play.api.Logging
import play.api.i18n.{Lang, Messages, MessagesApi}
import service.{CasesService, CountriesService, FileService, PdfService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mongo.lock.{MongoLockRepository, TimePeriodLockService}
import viewmodels.{FileView, PdfViewModel}
import views.html.components.view_application

import java.time.{Clock, ZonedDateTime}
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class MigrationWorker @Inject() (
  appConfig: FrontendAppConfig,
  casesService: CasesService,
  countriesService: CountriesService,
  fileService: FileService,
  pdfService: PdfService,
  messagesApi: MessagesApi,
  clock: Clock,
  mongoLockRepository: MongoLockRepository,
  view_application: view_application
)(implicit system: ActorSystem)
    extends Logging {

  implicit val ec: ExecutionContext = system.dispatchers.lookup("migration-dispatcher")
  implicit val hc: HeaderCarrier    = HeaderCarrier()
  implicit val messages: Messages   = messagesApi.preferred(Seq(Lang.defaultLang))

  private val myLock: TimePeriodLockService =
    TimePeriodLockService(mongoLockRepository, lockId = "migration-lock", ttl = 2.minutes)

  private val decider: Supervision.Decider = {
    case NonFatal(e) =>
      logger.error("[MigrationWorker][decider] Skipping case migration due to error", e)
      Supervision.resume
    case _ =>
      Supervision.stop
  }

  private val migrationSource = Source
    .unfoldAsync(SearchPagination()) { pagination =>
      myLock
        .withRenewedLock {
          casesService.allCases(pagination, Sort())
        }
        .map { maybeCases =>
          logger.info("[MigrationWorker][migrationSource] Acquired migration lock")
          maybeCases
            .filter(_.nonEmpty)
            .map(cases => (pagination.copy(page = pagination.page + 1), cases.results.toList))
        }
    }
    .flatMapConcat(cases => Source(cases.filter(_.application.applicationPdf.isEmpty)))
    .mapAsync(Runtime.getRuntime.availableProcessors())(regeneratePdf)
    .withAttributes(ActorAttributes.supervisionStrategy(decider))

  val runMigration: Future[Any] =
    if (appConfig.migrationWorkerEnabled) {
      migrationSource.runWith(Sink.ignore)
    } else {
      Future.successful(())
    }

  private def noFileMetadata(cse: Case, att: Attachment) =
    new NoSuchElementException(s"Unable to find file metadata for attachment ${att.id} of case ${cse.reference}")

  private def getCountryName(code: String): Option[String] =
    countriesService.getAllCountriesById.get(code).map(_.countryName)

  private def regeneratePdf(cse: Case): Future[Unit] =
    for {
      meta <- fileService.getAttachmentMetadataForCase(cse)

      metaById = meta.map(m => (m.id, m)).toMap

      fileViews = cse.attachments.map { att =>
                    val fileMeta = metaById.getOrElse(att.id, throw noFileMetadata(cse, att))
                    FileView(att.id, fileMeta.fileName, !att.public)
                  }

      pdfModel = PdfViewModel(cse, fileViews)

      _ = logger.info(s"[MigrationWorker][regeneratePdf] Regenerating application PDF for case ${cse.reference}")

      pdfFile <- pdfService.generatePdf(view_application(appConfig, pdfModel, getCountryName))

      pdfStored <- fileService.uploadApplicationPdf(cse.reference, pdfFile)

      creationTime = ZonedDateTime.ofInstant(clock.instant(), clock.getZone)

      pdfAttachment = Attachment(pdfStored.id, public = false, timestamp = creationTime)

      caseUpdate = CaseUpdate(
                     Some(
                       ApplicationUpdate(
                         applicationPdf = SetValue(Some(pdfAttachment))
                       )
                     )
                   )

      _ <- casesService.update(cse.reference, caseUpdate)

      _ = logger.info(
            s"[MigrationWorker][regeneratePdf] Successfully regenerated application PDF for case ${cse.reference}"
          )

    } yield ()
}
