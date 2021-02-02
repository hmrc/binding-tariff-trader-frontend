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

package models

sealed abstract class UpdateType(val name: String)
object UpdateType {
  case object SetValue extends UpdateType("SET_VALUE")
  case object NoChange extends UpdateType("NO_CHANGE")
}

sealed abstract class Update[+A] {
  def map[B](f: A => B): Update[B] = this match {
    case SetValue(a) => SetValue(f(a))
    case NoChange => NoChange
  }
  def getOrElse[AA >: A](default: => AA): AA = this match {
    case SetValue(a) => a
    case NoChange => default
  }
}
case class SetValue[A](a: A) extends Update[A]
case object NoChange extends Update[Nothing]

case class ApplicationUpdate(
  `type`: String = "BTI",
  // holder: Update[EORIDetails]                     = NoChange,
  // contact: Update[Contact]                        = NoChange,
  // agent: Update[Option[AgentDetails]]             = NoChange,
  // offline: Update[Boolean]                        = NoChange,
  // goodName: Update[String]                        = NoChange,
  // goodDescription: Update[String]                 = NoChange,
  // confidentialInformation: Update[Option[String]] = NoChange,
  // otherInformation: Update[Option[String]]        = NoChange,
  // reissuedBTIReference: Update[Option[String]]    = NoChange,
  // relatedBTIReferences: Update[List[String]]      = NoChange,
  // knownLegalProceedings: Update[Option[String]]   = NoChange,
  // envisagedCommodityCode: Update[Option[String]]  = NoChange,
  // sampleToBeProvided: Update[Boolean]             = NoChange,
  // sampleIsHazardous: Update[Option[Boolean]]      = NoChange,
  // sampleToBeReturned: Update[Boolean]             = NoChange,
  applicationPdf: Update[Option[Attachment]] = NoChange
)

case class CaseUpdate(
  // status: Update[CaseStatus.Value]             = NoChange,
  // createdDate: Update[Instant]                 = NoChange,
  // caseBoardsFileNumber: Update[Option[String]] = NoChange,
  // assignee: Update[Option[Operator]]           = NoChange,
  // queueId: Update[Option[String]]              = NoChange,
  application: Option[ApplicationUpdate] = None
  // decision: Update[Option[DecisionUpdate]]     = NoChange,
  // attachments: Update[Seq[Attachment]]         = NoChange,
  // keywords: Update[Set[String]]                = NoChange,
  // sample: Update[Sample]                       = NoChange,
  // dateOfExtract: Update[Option[Instant]]       = NoChange,
  // migratedDaysElapsed: Update[Option[Long]]    = NoChange
)
