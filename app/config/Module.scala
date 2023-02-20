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

package config

import connectors._
import controllers.actions._
import java.time.Clock
import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module => PlayModule}
import uk.gov.hmrc.crypto.CompositeSymmetricCrypto
import workers.MigrationWorker

class Module extends PlayModule {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq(
      bind[DataRetrievalAction].to[DataRetrievalActionImpl].eagerly(),
      bind[DataRequiredAction].to[DataRequiredActionImpl].eagerly(),
      bind[IdentifierAction].to[AuthenticatedIdentifierAction].eagerly(),
      bind[DataCacheConnector].to[MongoCacheConnector].eagerly(),
      bind[CompositeSymmetricCrypto].to[Crypto],
      bind[Clock].to(Clock.systemUTC()),
      bind[MigrationWorker].toSelf.eagerly()
    )
}
