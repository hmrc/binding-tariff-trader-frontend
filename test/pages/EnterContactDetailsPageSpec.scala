package pages

import models.EnterContactDetails
import pages.behaviours.PageBehaviours

class EnterContactDetailsPageSpec extends PageBehaviours {

  "EnterContactDetailsPage" must {

    beRetrievable[EnterContactDetails](EnterContactDetailsPage)

    beSettable[EnterContactDetails](EnterContactDetailsPage)

    beRemovable[EnterContactDetails](EnterContactDetailsPage)
  }
}
