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

package metrics

import play.api.mvc.{Action, MessagesBaseController}

import scala.concurrent.ExecutionContext

trait HasActionMetrics extends HasMetrics { self: MessagesBaseController =>

  /** Execute a [[play.api.mvc.Action]] with a metrics timer. Intended for use in controllers that return HTTP
    * responses.
    *
    * @param metric
    *   The id of the metric to be collected
    * @param action
    *   The action to wrap with metrics collection
    * @param ec
    *   The [[scala.concurrent.ExecutionContext]] on which the block of code should run
    * @return
    *   an action which captures metrics about the wrapped action
    */
  def withMetricsTimerAction[A](metric: String)(action: Action[A])(implicit ec: ExecutionContext): Action[A] =
    Action(action.parser).async(request => withMetricsTimerResult(metric)(action(request)))
}
