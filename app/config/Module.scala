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

package config

import controllers.actions.*
import org.apache.fop.apps.FopFactory
import play.api.inject.{Binding, Module => PlayModule}
import play.api.{Configuration, Environment}
import service.{DataCacheService, MongoCacheService}
import workers.MigrationWorker

import java.time.Clock

class Module extends PlayModule {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[?]] =
    Seq(
      bind[DataRetrievalAction].to[DataRetrievalActionImpl].eagerly(),
      bind[DataRequiredAction].to[DataRequiredActionImpl].eagerly(),
      bind[IdentifierAction].to[AuthenticatedIdentifierAction].eagerly(),
      bind[FopFactory].toProvider[FopFactoryProvider].eagerly(),
      bind[DataCacheService].to[MongoCacheService].eagerly(),
      bind[Clock].to(Clock.systemUTC()),
      bind[MigrationWorker].toSelf.eagerly()
    )
}
