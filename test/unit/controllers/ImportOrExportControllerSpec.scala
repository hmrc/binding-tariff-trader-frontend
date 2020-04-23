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
import forms.ImportOrExportFormProvider
import models.ImportOrExport.{Advice, Export, Import}
import models.{ImportOrExport, NormalMode}
import navigation.Navigator
import pages.ImportOrExportPage
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import views.html.importOrExport

class ImportOrExportControllerSpec extends ControllerSpecBase {

  private val formProvider = new ImportOrExportFormProvider()
  private val form = formProvider()

  private def viewAsString(form: Form[_] = form): String = importOrExport(frontendAppConfig, form, NormalMode)(fakeRequestWithEori, messages).toString

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ImportOrExportController(
      frontendAppConfig,
      FakeDataCacheConnector,
      new Navigator,
      FakeIdentifierAction,
      dataRetrievalAction,
      formProvider,
      cc
    )

  "ImportOrExport Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) shouldBe OK
      contentAsString(result) shouldBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(ImportOrExportPage.toString -> JsString(ImportOrExport.values.head.toString))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) shouldBe viewAsString(form.fill(ImportOrExport.values.head))
    }

    "redirect to the next page when Import is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", Import))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }

    "redirect to the next page when Export is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", Export))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }

    "redirect to the next page when Advice is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", Advice))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.ContactCustomsDutyLiabilityTeamController.onPageLoad().url)
    }


    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) shouldBe viewAsString(boundForm)
    }

  }
}
