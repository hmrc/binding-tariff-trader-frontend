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

package views

import models.requests.IdentifierRequest
import views.behaviours.ViewBehaviours
import views.html.informationPublic

class  InformationPublicViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "informationPublic"

  val informationPublicView: informationPublic = app.injector.instanceOf[informationPublic]

  private def createView(eori: Option[String] = Some("eori")) = () => informationPublicView(frontendAppConfig)(IdentifierRequest(fakeRequest, "id", eori), messages)

  "InformationPublic view" must {
    behave like normalPage(createView(), messageKeyPrefix)()

    behave like pageWithBackLink(createView())
  }

}

