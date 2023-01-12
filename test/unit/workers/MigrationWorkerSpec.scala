/*
 * Copyright 2023 HM Revenue & Customs
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

package unit.workers

import com.kenshoo.play.metrics.Metrics
import models._
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito._
import org.mockito.Mockito.verify
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers
import play.twirl.api.Html
import service.{CasesService, FileService, PdfService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mongo.lock.MongoLockRepository
import uk.gov.hmrc.mongo.test.MongoSupport
import unit.utils.{TestMetrics, UnitSpec}
import workers.MigrationWorker

import java.time.{Clock, Instant, ZoneOffset, ZonedDateTime}
import scala.collection.immutable.ListMap
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, DurationInt}

class MigrationWorkerSpec extends UnitSpec with MockitoSugar with BeforeAndAfterAll with MongoSupport { self =>

  val lockId                          = "lockId"
  val ttl: Duration                   = 1000.millis
  val repository: MongoLockRepository = mock[MongoLockRepository]

  //val lockRepoProvider = new LockRepoProvider(repository)

  val now: Instant              = Instant.now()
  val clock: Clock              = Clock.fixed(now, ZoneOffset.UTC)
  val caseService: CasesService = mock[CasesService]
  val fileService: FileService  = mock[FileService]
  val pdfService: PdfService    = mock[PdfService]

  val configuredApp: GuiceApplicationBuilder => GuiceApplicationBuilder =
    _.overrides(
      bind[Metrics].to(new TestMetrics),
      bind[CasesService].to(caseService),
      bind[FileService].to(fileService),
      bind[PdfService].to(pdfService),
      bind[Clock].to(clock),
      bind[MongoLockRepository].to(repository)
    )

  val pagination: SearchPagination = SearchPagination()

  def caseWithoutPdf(reference: String, cse: Case): Case =
    cse.copy(
      reference = reference,
      application = cse.application.copy(
        applicationPdf = None
      )
    )

  def attachmentUpdate(id: String): CaseUpdate = CaseUpdate(
    application = Some(
      ApplicationUpdate(
        applicationPdf = SetValue(
          Some(
            Attachment(
              id        = id,
              public    = false,
              timestamp = ZonedDateTime.ofInstant(now, ZoneOffset.UTC)
            )
          )
        )
      )
    )
  )

  val exampleCases: ListMap[String, Case] = ListMap(
    "ref1" -> caseWithoutPdf("ref1", oCase.btiCaseExample),
    "ref2" -> caseWithoutPdf("ref2", oCase.btiCaseWithDecision),
    "ref3" -> caseWithoutPdf("ref3", oCase.btiCaseWithDecisionNoExplanation)
  )

  val pagedCases: Paged[Case] = Paged(
    results     = exampleCases.values.toList,
    pagination  = pagination,
    resultCount = 3
  )

  override protected def beforeAll(): Unit = {
    given(repository.refreshExpiry(any[String], any[String], any[Duration]))
      .willReturn(Future.successful(true))
    given(repository.takeLock(any[String], any[String], any[Duration]))
      .willReturn(Future.successful(true))
    given(repository.releaseLock(any[String], any[String]))
      .willReturn(Future.successful(()))
    given(caseService.allCases(any[Pagination], any[Sort])(any[HeaderCarrier]))
      .willReturn(Future.successful(pagedCases))
      .willReturn(Future.successful(Paged.empty[Case](pagination)))
    given(caseService.update(refEq("ref1"), any[CaseUpdate])(any[HeaderCarrier]))
      .willReturn(Future.successful(Some(exampleCases("ref1"))))
    given(caseService.update(refEq("ref2"), any[CaseUpdate])(any[HeaderCarrier]))
      .willReturn(Future.successful(Some(exampleCases("ref2"))))
    given(caseService.update(refEq("ref3"), any[CaseUpdate])(any[HeaderCarrier]))
      .willReturn(Future.successful(Some(exampleCases("ref3"))))
    given(fileService.getAttachmentMetadata(any[Case])(any[HeaderCarrier]))
      .willReturn(Future.successful(Seq.empty))
    given(pdfService.generatePdf(any[Html]))
      .willReturn(Future.successful(PdfFile(Array.empty)))
    given(fileService.uploadApplicationPdf(refEq("ref1"), any[Array[Byte]])(any[HeaderCarrier]))
      .willReturn(Future.successful(FileAttachment("id1", "some.pdf", "application/pdf", 0L, uploaded = true)))
    given(fileService.uploadApplicationPdf(refEq("ref2"), any[Array[Byte]])(any[HeaderCarrier]))
      .willReturn(Future.successful(FileAttachment("id2", "some.pdf", "application/pdf", 0L, uploaded = true)))
    given(fileService.uploadApplicationPdf(refEq("ref3"), any[Array[Byte]])(any[HeaderCarrier]))
      .willReturn(Future.successful(FileAttachment("id3", "some.pdf", "application/pdf", 0L, uploaded = true)))
  }

  "MigrationWorker" should {
    "regenerate application PDF for cases where it is missing" in {
      Helpers.running(configuredApp) { app =>
        await(app.injector.instanceOf[MigrationWorker].runMigration)
        verify(caseService).update(refEq("ref1"), refEq(attachmentUpdate("id1")))(any[HeaderCarrier])
        verify(caseService).update(refEq("ref2"), refEq(attachmentUpdate("id2")))(any[HeaderCarrier])
        verify(caseService).update(refEq("ref3"), refEq(attachmentUpdate("id3")))(any[HeaderCarrier])
      }
    }
  }
}
