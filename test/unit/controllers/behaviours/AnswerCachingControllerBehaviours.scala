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
package behaviours

import controllers.actions.{ DataRetrievalAction, FakeDataRetrievalAction }
import controllers.routes
import models.NormalMode
import play.api.mvc.Call
import play.api.data.Form
import play.api.libs.json.JsValue
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import play.api.libs.json.Json
import play.api.libs.json.Format
import play.api.mvc.Request

trait YesNoCachingControllerBehaviours extends AnswerCachingControllerBehaviours { self: ControllerSpecBase =>
  def yesNoCachingController(
    controller: DataRetrievalAction => AnswerCachingController[Boolean],
    onwardRoute: Call,
    createView: (Form[Boolean], Request[_]) => String,
    formField: String = "value",
    backgroundData: Map[String, JsValue] = Map.empty
  ) = {
    behave like answerCachingController(
      controller,
      onwardRoute,
      createView,
      Map(formField -> "true"),
      Map(formField -> "invalid value"),
      backgroundData,
      true
    )

    "redirect to the next page when Yes is submitted" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))
      val postRequest = fakePOSTRequestWithCSRF.withFormUrlEncodedBody((formField, "true"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "redirect to the next page when No is submitted" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))
      val postRequest = fakePOSTRequestWithCSRF.withFormUrlEncodedBody((formField, "false"))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }
  }
}

trait AnswerCachingControllerBehaviours { self: ControllerSpecBase =>
  def answerCachingController[A: Format](
    controller: DataRetrievalAction => AnswerCachingController[A],
    onwardRoute: Call,
    createView: (Form[A], Request[_]) => String,
    validFormData: Map[String, String],
    invalidFormData: Map[String, String],
    backgroundData: Map[String, JsValue],
    validAnswers: A*
  ): Unit = {
    val noDataController = controller(dontGetAnyData)

    "return OK and the correct view for a GET" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))

      val controllerWithData = controller(getRelevantData)
      val request = fakeGETRequestWithCSRF
      val result = controllerWithData.onPageLoad(NormalMode)(request)
      val form = controllerWithData.form

      status(result) shouldBe OK
      contentAsString(result) shouldBe createView(form, request)
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      validAnswers.foreach { validAnswer =>
        val validData = backgroundData + (noDataController.questionPage.toString -> Json.toJson(validAnswer))
        val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

        val controllerWithData = controller(getRelevantData)
        val request = fakeGETRequestWithCSRF
        val result = controllerWithData.onPageLoad(NormalMode)(request)
        val form = controllerWithData.form

        contentAsString(result) shouldBe createView(form.fill(validAnswer), request)
      }
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakePOSTRequestWithCSRF.withFormUrlEncodedBody(validFormData.toSeq: _*)

      val result = controller(getEmptyCacheMap).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val validData = backgroundData + (noDataController.questionPage.toString -> Json.toJson(validAnswers.head))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val controllerWithData = controller(getRelevantData)
      val postRequest = fakePOSTRequestWithCSRF.withFormUrlEncodedBody(invalidFormData.toSeq: _*)
      val boundForm = controllerWithData.form.bind(invalidFormData)

      val result = controllerWithData.onSubmit(NormalMode)(postRequest)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) shouldBe createView(boundForm, postRequest)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}