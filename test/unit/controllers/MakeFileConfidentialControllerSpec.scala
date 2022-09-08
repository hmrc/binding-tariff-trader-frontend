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

package controllers

import connectors.FakeDataCacheConnector
import controllers.actions._
import controllers.behaviours.AccumulatingCachingControllerBehaviours
import forms.MakeFileConfidentialFormProvider
import models.{FileAttachment, NormalMode, UserAnswers}
import navigation.FakeNavigator
import pages.{MakeFileConfidentialPage, ProvideGoodsNamePage, QuestionPage, UploadSupportingMaterialMultiplePage}
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.{Call, Request}
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.makeFileConfidential

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.BeforeAndAfterEach

class MakeFileConfidentialControllerSpec
    extends ControllerSpecBase
    with AccumulatingCachingControllerBehaviours
    with BeforeAndAfterEach {
  private def onwardRoute        = Call("GET", "/foo")
  private val formProvider       = new MakeFileConfidentialFormProvider()
  private val goodsName          = "some-goods-name"
  private val lastFileUploadedId = "file-id-3"

  val backgroundData = Map(
    ProvideGoodsNamePage.toString -> JsString(goodsName),
    UploadSupportingMaterialMultiplePage.toString -> JsArray(
      Seq(
        Json.toJson(FileAttachment("file-id-1", "foo.jpg", "image/jpeg", 1L)),
        Json.toJson(FileAttachment("file-id-2", "bar.jpg", "image/jpeg", 1L)),
        Json.toJson(FileAttachment("file-id-3", "baz.jpg", "image/jpeg", 1L))
      )
    )
  )

  val fakeCacheConnector =
    new FakeDataCacheConnector(Map(cacheMapId -> CacheMap(cacheMapId, backgroundData)))

  override protected def beforeEach(): Unit = {
    await(fakeCacheConnector.remove(CacheMap(cacheMapId, Map.empty)))
    await(fakeCacheConnector.save(CacheMap(cacheMapId, backgroundData)))
  }

  val makeFileConfidentialView: makeFileConfidential = app.injector.instanceOf(classOf[makeFileConfidential])

  private def controller(dataRetrievalAction: DataRetrievalAction) =
    new MakeFileConfidentialController(
      frontendAppConfig,
      fakeCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc,
      makeFileConfidentialView
    )

  private def viewAsString(form: Form[_], submitAction: Call, request: Request[_]): String =
    makeFileConfidentialView(frontendAppConfig, form, submitAction, NormalMode, lastFileUploadedId)(request, messages).toString

  val invalidFormData = Map("file-id-1" -> "", "confidential" -> "")

  val validFormData = List(
    Map("fileId" -> "file-id-1", "confidential" -> "true"),
    Map("fileId" -> "file-id-2", "confidential" -> "false"),
    Map("fileId" -> "file-id-3", "confidential" -> "true")
  )

  def userAnswersFor[A: Format](backgroundData: Map[String, JsValue], questionPage: QuestionPage[A], answer: A) =
    UserAnswers(CacheMap(cacheMapId, backgroundData ++ Map(questionPage.toString -> Json.toJson(answer))))

  val expectedUserAnswers = List(
    userAnswersFor(backgroundData, MakeFileConfidentialPage, Map("file-id-1" -> true)),
    userAnswersFor(backgroundData, MakeFileConfidentialPage, Map("file-id-1" -> true, "file-id-2" -> false)),
    userAnswersFor(
      backgroundData,
      MakeFileConfidentialPage,
      Map("file-id-1" -> true, "file-id-2" -> false, "file-id-3" -> true)
    )
  )

  "MakeFileConfidentialController" must {
    behave like mapCachingController(
      controller,
      onwardRoute,
      viewAsString,
      backgroundData,
      invalidFormData,
      validFormData,
      expectedUserAnswers
    )
  }
}
