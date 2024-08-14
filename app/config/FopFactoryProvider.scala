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

package config

import org.apache.fop.apps.{FopFactory, FopFactoryBuilder}
import org.apache.fop.configuration.{Configuration, DefaultConfigurationBuilder}

import java.io.File
import javax.inject.{Inject, Provider, Singleton}

@Singleton
class FopFactoryProvider @Inject() extends Provider[FopFactory] {
  private val baseURI            = getClass.getClassLoader.getResource("./").toURI
  private val cfgBuilder         = new DefaultConfigurationBuilder()
  private val cfg: Configuration = cfgBuilder.buildFromFile(new File("./conf/fop.xconf"))

  override def get(): FopFactory = new FopFactoryBuilder(baseURI).setConfiguration(cfg).build()
}
