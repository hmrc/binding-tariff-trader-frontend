/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers

import controllers.actions.FakeIdentifierAction
import models.oCase._
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import play.api.test.Helpers._
import service.CasesService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class IndexControllerSpec extends ControllerSpecBase with MockitoSugar {


  private val casesService = mock[CasesService]


  "Index Controller" must {


    "return the correct view for a load applications" in {

      given(casesService.findApplicationsBy(any[String])(any[HeaderCarrier])).willReturn(Future.successful(Seq(btiCaseExample)))

      val result = new IndexController(frontendAppConfig, FakeIdentifierAction, casesService, messagesApi).loadApplications()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include ("applications-list-table")
    }

    "return the correct view for a load rulings" in {

      given(casesService.findRulingsBy(any[String])(any[HeaderCarrier])).willReturn(Future.successful(Seq(btiCaseWithDecision)))

      val result = new IndexController(frontendAppConfig, FakeIdentifierAction, casesService, messagesApi).loadRulings()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include ("rulings-list-table")
      contentAsString(result) must include ("")
    }


    "return 200 for a GET when no applications are found" in {
      given(casesService.findApplicationsBy(any[String])(any[HeaderCarrier])).willReturn(Future.successful(Seq.empty))

      val result = new IndexController(frontendAppConfig, FakeIdentifierAction, casesService, messagesApi).loadApplications()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include ("application-list-empty")

    }

    "return 200 for a GET when no rulings are found" in {
      given(casesService.findRulingsBy(any[String])(any[HeaderCarrier])).willReturn(Future.successful(Seq.empty))

      val result = new IndexController(frontendAppConfig, FakeIdentifierAction, casesService, messagesApi).loadRulings()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include ("ruling-list-empty")
    }

  }

}
