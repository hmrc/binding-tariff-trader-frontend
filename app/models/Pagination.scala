/*
 * Copyright 2020 HM Revenue & Customs
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

trait Pagination {
  val page: Int
  val pageSize: Int
}

object Pagination {
  val unlimited: Int = Integer.MAX_VALUE
}

case class SearchPagination
(
  override val page: Int = 1,
  override val pageSize: Int = 50
) extends Pagination

case class NoPagination
(
  override val page: Int = 1,
  override val pageSize: Int = Pagination.unlimited
) extends Pagination