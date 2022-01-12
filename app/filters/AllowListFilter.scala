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

package filters

import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.http.HttpVerbs.GET
import play.api.mvc.Call
import uk.gov.hmrc.allowlist.AkamaiAllowlistFilter

@Singleton
class AllowListFilter @Inject()(
                                  config: Configuration,
                                  override val mat: Materializer
                                ) extends AkamaiAllowlistFilter {

  override val allowlist: Seq[String] = {
    config
      .underlying
      .getString("filters.allowlist.ips")
      .split(",")
      .map(_.trim)
      .filter(_.nonEmpty)
  }

  override val destination: Call = {
    val path = config.underlying.getString("filters.allowlist.destination")
    Call(GET, path)
  }

  override val excludedPaths: Seq[Call] = {
    config.underlying.getString("filters.allowlist.excluded").split(",").map {
      path => Call(GET, path.trim)
    }
  }

}
