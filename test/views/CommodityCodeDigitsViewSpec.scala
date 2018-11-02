package views

import play.api.data.Form
import controllers.routes
import forms.CommodityCodeDigitsFormProvider
import models.NormalMode
import views.behaviours.StringViewBehaviours
import views.html.commodityCodeDigits

class CommodityCodeDigitsViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "commodityCodeDigits"

  val form = new CommodityCodeDigitsFormProvider()()

  def createView = () => commodityCodeDigits(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => commodityCodeDigits(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "CommodityCodeDigits view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.CommodityCodeDigitsController.onSubmit(NormalMode).url)
  }
}
