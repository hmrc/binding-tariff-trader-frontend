package pages

import models.LegalChallenge
import pages.behaviours.PageBehaviours

class LegalChallengeSpec extends PageBehaviours {

  "YourLocation" must {

    beRetrievable[LegalChallenge](LegalChallengePage)

    beSettable[LegalChallenge](LegalChallengePage)

    beRemovable[LegalChallenge](LegalChallengePage)
  }
}
