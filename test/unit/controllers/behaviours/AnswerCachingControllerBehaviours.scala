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
import play.api.data.Form
import play.api.libs.json.{ Format, Json, JsValue }
import play.api.mvc.{ Call, Request }
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import models.UserAnswers
import controllers.actions.DataRetrievalActionImpl

trait AccumulatingCachingControllerBehaviours extends AnswerCachingControllerBehaviours { self: ControllerSpecBase =>
  /**
    * Test the behaviour of a [[controllers.ListCachingController]]
    *
    * @param controller A function to create the controller to test
    * @param onwardRoute The route to navigate to on Navigator#nextPage calls
    * @param createView A function to create the expected view
    * @param backgroundData The background user answers data
    * @param invalidFormData A sample of invalid form data
    * @param validFormData A list of valid form data for submission
    * @param expectedUserAnswers A list of the expected [[models.UserAnswers]] after each form submission
    */
  def listCachingController[A: Format](
    controller: DataRetrievalAction => ListCachingController[A],
    onwardRoute: Call,
    createView: (Form[A], Request[_]) => String,
    backgroundData: Map[String, JsValue],
    invalidFormData: Map[String, String],
    validFormData: List[Map[String, String]],
    expectedUserAnswers: List[UserAnswers]
  ): Unit = {
    behave like accumulatingCachingController[List[A], A](
      controller,
      onwardRoute,
      createView,
      backgroundData,
      invalidFormData,
      validFormData,
      expectedUserAnswers
    )
  }

  /**
    * Test the behaviour of a [[controllers.MapCachingController]]
    *
    * @param controller A function to create the controller to test
    * @param onwardRoute The route to navigate to on Navigator#nextPage calls
    * @param createView A function to create the expected view
    * @param backgroundData The background user answers data
    * @param invalidFormData A sample of invalid form data
    * @param validFormData A list of valid form data for submission
    * @param expectedUserAnswers A list of the expected [[models.UserAnswers]] after each form submission
    */
  def mapCachingController[A: Format](
    controller: DataRetrievalAction => MapCachingController[A],
    onwardRoute: Call,
    createView: (Form[(String, A)], Request[_]) => String,
    backgroundData: Map[String, JsValue],
    invalidFormData: Map[String, String],
    validFormData: List[Map[String, String]],
    expectedUserAnswers: List[UserAnswers]
  ): Unit = {
    behave like accumulatingCachingController[Map[String, A], (String, A)](
      controller,
      onwardRoute,
      createView,
      backgroundData,
      invalidFormData,
      validFormData,
      expectedUserAnswers
    )
  }

  /**
   * Test the behaviour of a [[controllers.ListEditingController]]
   *
   * @param controller A function to create the controller to test
   * @param onwardRoute The route to navigate to on Navigator#nextPage calls
   * @param createView A function to create the expected view
   * @param backgroundData The background user answers data
   * @param invalidFormData A sample of invalid form data
   * @param validFormData A list of valid form data for submission
   * @param expectedUserAnswers A list of the expected [[models.UserAnswers]] after each form submission
   */
  def listEditingController[A: Format](
                                        controller: DataRetrievalAction => ListEditingController[A],
                                        onwardRoute: Call,
                                        createView: (Form[A], Request[_]) => String,
                                        backgroundData: Map[String, JsValue],
                                        invalidFormData: Map[String, String],
                                        validFormData: List[Map[String, String]],
                                        expectedUserAnswers: List[UserAnswers]
                                      ): Unit = {
    behave like accumulatingCachingController[List[A], A](
      controller,
      onwardRoute,
      createView,
      backgroundData,
      invalidFormData,
      validFormData,
      expectedUserAnswers
    )
  }

  /**
    * Test the behaviour of a [[controllers.AccumulatingCachingController]]
    *
    * @param controller A function to create the controller to test
    * @param onwardRoute The route to navigate to on Navigator#nextPage calls
    * @param createView A function to create the expected view
    * @param backgroundData The background user answers data
    * @param invalidFormData A sample of invalid form data
    * @param validFormData A list of valid form data for submission
    * @param expectedUserAnswers A list of the expected [[models.UserAnswers]] after each form submission
    */
  def accumulatingCachingController[F <: TraversableOnce[A]: Format, A: Format](
    controller: DataRetrievalAction => AccumulatingCachingController[F, A],
    onwardRoute: Call,
    createView: (Form[A], Request[_]) => String,
    backgroundData: Map[String, JsValue],
    invalidFormData: Map[String, String],
    validFormData: List[Map[String, String]],
    expectedUserAnswers: List[UserAnswers]
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

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakePOSTRequestWithCSRF.withFormUrlEncodedBody(validFormData.head.toSeq: _*)

      val controllerWithData = controller(getEmptyCacheMap)
      val result = controllerWithData.onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, backgroundData)))

      val controllerWithData = controller(getRelevantData)
      val postRequest = fakePOSTRequestWithCSRF.withFormUrlEncodedBody(invalidFormData.toSeq: _*)
      val boundForm = controllerWithData.form.bind(invalidFormData)

      val result = controllerWithData.onSubmit(NormalMode)(postRequest)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) shouldBe createView(boundForm, postRequest)
    }

    "accumulate valid data into the user answers" in {
      validFormData.zip(expectedUserAnswers).foreach {
        case (formData, userAnswers) =>
          val postRequest = fakePOSTRequestWithCSRF.withFormUrlEncodedBody(formData.toSeq: _*)
          val cacheConnector = noDataController.dataCacheConnector

          val controllerWithData = controller(new DataRetrievalActionImpl(cacheConnector))
          val result = controllerWithData.onSubmit(NormalMode)(postRequest)

          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(onwardRoute.url)

          await(cacheConnector.fetch(cacheMapId)).map(UserAnswers(_)) shouldBe Some(userAnswers)
      }
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