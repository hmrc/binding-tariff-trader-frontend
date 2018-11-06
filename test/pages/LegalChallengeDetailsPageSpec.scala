package pages

import pages.behaviours.PageBehaviours


class LegalChallengeDetailsPageSpec extends PageBehaviours {

  "LegalChallengeDetailsPage" must {

    beRetrievable[String](LegalChallengeDetailsPage)

    beSettable[String](LegalChallengeDetailsPage)

    beRemovable[String](LegalChallengeDetailsPage)
  }
}
