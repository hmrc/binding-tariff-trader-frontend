package views

import play.api.data.Form
import controllers.routes
import forms.CommodityCodeRulingReferenceFormProvider
import models.NormalMode
import views.behaviours.StringViewBehaviours
import views.html.commodityCodeRulingReference

class CommodityCodeRulingReferenceViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "commodityCodeRulingReference"

  val form = new CommodityCodeRulingReferenceFormProvider()()

  def createView = () => commodityCodeRulingReference(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => commodityCodeRulingReference(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "CommodityCodeRulingReference view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like stringPage(createViewUsingForm, messageKeyPrefix, routes.CommodityCodeRulingReferenceController.onSubmit(NormalMode).url)
  }
}
