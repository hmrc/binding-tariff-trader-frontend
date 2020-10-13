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

package controllers

import connectors.FakeDataCacheConnector
import controllers.actions._
import controllers.behaviours.AccumulatingCachingControllerBehaviours
import forms.CommodityCodeRulingReferenceFormProvider
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigator
import org.scalatest.BeforeAndAfterEach
import pages.{CommodityCodeRulingReferencePage, ProvideGoodsNamePage, QuestionPage}
import play.api.data.Form
import play.api.mvc.{Call, Request}
import views.html.commodityCodeRulingReference

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.{Format, JsArray, JsString, JsValue, Json}
import uk.gov.hmrc.http.cache.client.CacheMap

class CommodityCodeRulingReferenceControllerSpec extends ControllerSpecBase with AccumulatingCachingControllerBehaviours with BeforeAndAfterEach {

  private def onwardRoute = Call("GET", "/foo")

  private val formProvider = new CommodityCodeRulingReferenceFormProvider()
  private val goodsName = "some-goods-name"
  private val testAnswer = "answer"
  private val validFormData = List(Map("value" -> testAnswer))
  private val invalidFormData = Map("value" -> "")

  val backgroundData = Map(
    ProvideGoodsNamePage.toString -> JsString(goodsName),
    CommodityCodeRulingReferencePage.toString -> JsArray(Seq(
      Json.toJson("01234567890"),
      Json.toJson("09876543210")
    ))
  )

  val fakeCacheConnector =
    new FakeDataCacheConnector(Map(cacheMapId -> CacheMap(cacheMapId, backgroundData)))

  override protected def beforeEach(): Unit = {
    await(fakeCacheConnector.remove(CacheMap(cacheMapId, Map.empty)))
    await(fakeCacheConnector.save(CacheMap(cacheMapId, backgroundData)))
  }

  private def controller(dataRetrievalAction: DataRetrievalAction) =
    new CommodityCodeRulingReferenceController(
      frontendAppConfig,
      fakeCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc
    )

  def viewAsString(form: Form[_], request: Request[_]): String =
    commodityCodeRulingReference(frontendAppConfig, form, onwardRoute, NormalMode)(request, messages).toString

  def userAnswersFor[A: Format](backgroundData: Map[String, JsValue], questionPage: QuestionPage[A], answer: A) = {
    UserAnswers(CacheMap(cacheMapId, backgroundData ++ Map(questionPage.toString -> Json.toJson(answer))))
  }

  val expectedUserAnswers = List(
    userAnswersFor(backgroundData, CommodityCodeRulingReferencePage, List("01234567890")),
    userAnswersFor(backgroundData, CommodityCodeRulingReferencePage, List("01234567890", "09876543210")),
    userAnswersFor(backgroundData, CommodityCodeRulingReferencePage, List("01234567890", "09876543210"))
  )

  "CommodityCodeRulingReferenceController" must {
    behave like listEditingController(
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
