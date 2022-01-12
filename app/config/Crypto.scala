/*
 * Copyright 2022 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.crypto.{AesCrypto, CompositeSymmetricCrypto, Decrypter}

@Singleton
class Crypto @Inject()(appConfig: FrontendAppConfig) extends CompositeSymmetricCrypto {

  override protected lazy val currentCrypto: AesCrypto = new AesCrypto {
    override protected lazy val encryptionKey: String = appConfig.aesKey
  }

  override protected val previousCryptos = Seq.empty[Decrypter]

}
