/*
 * Copyright 2023 HM Revenue & Customs
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
import controllers.behaviours._
import forms.CommodityCodeRulingReferenceFormProvider
import models.{CheckMode, NormalMode, UserAnswers}
import navigation.FakeNavigator
import org.scalatest.BeforeAndAfterEach
import pages.{CommodityCodeRulingReferencePage, ProvideGoodsNamePage, QuestionPage}
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.{Call, Request}
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.commodityCodeRulingReference

import scala.concurrent.ExecutionContext.Implicits.global

class CommodityCodeRulingReferenceControllerSpec
    extends ControllerSpecBase
    with AccumulatingEditingControllerBehaviours
    with BeforeAndAfterEach {

  private def onwardRoute = Call("GET", "/foo")

  private val formProvider = new CommodityCodeRulingReferenceFormProvider()
  private val goodsName    = "some-goods-name"

  val backgroundData: Map[String, JsValue] = Map(
    ProvideGoodsNamePage.toString -> JsString(goodsName),
    CommodityCodeRulingReferencePage.toString -> JsArray(
      Seq(
        Json.toJson("01234567890"),
        Json.toJson("09876543210")
      )
    )
  )

  val fakeCacheConnector =
    new FakeDataCacheConnector(Map(cacheMapId -> CacheMap(cacheMapId, backgroundData)))

  override protected def beforeEach(): Unit = {
    await(fakeCacheConnector.remove(CacheMap(cacheMapId, Map.empty)))
    await(fakeCacheConnector.save(CacheMap(cacheMapId, backgroundData)))
  }

  val commodityCodeRulingReferenceView: commodityCodeRulingReference =
    app.injector.instanceOf(classOf[commodityCodeRulingReference])

  private def controller(dataRetrievalAction: DataRetrievalAction) =
    new CommodityCodeRulingReferenceController(
      frontendAppConfig,
      fakeCacheConnector,
      new FakeNavigator(onwardRoute),
      FakeIdentifierAction,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      formProvider,
      cc,
      commodityCodeRulingReferenceView
    )

  def viewAsString(form: Form[String], submitAction: Call, request: Request[_]): String =
    commodityCodeRulingReferenceView(frontendAppConfig, form, submitAction, NormalMode)(request, messages).toString

  private val invalidFormData = Map("value" -> "")

  private val validFormData = List(
    Map("value" -> "11111111111"),
    Map("value" -> "22222222222"),
    Map("value" -> "33333333333"),
    Map("value" -> "44444444444")
  )

  def userAnswersFor[A: Format](
    backgroundData: Map[String, JsValue],
    questionPage: QuestionPage[A],
    answer: A
  ): UserAnswers =
    UserAnswers(CacheMap(cacheMapId, backgroundData ++ Map(questionPage.toString -> Json.toJson(answer))))

  val expectedUserAnswers: List[UserAnswers] = List(
    userAnswersFor(backgroundData, CommodityCodeRulingReferencePage, List("01234567890", "09876543210", "11111111111")),
    userAnswersFor(
      backgroundData,
      CommodityCodeRulingReferencePage,
      List("01234567890", "09876543210", "11111111111", "22222222222")
    ),
    userAnswersFor(
      backgroundData,
      CommodityCodeRulingReferencePage,
      List("01234567890", "09876543210", "11111111111", "22222222222", "33333333333")
    ),
    userAnswersFor(
      backgroundData,
      CommodityCodeRulingReferencePage,
      List("01234567890", "09876543210", "11111111111", "22222222222", "33333333333", "44444444444")
    )
  )

  val validEditFormData: List[(Int, Map[String, String])] = List(
    1 -> Map("value" -> "09876543211"),
    0 -> Map("value" -> "11234567890")
  )

  val expectedEditedAnswers: List[UserAnswers] = List(
    userAnswersFor(backgroundData, CommodityCodeRulingReferencePage, List("01234567890", "09876543211")),
    userAnswersFor(backgroundData, CommodityCodeRulingReferencePage, List("11234567890", "09876543211"))
  )

  "CommodityCodeRulingReferenceController" must {
    behave like listEditingController(
      controller,
      onwardRoute,
      viewAsString,
      backgroundData,
      invalidFormData,
      validFormData,
      expectedUserAnswers,
      validEditFormData,
      expectedEditedAnswers
    )
  }

  "editSubmitAction" must {
    "should return the correct Call object in NormalMode" in {
      val index        = 1
      val mode         = NormalMode
      val expectedCall = Call("POST", "/advance-tariff-application/edit-similar-ruling-reference?index=1")

      val result = controller(getEmptyCacheMap).editSubmitAction(index, mode)

      assert(result == expectedCall)
    }

    "should return the correct Call object in CheckMode" in {
      val index        = 1
      val mode         = CheckMode
      val expectedCall = Call("POST", "/advance-tariff-application/change-edit-similar-ruling-reference?index=1")

      val result = controller(getEmptyCacheMap).editSubmitAction(index, mode)

      assert(result == expectedCall)
    }
  }
}
