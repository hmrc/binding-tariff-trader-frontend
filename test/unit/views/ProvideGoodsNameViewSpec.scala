package unit.views

import controllers.routes
import forms.provideGoodsNameFormProvider
import models.NormalMode
import play.api.data.Form
import views.behaviours.StringViewBehaviours
import views.html.provideGoodsName

class ProvideGoodsNameViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "provideGoodsName"

  val form = new provideGoodsNameFormProvider()()

  def createView = () => provideGoodsName(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => provideGoodsName(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "provideGoodsName view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.ProvideGoodsNameController.onSubmit(NormalMode).url)
  }
}
