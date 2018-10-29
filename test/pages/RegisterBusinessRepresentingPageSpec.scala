package pages

import models.RegisterBusinessRepresenting
import pages.behaviours.PageBehaviours

class RegisterBusinessRepresentingPageSpec extends PageBehaviours {

  "RegisterBusinessRepresentingPage" must {

    beRetrievable[RegisterBusinessRepresenting](RegisterBusinessRepresentingPage)

    beSettable[RegisterBusinessRepresenting](RegisterBusinessRepresentingPage)

    beRemovable[RegisterBusinessRepresenting](RegisterBusinessRepresentingPage)
  }
}
