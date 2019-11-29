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

package filters

import base.SpecBase
import org.scalatest.concurrent.ScalaFutures
import play.api.mvc.{RequestHeader, Result, Results}
import uk.gov.hmrc.http.{HeaderNames, SessionKeys}

import scala.concurrent.Future

class SessionIdFilterSpec extends SpecBase with ScalaFutures {
  val builder = messagesControllerComponents.actionBuilder

  ".apply" must {

    "add a sessionId to the headers of the incoming request if one doesn't already exist" in {
      val SUT = app.injector.instanceOf[SessionIdFilter]

      val handler : RequestHeader => Future[Result] = rh => {
        rh.headers.get(HeaderNames.xSessionId) mustBe a[Some[_]]
        rh.headers.get(HeaderNames.xSessionId).get must startWith("session-")
        Future.successful(Results.Ok)
      }

      SUT(handler)(fakeRequest)
    }

    "add a sessionId to the session on the response if one doesn't already exist" in {
      val SUT = app.injector.instanceOf[SessionIdFilter]

      val handler : RequestHeader => Future[Result] = _ => Future.successful(Results.Ok)

      val request = fakeRequest
      whenReady(SUT(handler)(request)){ response =>
        response.session(request).get(SessionKeys.sessionId) mustBe a[Some[_]]
        response.session(request).get(SessionKeys.sessionId).get must startWith("session-")
      }
    }

    "not override a sessionId if one already exists" in {
      val SUT = app.injector.instanceOf[SessionIdFilter]

      val handler : RequestHeader => Future[Result] = rh => {
        rh.headers.get(HeaderNames.xSessionId) mustBe(None)
        rh.session.get(SessionKeys.sessionId) mustBe(Some("foo"))
        Future.successful(Results.Ok)
      }

      val request = fakeRequest.withSession(SessionKeys.sessionId -> "foo")
      whenReady(SUT(handler)(request)){ response =>
        response.session(request).get(SessionKeys.sessionId) mustBe (Some("foo"))
      }
    }

    "not override other session values from either the request or the response" in {
      val SUT = app.injector.instanceOf[SessionIdFilter]

      val handler: RequestHeader => Future[Result] = rh => {
        rh.session.get("foo") mustBe Some("bar")
        Future.successful(Results.Ok.addingToSession("baz" -> "qux")(rh))
      }

      val request = fakeRequest.withSession("foo" -> "bar")
      whenReady(SUT(handler)(request)) {response =>
        response.session(request).get("foo") mustBe Some("bar")
        response.session(request).get("baz") mustBe Some("qux")
      }
    }
  }

}
