package controllers

import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import models.oCase
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import play.api.test.Helpers.{redirectLocation, status, _}
import service.CasesService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class RulingControllerSpec extends ControllerSpecBase with MockitoSugar {

  private val casesService = mock[CasesService]
  private lazy val givenUserDoesntHaveAnEORI = FakeIdentifierAction(None)

  def controller(identifier: IdentifierAction = FakeIdentifierAction) =
    new RulingController(frontendAppConfig, identifier, casesService, messagesApi)


  "Ruling Controller" must {

    "return OK and the correct view for a GET" in {
      given(casesService.getCaseForUser(any[String], any[String])(any[HeaderCarrier]))
            .willReturn(Future.successful(oCase.btiCaseWithDecision))
      val result = controller().viewRuling("a-ruling")(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) must include(oCase.btiCaseWithDecision.reference)
      contentAsString(result) must include(oCase.btiCaseWithDecision.decision.get.bindingCommodityCode)
    }

    "redirect to BeforeYouStart when EORI unavailable" in {

      val result = controller(givenUserDoesntHaveAnEORI).viewRuling("aruling")(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.BeforeYouStartController.onPageLoad().url)
    }
  }
}
