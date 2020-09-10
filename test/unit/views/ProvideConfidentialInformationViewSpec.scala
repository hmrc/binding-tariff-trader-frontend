package unit.views

import controllers.routes
import forms.ProvideConfidentialInformationFormProvider
import models.NormalMode
import play.api.data.Form
import play.inject.Injector
import views.behaviours.StringViewBehaviours
import views.html.provideConfidentialInformation

class ProvideConfidentialInformationViewSpec extends StringViewBehaviours {

  val provideConfidentialInformation = injector.instanceOf[provideConfidentialInformation]

  val messageKeyPrefix = "provideConfidentialInformation"

  val form = new ProvideConfidentialInformationFormProvider()()

  val fakeGETRequest = fakeGETRequestWithCSRF

  val goodsName = "shoos"

  def createView = () => provideConfidentialInformation(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  def createViewUsingForm = (form: Form[String]) => provideConfidentialInformation(frontendAppConfig, form, goodsName, NormalMode)(fakeGETRequest, messages)

  "ProvideConfidentialInformation view" must {
    behave like normalPage(createView, messageKeyPrefix, goodsName)()

    behave like pageWithBackLink(createView)

    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.ProvideConfidentialInformationController.onSubmit(NormalMode).url)
  }
}
