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

package controllers.actions

import com.google.inject.Inject
import play.api.mvc.ActionTransformer
import connectors.DataCacheConnector
import models.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionImpl @Inject()(val dataCacheConnector: DataCacheConnector) extends DataRetrievalAction {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    dataCacheConnector.fetch(request.identifier).map {
      case None => OptionalDataRequest(request.request, request.identifier, None)
      case Some(data) => OptionalDataRequest(request.request, request.identifier, Some(UserAnswers(data)))
    }
  }

}

trait DataRetrievalAction extends ActionTransformer[IdentifierRequest, OptionalDataRequest]
