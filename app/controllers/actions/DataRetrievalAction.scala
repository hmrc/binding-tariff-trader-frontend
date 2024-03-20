/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.actions

import com.google.inject.Inject
import connectors.DataCacheConnector
import models.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest}
import play.api.mvc.ActionTransformer
import models.cache.CacheMap

import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionImpl @Inject() (val dataCacheConnector: DataCacheConnector)(implicit ec: ExecutionContext)
    extends DataRetrievalAction {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] =
    dataCacheConnector.fetch(request.identifier).map { maybeData: Option[CacheMap] =>
      OptionalDataRequest(request.request, request.identifier, request.eoriNumber, maybeData.map(UserAnswers(_)))
    }

  override protected def executionContext: ExecutionContext = ec
}

trait DataRetrievalAction extends ActionTransformer[IdentifierRequest, OptionalDataRequest]
