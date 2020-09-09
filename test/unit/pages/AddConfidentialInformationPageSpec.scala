package unit.pages

import pages.AddConfidentialInformationPage
import pages.behaviours.PageBehaviours

class AddConfidentialInformationPageSpec extends PageBehaviours {

  "AddConfidentialInformationPage" must {

    beRetrievable[Boolean](AddConfidentialInformationPage)

    beSettable[Boolean](AddConfidentialInformationPage)

    beRemovable[Boolean](AddConfidentialInformationPage)
  }
}
