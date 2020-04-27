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

package filters

import java.util.UUID

import akka.stream.Materializer
import com.google.inject.Inject
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.{DefaultHttpFilters, HttpFilters}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc._
import play.api.routing.Router
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderNames, HttpVerbs, SessionKeys}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext

object SessionIdFilterSpec {

  lazy val sessionId = "28836767-a008-46be-ac18-695ab140e705"

  class Filters @Inject()(sessionId: SessionIdFilter) extends DefaultHttpFilters(sessionId)

  class TestSessionIdFilter @Inject()(
                                       override val mat: Materializer,
                                       ec: ExecutionContext
                                     ) extends SessionIdFilter(mat, UUID.fromString(sessionId), ec)

}

class SessionIdFilterSpec extends UnitSpec with GuiceOneAppPerSuite {

  override lazy val fakeApplication: Application = GuiceApplicationBuilder()
    .overrides(
      play.api.inject.bind[HttpFilters].to[SessionIdFilterSpec.Filters],
      play.api.inject.bind[SessionIdFilter].to[SessionIdFilterSpec.TestSessionIdFilter]
    )
    .router(router)
    .build()
  private lazy val sessionId = SessionIdFilterSpec.sessionId
  private lazy val realApp = GuiceApplicationBuilder()
    .configure(
      "metrics.jvm" -> false,
      "metrics.enabled" -> false
    ).build()
  private lazy val cc = realApp.injector.instanceOf[MessagesControllerComponents]

  private val router: Router = {
    import play.api.routing.sird._

    Router.from {
      case GET(p"/test") => Action {
        request =>
          val fromHeader = request.headers.get(HeaderNames.xSessionId).getOrElse("")
          val fromSession = request.session.get(SessionKeys.sessionId).getOrElse("")
          Results.Ok(
            Json.obj(
              "fromHeader" -> fromHeader,
              "fromSession" -> fromSession
            )
          )
      }
      case GET(p"/test2") => Action {
        implicit request =>
          Results.Ok.addingToSession("foo" -> "bar")
      }
    }
  }

  private def Action: ActionBuilder[MessagesRequest, AnyContent] = cc.messagesActionBuilder.compose(cc.actionBuilder)

  ".apply" must {

    "add a sessionId if one doesn't already exist" in {

      val Some(result) = route(fakeApplication, FakeRequest(HttpVerbs.GET, "/test"))

      val body = contentAsJson(result)

      (body \ "fromHeader").as[String] shouldBe s"session-$sessionId"
      (body \ "fromSession").as[String] shouldBe s"session-$sessionId"
    }

    "not override a sessionId if one doesn't already exist" in {

      val Some(result) = route(fakeApplication, FakeRequest(HttpVerbs.GET, "/test").withSession(SessionKeys.sessionId -> "foo"))

      val body = contentAsJson(result)

      (body \ "fromHeader").as[String] shouldBe ""
      (body \ "fromSession").as[String] shouldBe "foo"
    }

    "not override other session values from the response" in {

      val Some(result) = route(fakeApplication, FakeRequest(HttpVerbs.GET, "/test2"))
      session(result).data should contain("foo" -> "bar")
    }

    "not override other session values from the request" in {

      val Some(result) = route(fakeApplication, FakeRequest(HttpVerbs.GET, "/test").withSession("foo" -> "bar"))
      session(result).data should contain("foo" -> "bar")
    }
  }

}
