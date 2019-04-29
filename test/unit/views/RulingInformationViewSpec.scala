package views

import models.{Case, oCase}
import play.twirl.api.{Html, HtmlFormat}
import utils.Dates
import views.behaviours.ViewBehaviours
import views.html.ruling_information

class RulingInformationViewSpec extends ViewBehaviours {

  private def createView(c: Case): () => HtmlFormat.Appendable = () => ruling_information(frontendAppConfig, c)(fakeRequest, messages)

  private val rulingCase = oCase.btiCaseWithDecision
  private val holder = rulingCase.application.holder
  private val ruling = rulingCase.decision.getOrElse(throw new Exception("Bad test data"))


  "Ruling Information View" must {

    "show the expected element values" in {

      val doc = asDocument(createView(rulingCase).apply())
      assertContainsText(doc,"GB"+rulingCase.reference)
      assertContainsText(doc,rulingCase.application.holder.businessName)
      assertContainsText(doc,rulingCase.application.holder.businessName)
      assertContainsText(doc,Dates.format(ruling.effectiveStartDate))
      assertContainsText(doc,Dates.format(ruling.effectiveEndDate))

      assertContainsText(doc,messages("rulingInformation.CommodityIntro")+" "+ ruling.bindingCommodityCode+".")

      assertContainsText(doc,ruling.explanation.getOrElse("NeverMatch"))

      assertContainsText(doc, messages("rulingInformation.CertificateText"))
      assertContainsText(doc, messages("rulingInformation.SamplesText"))
      assertContainsText(doc, messages("rulingInformation.AppealsText"))
    }
  }
}
